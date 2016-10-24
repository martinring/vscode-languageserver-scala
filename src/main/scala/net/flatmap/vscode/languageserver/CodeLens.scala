package net.flatmap.vscode.languageserver

import io.circe.Json


/**
  * A code lens represents a command that should be shown along with source
  * text, like the number of references, a way to run tests, etc.
  *
  * A code lens is _unresolved_ when no command is associated to it. For
  * performance reasons the creation of a code lens and resolving should be
  * done in two stages.
  *
  * @param range   The range in which this code lens is valid. Should only
  *                span a single line.
  * @param command The command this code lens represents.
  * @param data    A data entry field that is preserved on a code lens item
  *                between a code lens and a code lens resolve request.
  */
case class CodeLens(range: Range, command: Option[Command], data: Option[Json])