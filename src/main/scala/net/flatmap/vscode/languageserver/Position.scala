package net.flatmap.vscode.languageserver

/**
  * Position in a text document expressed as zero-based line and character
  * offset. A position is between two characters like an 'insert' cursor in a
  * editor.
  *
  * @param line      Line position in a document (zero-based).
  * @param character Character offset on a line in a document (zero-based).
  */
case class Position(line: Int, character: Int) extends Ordered[Position] {
  def compare(that: Position): Int = Position.ordering.compare(this,that)

  override def toString: String =
    s"($line,$character)"
}

object Position {
  implicit val ordering: Ordering[Position] =
    Ordering.by(p => (p.line,p.character))
}