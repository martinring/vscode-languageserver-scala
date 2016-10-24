package net.flatmap.vscode.languageserver


/**
  * @param includeDeclaration Include the declaration of the current symbol.
  */
case class ReferenceContext(includeDeclaration: Boolean)