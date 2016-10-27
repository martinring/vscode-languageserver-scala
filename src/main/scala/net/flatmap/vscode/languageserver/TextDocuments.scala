package net.flatmap.vscode.languageserver

import java.net.URI

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.reactivestreams.{Publisher, Subscriber, Subscription}

trait TextDocuments extends LanguageServer {
  override def textDocumentSyncKind: TextDocumentSyncKind = TextDocumentSyncKind.None

  def textDocuments: Source[(URI,Source[TextDocumentItem,NotUsed]),NotUsed] =
    Source.fromPublisher(DocumentPublisher)

  private class DocumentPublisher(var state: TextDocumentItem) extends Publisher[TextDocumentItem] {
    var subscribers =
      Map.empty[Subscriber[_ >: TextDocumentItem], (Long,Boolean)]

    def subscribe(s: Subscriber[_ >: TextDocumentItem]): Unit = {
      if (closed) {
        s.onSubscribe(new Subscription {
          def cancel(): Unit = ()
          def request(n: Long): Unit = ()
        })
        s.onComplete()
      } else {
        subscribers += s -> (0,false)
        s.onSubscribe(new Subscription {
          def cancel() =
            if (subscribers.contains(s)) {
              subscribers -= s
              s.onComplete()
            }

          def request(n: Long) =
            for {
              (m, delivered) <- subscribers.get(s)
            } {
              subscribers += s -> (
                if (!delivered) {
                  s.onNext(state)
                  (m + n - 1, true)
                } else (m + n, true)
                )
            }
        })
      }
    }

    def change(textDocument: VersionedTextDocumentIdentifier,
               contentChanges: Seq[TextDocumentContentChangeEvent]): Unit = {
      for {
        last <- contentChanges.lastOption
      } {
        state = state.copy(
          text = last.text,
          version = textDocument.version
        )
        subscribers = (subscribers.map {
          case (s,(0l,_)) => (s,(0l,false))
          case (s,(n,_)) =>
            s.onNext(state)
            (s,(n-1,true))
        }).toMap
      }
    }

    var closed = false

    def close() = {
      closed = true
      subscribers.keys.foreach(_.onComplete())
      subscribers = Map.empty
    }
  }

  private object DocumentPublisher extends Publisher[(URI,Source[TextDocumentItem,NotUsed])] {
    var documents = Map.empty[URI,DocumentPublisher]

    var subscribers =
      Map.empty[Subscriber[_ >: (URI,Source[TextDocumentItem,NotUsed])],
                (Long,Seq[(URI,Source[TextDocumentItem,NotUsed])])]

    def subscribe(s: Subscriber[_ >: (URI,Source[TextDocumentItem,NotUsed])]): Unit = {
      subscribers += s -> (0,Seq.empty)
      s.onSubscribe(new Subscription {
        def cancel() =
          if (subscribers.contains(s)) {
            subscribers -= s
            s.onComplete()
          }
        def request(n: Long) =
          for {
            (m,buf) <- subscribers.get(s)
          } {
            subscribers += s -> (if (buf.nonEmpty) {
              buf.take((n + m).toInt).foreach(s.onNext)
              (Math.max(0, m + n - (buf.length)), buf.drop((n + m).toInt))
            }
            else (m + n, buf))
          }
      })
    }

    def open(textDocumentItem: TextDocumentItem) = {
      val pub = new DocumentPublisher(textDocumentItem)
      val src = textDocumentItem.uri -> Source.fromPublisher(pub)
      documents += textDocumentItem.uri -> pub
      subscribers = subscribers.map {
        case (s,(0l,buf)) => (s,(0l,buf :+ src))
        case (s,(n,buf)) =>
          val nbuf = buf :+ src
          nbuf.take(n.toInt).foreach(s.onNext)
          (s,(Math.max(0,n - nbuf.length), nbuf.drop(n.toInt)))
      }.toMap
    }

    def change(textDocument: VersionedTextDocumentIdentifier,
               contentChanges: Seq[TextDocumentContentChangeEvent]) = {
      documents.get(textDocument.uri).foreach { publisher =>
        publisher.change(textDocument,contentChanges)
      }
    }

    def close(uri: URI) = {
      documents.get(uri).foreach(_.close())
      documents -= uri
    }
  }

  override def didOpen(textDocument: TextDocumentItem): Unit = {
    DocumentPublisher.open(textDocument)
  }

  override def didChange(textDocument: VersionedTextDocumentIdentifier,
                contentChanges: Seq[TextDocumentContentChangeEvent]): Unit = {
    DocumentPublisher.change(textDocument,contentChanges)
  }

  override def didSave(textDocument: TextDocumentIdentifier): Unit = {
    // TODO: Signal Save events
  }

  override def didClose(textDocument: TextDocumentIdentifier): Unit = {
    DocumentPublisher.close(textDocument.uri)
  }
}