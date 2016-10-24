package net.flatmap.vscode.languageserver

/**
  * @param title A short title like 'Retry', 'Open Log' etc.
  */
case class MessageActionItem(title: String)

sealed trait MessageType
object MessageType {
  /** An error message. */
  object Error extends MessageType
  /** A warning message */
  object Warning extends MessageType
  /** An information message. */
  object Info extends MessageType
  /** A log message. */
  object Log extends MessageType
}