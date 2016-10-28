package net.flatmap.vscode.languageserver

/**
  * MarkedString can be used to render human readable text. It is either a markdown string
  * or a code-block that provides a language and a code snippet. Note that
  * markdown strings will be sanitized - that means html will be escaped.
  */
case class MarkedString(value: String, language: Option[String] = None)

object MarkedString {
  // escape markdown syntax tokens: http://daringfireball.net/projects/markdown/syntax#backslash
  private val markdownSyntaxTokens =
    "[\\\\`\\*_\\{\\}\\[\\]\\(\\)#\\+\\-\\.!]".r

  /**
    * Creates a marked string from plain text.
    *
    * @param plainText The plain text.
    */
  def fromPlainText(plainText: String) = MarkedString(
    markdownSyntaxTokens.replaceAllIn(plainText,"\\\\$0")
  )
}