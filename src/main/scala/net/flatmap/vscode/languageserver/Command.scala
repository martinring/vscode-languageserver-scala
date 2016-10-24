package net.flatmap.vscode.languageserver

import io.circe.Json

/**
  * Represents a reference to a command. Provides a title which will be used
  * to represent a command in the UI. Commands are identitifed using a string
  * identifier and the protocol currently doesn't specify a set of well known
  * commands. So executing a command requires some tool extension code.
  *
  * @param title     Title of the command, like `save`.
  * @param command   The identifier of the actual command handler.
  * @param arguments Arguments that the command handler should be invoked with.
  */
case class Command(title: String,
                   command: String,
                   arguments: Option[Seq[Json]] = None)