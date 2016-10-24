package net.flatmap.vscode.languageserver


sealed trait DiagnosticCode
object DiagnosticCode {
  case class Int(code: scala.Int) extends DiagnosticCode
  case class String(code: java.lang.String) extends DiagnosticCode
}

sealed trait DiagnosticSeverity
object DiagnosticSeverity {
  /** Reports an error. */
  object Error extends DiagnosticSeverity
  /** Reports a warning. */
  object Warning extends DiagnosticSeverity
  /** Reports an information. */
  object Information extends DiagnosticSeverity
  /** Reports a hint. */
  object Hint extends DiagnosticSeverity
}

/**
  * Represents a diagnostic, such as a compiler error or warning. Diagnostic
  * objects are only valid in the scope of a resource.
  *
  * @param range    The range at which the message applies.
  * @param severity The diagnostic's severity. Can be omitted. If omitted it
  *                 is up to the client to interpret diagnostics as error,
  *                 warning, info or hint.
  * @param code     The diagnostic's code. Can be omitted.
  * @param source   A human-readable string describing the source of this
  *                 diagnostic, e.g. 'typescript' or 'super lint'.
  * @param message  The diagnostic's message.
  */
case class Diagnostic(range: Range,
                      severity: Option[DiagnosticSeverity],
                      code: Option[DiagnosticCode],
                      source: Option[String],
                      message: String)