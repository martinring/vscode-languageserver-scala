package net.flatmap.vscode.languageserver

import io.circe.Json

import scala.concurrent.Future

object ClientProtocol {
  trait Window {
    def showMessage(messageType: MessageType, message: String): Unit
    def showMessageRequest(messageType: MessageType, message: String, actions: Option[Array[MessageActionItem]]): Future[MessageActionItem]
    def logMessage(messageType: MessageType, message: String): Unit
  }

  trait Telemetry {
    def event(params: Json)
  }

  trait TextDocument {
    def publishDiagnostics(uri: String, diagnostics: Array[Diagnostic]): Unit
  }
}

trait ClientProtocol {
  val window: ClientProtocol.Window
  val telemetry: ClientProtocol.Telemetry
  val textDocument: ClientProtocol.TextDocument
}