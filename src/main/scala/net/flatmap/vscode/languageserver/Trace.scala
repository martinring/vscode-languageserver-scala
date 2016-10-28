package net.flatmap.vscode.languageserver

sealed abstract class Trace(value: String)
object Trace {
  case object Off extends Trace("off")
  case object Messages extends Trace("messages")
  case object Verbose extends Trace("verbose")
}