package net.flatmap.vscode.languageserver

/**
  * The result of a hover request.
  * @param contents The hover's content
  * @param range    An optional range is a range inside a text document
  *                 that is used to visualize a hover, e.g. by changing the
  *                 background color.
  */
case class Hover(contents: Seq[MarkedString], range: Option[Range])