package net.flatmap.vscode.languageserver

import cats.data.Xor
import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.semiauto._

/**
  * Position in a text document expressed as zero-based line and character
  * offset. A position is between two characters like an 'insert' cursor in a
  * editor.
  *
  * @param line      Line position in a document (zero-based).
  * @param character Character offset on a line in a document (zero-based).
  */
case class Position(line: Int, character: Int)

/**
  * A range in a text document expressed as (zero-based) start and end positions.
  * A range is comparable to a selection in an editor. Therefore the end position is exclusive.
  *
  * @param start The range's start position.
  * @param end   The range's end position.
  */
case class Range(start: Position, end: Position)

/**
  * Represents a location inside a resource, such as a line inside a text file.
  *
  * @param uri
  * @param range
  */
case class Location(uri: String, range: Range)

/**
  * Represents a diagnostic, such as a compiler error or warning. Diagnostic objects are only valid in the scope of a resource.
  *
  * @param range    The range at which the message applies.
  * @param severity The diagnostic's severity. Can be omitted. If omitted it is up to the client to interpret diagnostics as error, warning, info or hint.
  * @param code     The diagnostic's code. Can be omitted.
  * @param source   A human-readable string describing the source of this diagnostic, e.g. 'typescript' or 'super lint'.
  * @param message  The diagnostic's message.
  */
case class Diagnostic(range: Range,
                      severity: Option[Int],
                      code: Option[Either[Int, String]],
                      source: Option[String],
                      message: String)

sealed trait DiagnosticSeverity

object DiagnosticSeverity {
  object Error extends DiagnosticSeverity
  object Warning extends DiagnosticSeverity
  object Information extends DiagnosticSeverity
  object Hint extends DiagnosticSeverity
}

/**
  * Represents a reference to a command. Provides a title which will be used to represent a command in the UI. Commands are identitifed using a string identifier and the protocol currently doesn't specify a set of well known commands. So executing a command requires some tool extension code.
  *
  * @param title     Title of the command, like `save`.
  * @param command   The identifier of the actual command handler.
  * @param arguments Arguments that the command handler should be invoked with.
  */
case class Command(title: String, command: String, arguments: Option[Array[Json]])

/**
  * A textual edit applicable to a text document.
  *
  * @param range   The range of the text document to be manipulated. To insert text into a document create a range where start === end.
  * @param newText The string to be inserted. For delete operations use an empty string.
  */
case class TextEdit(range: Range, newText: String)

/**
  * A workspace edit represents changes to many resources managed in the workspace.
  *
  * @param changes Holds changes to existing resources.
  */
case class WorkspaceEdit(changes: Map[String,Array[TextEdit]])

/**
  * Text documents are identified using a URI. On the protocol level, URIs are passed as strings. The corresponding JSON structure looks like this:
  *
  * @param uri The text document's URI.
  */
case class TextDocumentIdentifier(uri: String)

/**
  * An item to transfer a text document from the client to the server.
  *
  * @param uri        The text document's URI.
  * @param languageId The text document's language identifier.
  * @param version    The version number of this document (it will strictly increase after each
  *                   change, including undo/redo).
  * @param text       The content of the opened text document.
  */
case class TextDocumentItem(uri: String,
                            languageId: String,
                            version: Int,
                            text: String)

/**
  * An identifier to denote a specific version of a text document.
  *
  * @param uri     The text document's URI.
  * @param version The version number of this document.
  */
case class VersionedTextDocumentIdentifier(uri: String, version: Int)

/**
  * A parameter literal used in requests to pass a text document and a position inside that document.
  *
  * @param textDocument The text document.
  * @param position     The position inside the text document.
  */
case class TextDocumentPositionParams(textDocument: TextDocumentIdentifier, position: Position)

sealed trait ClientCapabilities
object ClientCapabilities {
  object All extends ClientCapabilities
  implicit val encode = new Encoder[ClientCapabilities] {
    final def apply(a: ClientCapabilities): Json = Json.obj()
  }
  implicit val decode = new Decoder[ClientCapabilities] {
    final def apply(c: HCursor): Decoder.Result[ClientCapabilities] = Xor.right(ClientCapabilities.All)
  }
}

case class InitializeResult(capabilities: ServerCapabilities)

abstract class TextDocumentSyncKind(val value: Int)
object TextDocumentSyncKind {
  object None extends TextDocumentSyncKind(1)
  object Full extends TextDocumentSyncKind(2)
  object Incremental extends TextDocumentSyncKind(3)
  implicit val encode = new Encoder[TextDocumentSyncKind] {
    final def apply(a: TextDocumentSyncKind): Json = Json.fromInt(a.value)
  }
}

