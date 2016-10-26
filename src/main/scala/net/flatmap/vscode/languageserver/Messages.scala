package net.flatmap.vscode.languageserver

/**
  * @param title A short title like 'Retry', 'Open Log' etc.
  */
case class MessageActionItem(title: String)

sealed trait MessageType
object MessageType {
  /** An error message. */
  case object Error extends MessageType
  /** A warning message */
  case object Warning extends MessageType
  /** An information message. */
  case object Info extends MessageType
  /** A log message. */
  case object Log extends MessageType
}