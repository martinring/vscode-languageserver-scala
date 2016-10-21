package net.flatmap.vscode.languageserver

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Partition}
import io.circe.Json
import net.flatmap.jsonrpc.{Id, Remote, RequestMessage, Response}

import scala.concurrent.Future

object LanguageClient {
  val messageFlow: Flow[Response,RequestMessage,LanguageClient] = {
    val window = Remote[LanguageClient.Window](Id.discriminated(3,0))
      .map(_.prefixed("window/"))
    val telemetry = Remote[LanguageClient.Telemetry](Id.discriminated(3,1))
      .map(_.prefixed("telemetry/"))
    val textDocument = Remote[LanguageClient.TextDocument](Id.discriminated(3,2))
      .map(_.prefixed("textDocument/"))

    Flow.fromGraph(
      GraphDSL.create(window,telemetry,textDocument)(LanguageClient.apply) {
        implicit builder => (window,telemetry,textDocument) =>
          import GraphDSL.Implicits._
          val in = builder.add(Partition[Response](3,
            ((x: Response) => x.id) andThen Id.discriminator(3)))
          val out = builder.add(Merge[RequestMessage](3))

          in ~> window       ~> out
          in ~> telemetry    ~> out
          in ~> textDocument ~> out

          FlowShape(in.in,out.out)
      }
    )
  }


  trait Window {
    /**
      * The show message notification is sent from a server to a client to
      * ask the client to display a particular message in the user interface.
      * @param messageType The message type.
      * @param message     The actual message.
      */
    def showMessage(messageType: MessageType, message: String): Unit

    /**
      * The show message request is sent from a server to a client to ask
      * the client to display a particular message in the user interface.
      * In addition to the show message notification the request allows to
      * pass actions and to wait for an answer from the client.
      * @param messageType The message type.
      * @param message     The actual message
      * @param actions     The message action items to present
      * @return
      */
    def showMessageRequest(messageType: MessageType,
                           message: String,
                           actions: Option[Array[MessageActionItem]]):
      Future[MessageActionItem]

    /**
      * The log message notification is sent from the server to the client
      * to ask the client to log a particular message.
      * @param messageType The message type.
      * @param message     The actual message.
      */
    def logMessage(messageType: MessageType, message: String): Unit
  }

  trait Telemetry {
    /**
      * The telemetry notification is sent from the server to the client to
      * ask the client to log a telemetry event.
      * @param params payload
      */
    def event(params: Json)
  }

  trait TextDocument {
    /**
      * Diagnostics notification are sent from the server to the client to
      * signal results of validation runs.
      * @param uri         The URI for which diagnostic information is
      *                    reported.
      * @param diagnostics An array of diagnostic information items
      */
    def publishDiagnostics(uri: String, diagnostics: Array[Diagnostic]): Unit
  }
}

case class LanguageClient(
  window: LanguageClient.Window,
  telemetry: LanguageClient.Telemetry,
  textDocument: LanguageClient.TextDocument
)
