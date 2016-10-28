package net.flatmap.vscode.languageserver

import net.flatmap.jsonrpc.JsonRPC

sealed trait Trace
object Trace {
  case object Off extends Trace
  case object Messages extends Trace
  case object Verbose extends Trace
}

sealed trait Tracing {
  @JsonRPC.Named("$/setTraceNotification")
  def setTrace(value: Trace): Unit
  @JsonRPC.Named("$/logTraceNotification")
  def logTrace(message: String, verbose: Option[String]): Unit
}