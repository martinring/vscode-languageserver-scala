package net.flatmap.vscode.languageserver

/**
  * A textual edit applicable to a text document.
  *
  * @param range   The range of the text document to be manipulated. To
  *                insert text into a document create a range where start ===
  *                end.
  * @param newText The string to be inserted. For delete operations use an
  *                empty string.
  */
case class TextEdit(range: Range, newText: String)

object TextEdit {
  /**
    * Utility to create a replace edit.
    *
    * @param range A range.
    * @param newText A string.
    * @return A new text edit object.
    */
  def replace(range: Range, newText: String): TextEdit =
    TextEdit(range,newText)

  /**
    * Utility to create an insert edit.
    *
    * @param position A position, will become an empty range.
    * @param newText A string.
    * @return A new text edit object.
    */
  def insert(position: Position, newText: String): TextEdit =
    TextEdit(Range(position,position),newText)

  /**
    * Utility to create a delete edit.
    *
    * @param range A range.
    * @return A new text edit object.
    */
  def delete(range: Range): TextEdit =
    TextEdit(range,"")
}