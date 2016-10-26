package net.flatmap.vscode.languageserver

/**
  * A symbol kind.
  */
sealed trait SymbolKind
object SymbolKind {
  case object File extends SymbolKind
  case object Module extends SymbolKind
  case object Namespace extends SymbolKind
  case object Package extends SymbolKind
  case object Class extends SymbolKind
  case object Method extends SymbolKind
  case object Property extends SymbolKind
  case object Field extends SymbolKind
  case object Constructor extends SymbolKind
  case object Enum extends SymbolKind
  case object Interface extends SymbolKind
  case object Function extends SymbolKind
  case object Variable extends SymbolKind
  case object Constant extends SymbolKind
  case object String extends SymbolKind
  case object Number extends SymbolKind
  case object Boolean extends SymbolKind
  case object Array extends SymbolKind
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