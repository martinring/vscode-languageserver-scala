package net.flatmap.vscode.languageserver

case class ClientCapabilities(
  willSaveTextDocumentNotification: Boolean = false,
  willSaveTextDocumentProvider: Boolean = false
)