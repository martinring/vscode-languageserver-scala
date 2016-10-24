package net.flatmap.vscode.languageserver

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, FlowShape, IOResult}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Merge, Sink, Source, StreamConverters}
import akka.util.ByteString
import io.circe.Json
import net.flatmap.jsonrpc
import net.flatmap.jsonrpc._
import net.flatmap.jsonrpc.util.TypePartition

import scala.concurrent.{Future, Promise}

/**
  * Created by martin on 21/10/2016.
  */
object Example extends App{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher = system.dispatcher

  val msg = Promise[Message]
  val msg2 = Promise[Message]

  val msgs =
    Source.fromFuture(msg.future) ++
    Source.fromFuture(msg2.future)

  val in = msgs
    .via(jsonrpc.Codec.encoder)
    .via(Flow.fromFunction(jsonrpc.Codec.jsonPrinter.pretty))
    .via(Framing.byteStringFramer)

  import Codec._

  val client = Promise[LanguageClient]
  val server = new ExampleServer(client.future)
  val local = Local[LanguageServer](server)
  val remote = Remote[LanguageClient](Id.standard)

  val out = Sink.foreach[Message](println)

  val connection = Connection.create(local,remote)

  val handler = GraphDSL.create(local, remote) (Keep.right) { implicit b =>
    (local, remote) =>
      import GraphDSL.Implicits._

      val partition = b.add(TypePartition[Message,RequestMessage,Response])
      val merge     = b.add(Merge[Message](2))
      partition.out1 ~> local  ~> merge
      partition.out2 ~> remote ~> merge
      FlowShape(partition.in, merge.out)
  }

  val interface = msgs.viaMat(handler)(Keep.right).to(out).run()

  client.success(interface)

  msg.success(Request(
    Id.Long(0),
    "initialize",
    NamedParameters(Map(
      "capabilities" -> Json.obj()
    ))
  ))

  readLine()

  system.terminate()
}