case class CompletionOptions(resolveProvider: Boolean = false, triggerCharacters: Array[String] = Array())

case class SignatureHelpOptions(triggerCharacters: Array[String] = Array())

case class CodeLensOptions(resolveProvider: Boolean = false)

case class DocumentOnTypeFormattingOptions(firstTriggerCharacter: String, moreTriggerCharacter: Array[String] = Array())

case class ServerCapabilities(textDocumentSync: Option[TextDocumentSyncKind] = None,
                              hoverProvider: Boolean = false,
                              completionProvider: Option[CompletionOptions] = None,
                              signatureHelpProvider: Option[SignatureHelpOptions] = None,
                              definitionProvider: Boolean = false,
                              referenceProvider: Boolean = false,
                              documentHighlightProvider: Boolean = false,
                              documentSymbolProvider: Boolean = false,
                              workspaceSymbolProvider: Boolean = false,
                              codeActionProvider: Boolean = false,
                              codeLensProvider: Option[CodeLensOptions] = None,
                              documentFormattingProvider: Boolean = false,
                              documentRangeFormattingProvider: Boolean = false,
                              documentOnTypeFormattingProvider: Option[DocumentOnTypeFormattingOptions] = None,
                              renameProvider: Boolean = false)

case class MessageActionItem(title: String)

sealed abstract class MessageType(val value: Int)
object MessageType {
  object Error extends MessageType(1)
  object Warning extends MessageType(2)
  object Info extends MessageType(3)
  object Log extends MessageType(4)
  implicit val encode = new Encoder[MessageType] {
    final def apply(a: MessageType) = Json.fromInt(a.value)
  }
}

case class TextDocumentContentChangeEvent(range: Option[Range], rangeLength: Option[Int], text: String)

sealed abstract class FileChangeType(val value: Int)
object FileChangeType {
  object Created extends FileChangeType(1)
  object Changed extends FileChangeType(2)
  object Deleted extends FileChangeType(3)
  implicit val encode = new Encoder[FileChangeType] {
    final def apply(a: FileChangeType) = Json.fromInt(a.value)
  }
  implicit val decode = new Decoder[FileChangeType] {
    override def apply(c: HCursor): Result[FileChangeType] = c.as[Int].map {
      case 1 => Created
      case 2 => Changed
      case 3 => Deleted
    }
  }
}

case class FileEvent(uri: String, fileChangeType: FileChangeType)

case class CompletionItem(
  label: String,
  kind: Option[CompletionItemKind] = None,
  detail: Option[String] = None,
  documentation: Option[String] = None,
  sortText: Option[String] = None,
  filterText: Option[String] = None,
  insertText: Option[String] = None,
  textEdit: Option[TextEdit],
  data: Option[Json])

sealed abstract class CompletionItemKind(val value: Int)
object CompletionItemKind {
  object Text extends CompletionItemKind(1)
  object Method extends CompletionItemKind(2)
  object Function extends CompletionItemKind(3)
  object Constructor extends CompletionItemKind(4)
  object Field extends CompletionItemKind(5)
  object Variable extends CompletionItemKind(6)
  object Class extends CompletionItemKind(7)
  object Interface extends CompletionItemKind(8)
  object Module extends CompletionItemKind(9)
  object Property extends CompletionItemKind(10)
  object Unit extends CompletionItemKind(10)
  object Value extends CompletionItemKind(11)
  object Enum extends CompletionItemKind(12)
  object Keyword extends CompletionItemKind(13)
  object Snippet extends CompletionItemKind(14)
  object Color extends CompletionItemKind(15)
  object File extends CompletionItemKind(16)
  object Reference extends CompletionItemKind(17)
  implicit val encode = new Encoder[CompletionItemKind] {
    final def apply(a: CompletionItemKind) = Json.fromInt(a.value)
  }
  implicit val decode = new Decoder[CompletionItemKind] {
    override def apply(c: HCursor): Result[CompletionItemKind] = c.as[Int].map {
      case 1 => Text
      case 2 => Method
      case 3 => Function
      case 4 => Constructor
      case 5 => Field
      case 6 => Variable
      case 7 => Class
      case 8 => Interface
      case 9 => Module
      case 10 => Property
      case 10 => Unit
      case 11 => Value
      case 12 => Enum
      case 13 => Keyword
      case 14 => Snippet
      case 15 => Color
      case 16 => File
      case 17 => Reference
    }
  }
}

case class CompletionList(isIncomplete: Boolean, items: Array[CompletionItem])

