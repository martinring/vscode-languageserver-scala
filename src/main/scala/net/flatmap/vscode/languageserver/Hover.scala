package net.flatmap.vscode.languageserver

/**
  *  * The marked string is rendered:
  * - as markdown if it is represented as a string
  * - as code block of the given langauge if it is represented as a pair of a
  *   language and a value
  *
  * The pair of a language and a value is an equivalent to markdown:
  * ```${language}
  * ${value}
  * ```
  */
case class MarkedString(value: String, language: Option[String] = None)

/**
  * The result of a hover request.
  * @param contents The hover's content
  * @param range    An optional range is a range inside a text document
  *                 that is used to visualize a hover, e.g. by changing the
  *                 background color.
  */
case class Hover(contents: Seq[MarkedString], range: Option[Range])