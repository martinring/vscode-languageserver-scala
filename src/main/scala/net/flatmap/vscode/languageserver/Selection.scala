package net.flatmap.vscode.languageserver

/**
  * Represents a text selection in an editor.
  *
  * @param anchor The position at which the selection starts.
  *               This position might be before or after [active](#Selection.active).
  * @param active The position of the cursor.
  *               This position might be before or after [anchor](#Selection.anchor).
  */
case class Selection(anchor: Position, active: Position)
  extends Range(Position.ordering.min(anchor,active),
                Position.ordering.max(anchor,active)) {
  /**
    * A selection is reversed if [active](#Selection.active).isBefore([anchor](#Selection.anchor)).
    */
  def isReversed = active < anchor
}