sealed trait MarkedString
object MarkedString {
  case class Plain(text: String) extends MarkedString
  case class Marked(language: String, value: String) extends MarkedString
  implicit val encode = new Encoder[MarkedString] {
    override final def apply(a: MarkedString): Json = a match {
      case Plain(t) => Json.fromString(t)
      case Marked(lang, value) => Json.obj(
        "language" -> Json.fromString(lang),
        "value" -> Json.fromString(value)
      )
    }
  }
}

case class Hover(contents: Array[MarkedString], range: Option[Range])

case class SignatureHelp(signatures: Array[SignatureInformation], activeSignature: Option[Int], activeParameter: Option[Int])

case class SignatureInformation(label: String, documentation: Option[String], parameters: Option[Array[ParameterInformation]])

case class ParameterInformation(label: String, documentation: Option[String])

case class ReferenceContext(includeDeclaration: Boolean)

/**
  * A document highlight kind.
  */
sealed abstract class DocumentHighlightKind(val value: Int)
object DocumentHighlightKind {
  /**
    * A textual occurrance.
    */
  object Text extends DocumentHighlightKind(1)

  /**
    * Read-access of a symbol, like reading a variable.
    */
  object Read extends DocumentHighlightKind(2)

  /**
    * Write-access of a symbol, like writing to a variable.
    */
  object Write extends DocumentHighlightKind(3)

  implicit val encode = new Encoder[DocumentHighlightKind] {
    final def apply(a: DocumentHighlightKind) = Json.fromInt(a.value)
  }
}

case class DocumentHighlight(range: Range, kind: DocumentHighlightKind)


sealed abstract class SymbolKind(val value: Int)
object SymbolKind {
  object File extends SymbolKind(1)
  object Module extends SymbolKind(2)
  object Namespace extends SymbolKind(3)
  object Package extends SymbolKind(4)
  object Class extends SymbolKind(5)
  object Method extends SymbolKind(6)
  object Property extends SymbolKind(7)
  object Field extends SymbolKind(8)
  object Constructor extends SymbolKind(9)
  object Enum extends SymbolKind(10)
  object Interface extends SymbolKind(11)
  object Function extends SymbolKind(12)
  object Variable extends SymbolKind(13)
  object Constant extends SymbolKind(14)
  object String extends SymbolKind(15)
  object Number extends SymbolKind(16)
  object Boolean extends SymbolKind(17)
  object Array extends SymbolKind(18)
  implicit val encode = new Encoder[SymbolKind] {
    final def apply(a: SymbolKind) = Json.fromInt(a.value)
  }
}


case class SymbolInformation(name: String, kind: SymbolKind, location: Location, containerName: Option[String])

case class CodeLens(range: Range, command: Option[Command], data: Option[Json])

case class CodeActionContext(diagnostics: Array[Diagnostic])
case class FormattingOptions(tabSize: Int, insertSpaces: Boolean)

case class InitializeParams(processId: Int, rootPath: String, initializeOptions: Option[Json], capabilities: ClientCapabilities)
case class ShowMessageParams(`type`: MessageType, message: String)
case class ShowMessageRequestParams(`type`: MessageType, message: String, actions: Option[Array[MessageActionItem]])
case class LogMessageParams(`type`: MessageType, message: String)
case class DidChangeConfigurationParams(settings: Json)
case class DidChangeWatchedFilesParams(changes: Array[FileEvent])
case class DidChangeTextDocumentParams(textDocument: VersionedTextDocumentIdentifier, contentChanges: Array[TextDocumentContentChangeEvent])
case class DidCloseTextDocumentParams(textDocument: TextDocumentIdentifier)
case class DidOpenTextDocumentParams(textDocument: TextDocumentItem)
case class DidSaveTextDocumentParams(textDocument: TextDocumentIdentifier)
case class PublishDiagnosticsParams(uri: String, diagnostics: Array[Diagnostic])
case class ReferenceParams(textDocument: TextDocumentIdentifier, position: Position, context: ReferenceContext)
case class DocumentSymbolParams(textDocument: TextDocumentIdentifier)
case class WorkspaceSymbolParams(query: String)
case class CodeActionParams(textDocument: TextDocumentIdentifier, range: Range, context: CodeActionContext)
case class CodeLensParams(textDocument: TextDocumentIdentifier)
case class DocumentFormattingParams(textDocument: TextDocumentIdentifier, options: FormattingOptions)
case class DocumentRangeFormattingParams(textDocument: TextDocumentIdentifier, range: Range, options: FormattingOptions)
case class DocumentOnTypeFormattingParams(textDocument: TextDocumentIdentifier, position: Position, ch: String, options: FormattingOptions)
case class RenameParams(textDocument: TextDocumentIdentifier, position: Position, newName: String)
