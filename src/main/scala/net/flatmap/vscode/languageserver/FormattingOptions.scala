package net.flatmap.vscode.languageserver


/**
  * Value-object describing what options formatting should use.
  * @param tabSize      Size of a tab in spaces.
  * @param insertSpaces Prefer spaces over tabs.
  */
case class FormattingOptions(tabSize: Int, insertSpaces: Boolean)