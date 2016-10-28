package net.flatmap.vscode.languageserver

import io.circe.JsonNumber

sealed trait FormattingOption
object FormattingOption {
  case class Boolean(value: scala.Boolean) extends FormattingOption
  case class Number(value: JsonNumber) extends FormattingOption
  case class String(value: java.lang.String) extends FormattingOption
}

/**
  * Value-object describing what options formatting should use.
  * @param tabSize      Size of a tab in spaces.
  * @param insertSpaces Prefer spaces over tabs.
  */
case class FormattingOptions(
  tabSize: Int,
  insertSpaces: Boolean,
  furtherProperties: Map[String,FormattingOption])