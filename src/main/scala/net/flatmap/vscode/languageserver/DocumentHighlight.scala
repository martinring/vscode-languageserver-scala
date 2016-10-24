package net.flatmap.vscode.languageserver

/**
  * A document highlight kind.
  */
sealed trait DocumentHighlightKind
object DocumentHighlightKind {
  /** A textual occurrance. */
  object Text extends DocumentHighlightKind

  /** Read-access of a symbol, like reading a variable. */
  object Read extends DocumentHighlightKind

  /** Write-access of a symbol, like writing to a variable. */
  object Write extends DocumentHighlightKind
}

/**
  * A document highlight is a range inside a text document which deserves
  * special attention. Usually a document highlight is visualized by changing
  * the background color of its range.
  * @param range The range this highlight applies to.
  * @param kind  The highlight kind, default is DocumentHighlightKind.Text.
  */
case class DocumentHighlight(range: Range,
                             kind: Option[DocumentHighlightKind])
