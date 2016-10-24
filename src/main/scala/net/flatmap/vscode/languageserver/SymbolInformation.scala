package net.flatmap.vscode.languageserver

/**
  * A symbol kind.
  */
sealed trait SymbolKind
object SymbolKind {
  object File extends SymbolKind
  object Module extends SymbolKind
  object Namespace extends SymbolKind
  object Package extends SymbolKind
  object Class extends SymbolKind
  object Method extends SymbolKind
  object Property extends SymbolKind
  object Field extends SymbolKind
  object Constructor extends SymbolKind
  object Enum extends SymbolKind
  object Interface extends SymbolKind
  object Function extends SymbolKind
  object Variable extends SymbolKind
  object Constant extends SymbolKind
  object String extends SymbolKind
  object Number extends SymbolKind
  object Boolean extends SymbolKind
  object Array extends SymbolKind
}

/**
  * Represents information about programming constructs like variables, classes,
  * interfaces etc.
  *
  * @param name          The name of this symbol.
  * @param kind          The kind of this symbol.
  * @param location      The location of this symbol.
  * @param containerName The name of the symbol containing this symbol.
  */
case class SymbolInformation(name: String,
                             kind: SymbolKind,
                             location: Location,
                             containerName: Option[String])