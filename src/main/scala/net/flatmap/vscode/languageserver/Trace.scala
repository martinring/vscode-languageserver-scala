package net.flatmap.vscode.languageserver

sealed trait Trace
object Trace {
  case object Off extends Trace
  case object Messages extends Trace
  case object Verbose extends Trace
}