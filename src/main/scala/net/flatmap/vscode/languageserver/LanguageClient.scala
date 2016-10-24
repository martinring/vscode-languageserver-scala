package net.flatmap.vscode.languageserver

import io.circe.Json
import net.flatmap.jsonrpc._

import scala.concurrent.Future

object LanguageClient {
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
                           actions: Option[Seq[MessageActionItem]]):
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
    def event(params: Json): Unit
  }

  trait TextDocument {
    /**
      * Diagnostics notification are sent from the server to the client to
      * signal results of validation runs.
      * @param uri         The URI for which diagnostic information is
      *                    reported.
      * @param diagnostics An array of diagnostic information items
      */
    def publishDiagnostics(uri: String, diagnostics: Seq[Diagnostic]): Unit
  }
}

trait LanguageClient {
  @JsonRPCNamespace("window/")
  def window: LanguageClient.Window

  @JsonRPCNamespace("telemetry/")
  def telemetry: LanguageClient.Telemetry

  @JsonRPCNamespace("textDocument/")
  def textDocument: LanguageClient.TextDocument
}
