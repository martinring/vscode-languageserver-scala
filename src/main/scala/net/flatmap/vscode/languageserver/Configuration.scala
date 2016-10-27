package net.flatmap.vscode.languageserver
import akka.NotUsed
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import cats.data.Xor
import io.circe.{Decoder, Json}

trait Configuration[T] extends LanguageServer {
  private var configQueues = Set.empty[SourceQueueWithComplete[Json]]

  def configChanges(implicit decoder: Decoder[T]): Source[T,NotUsed] =
    Source.queue[Json](1024,OverflowStrategy.dropTail).mapMaterializedValue {
      case queue =>
        configQueues += queue
        NotUsed
    }.map(decoder.decodeJson).collect {
      case Xor.Right(t) => t
    }

  override def didChangeConfiguration(settings: Json): Unit = {
    configQueues.foreach(_.offer(settings))
  }
}
