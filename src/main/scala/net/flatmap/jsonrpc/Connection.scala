package net.flatmap.jsonrpc

import java.io.{InputStream, OutputStream}
import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl._
import akka.util.ByteString
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.annotation.tailrec

sealed trait FramingState { def append(s: ByteString): FramingState }
case class IncompleteHeader(part: ByteString) extends FramingState {
  def append(x: ByteString) = copy(part = part ++ x)
}
case class IncompleteContent(contentLength: Int, part: ByteString) extends FramingState {
  def append(x: ByteString) = copy(part = part ++ x)
}

object FramingState {
  @tailrec
  def reduceStream(messages: collection.immutable.Seq[String], connectionState: FramingState): (collection.immutable.Seq[String], FramingState) = connectionState match {
    case IncompleteHeader(part) =>
      val str = part.decodeString(StandardCharsets.US_ASCII)
      val pos = str.indexOf("\r\n\r\n")
      if (pos > -1) {
        val parts = str.take(pos).split("\r\n")
        val contentLength = parts.find(_.startsWith("Content-Length: ")).map { x =>
          x.drop(16).toInt
        }.get
        val remaining = part.drop(pos + 4)
        reduceStream(messages,IncompleteContent(contentLength, remaining))
      } else {
        (collection.immutable.Seq.empty, IncompleteHeader(part))
      }
    case IncompleteContent(contentLength,part) =>
      if (part.length < contentLength)
        (messages, IncompleteContent(contentLength,part))
      else if (part.length == contentLength)
        (messages :+ part.decodeString(StandardCharsets.UTF_8), IncompleteHeader(ByteString.empty))
      else {
        val (content,next) = part.splitAt(contentLength)
        reduceStream(messages :+ content.decodeString(StandardCharsets.UTF_8), IncompleteHeader(next))
      }
  }

}

sealed trait Connection {
  def run(listener: (Message) => Unit)(implicit materializer: Materializer)
  def send(message: Message)
}

object Connection {
  def encodeMessage(message: Message): Array[Byte] = {
    val bytes = message.asJson.noSpaces.getBytes(StandardCharsets.UTF_8)
    val length = bytes.length
    s"Content-Length: $length\r\n\r\n".getBytes ++ bytes
  }


  def create(in: InputStream, out: OutputStream) = new Connection {
    val inStream = StreamConverters.fromInputStream(() => in)

    val deframer =
      Flow[ByteString].scan[(collection.immutable.Iterable[String],FramingState)]((collection.immutable.Seq.empty,IncompleteHeader(ByteString.empty))) {
        case ((msgs, state), next) =>
          FramingState.reduceStream(collection.immutable.Seq.empty,state.append(next))
      }.map(_._1).flatMapConcat(x => Source.apply(x))

    val deserializer =
      Flow[String].map { x =>
        decode[Message](x).toOption.get
      }

    val serializer =
      Flow[Message].map { x =>
        ByteString.fromString(x.asJson.noSpaces,StandardCharsets.UTF_8.displayName())
      }

    def run(listener: Message => Unit)(implicit materializer: Materializer) = {
      val handler = Sink.foreach[Message](listener)
      (inStream via deframer via deserializer to handler).run()
    }

    def send(msg: Message) = out.write(encodeMessage(msg))
  }
}