package net.flatmap.vscode.languageserver

case class ClientCapabilities(
  willSaveTextDocumentNotification: Option[Boolean] = None,
  willSaveTextDocumentProvider: Option[Boolean] = None
)