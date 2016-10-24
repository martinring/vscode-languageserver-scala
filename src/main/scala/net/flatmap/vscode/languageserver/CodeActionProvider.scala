package net.flatmap.vscode.languageserver

/**
  * Contains additional diagnostic information about the context in which a
  * code action is run.
  * @param diagnostics An array of diagnostics.
  */
case class CodeActionContext(diagnostics: Seq[Diagnostic])
