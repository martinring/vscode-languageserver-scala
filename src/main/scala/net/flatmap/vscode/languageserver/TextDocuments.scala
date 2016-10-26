package net.flatmap.vscode.languageserver

import java.net.URI

import akka.NotUsed
import akka.actor.ActorRefFactory
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import akka.stream.scaladsl.Source

/*private class TextDocumentActor extends ActorPublisher[TextDocumentItem] {
  def receive: Receive = {
    case item: TextDocumentItem =>
      context.become(initialized(item, totalDemand > 0))
      if (totalDemand > 0) onNext(item)
  }

  def initialized(item: TextDocumentItem, delivered: Boolean = false): Receive = {
    case (version: Int, changes: Seq[TextDocumentContentChangeEvent]) =>
      for (last <- changes.lastOption) {
        val next = item.copy(text = last.text, version = version)
        context.become(initialized(next,totalDemand > 0))
        if (totalDemand > 0) onNext(next)
      }
    case Request(_) =>
      if (!delivered) {
        onNext(item)
        context.become(initialized(item,true))
      }
    case Cancel => context.stop(self)
  }
}

private class TextDocumentsActor extends ActorPublisher[Source[TextDocumentItem,NotUsed]] {
  def receive: Receive = state(Map.empty)

  def state(items: Map[URI,TextDocumentItem]): Receive = {
    case _ =>
  }
}

trait TextDocuments extends LanguageServer {
  override def textDocumentSyncKind: TextDocumentSyncKind = TextDocumentSyncKind.None
  def actorFactory: ActorRefFactory
  def textDocuments: Source[Source[TextDocumentItem,NotUsed],NotUsed] =
}*/