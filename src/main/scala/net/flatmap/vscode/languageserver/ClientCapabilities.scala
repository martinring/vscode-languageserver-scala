package net.flatmap.vscode.languageserver

sealed trait ClientCapabilities
object ClientCapabilities {
  object Default extends ClientCapabilities
}
