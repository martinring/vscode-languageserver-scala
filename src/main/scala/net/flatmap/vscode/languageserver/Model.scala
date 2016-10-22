package net.flatmap.vscode.languageserver

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
  * A range in a text document expressed as (zero-based) start and end
  * positions. A range is comparable to a selection in an editor. Therefore
  * the end position is exclusive.
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

/**
  * Represents a reference to a command. Provides a title which will be used
  * to represent a command in the UI. Commands are identitifed using a string
  * identifier and the protocol currently doesn't specify a set of well known
  * commands. So executing a command requires some tool extension code.
  *
  * @param title     Title of the command, like `save`.
  * @param command   The identifier of the actual command handler.
  * @param arguments Arguments that the command handler should be invoked with.
  */
case class Command(title: String,
                   command: String,
                   arguments: Option[Seq[Json]] = None)

/**
  * A textual edit applicable to a text document.
  *
  * @param range   The range of the text document to be manipulated. To
  *                insert text into a document create a range where start ===
  *                end.
  * @param newText The string to be inserted. For delete operations use an
  *                empty string.
  */
case class TextEdit(range: Range, newText: String)

/**
  * A workspace edit represents changes to many resources managed in the
  * workspace.
  *
  * @param changes Holds changes to existing resources.
  */
case class WorkspaceEdit(changes: Map[String,Seq[TextEdit]])

/**
  * Text documents are identified using a URI. On the protocol level, URIs
  * are passed as strings. The corresponding JSON structure looks like this:
  *
  * @param uri The text document's URI.
  */
case class TextDocumentIdentifier(uri: String)

/**
  * An item to transfer a text document from the client to the server.
  *
  * @param uri        The text document's URI.
  * @param languageId The text document's language identifier.
  * @param version    The version number of this document (it will strictly
  *                   increase after each change, including undo/redo).
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

sealed trait ClientCapabilities
object ClientCapabilities {
  object Default extends ClientCapabilities
}

/**
  * @param retry Indicates whether the client should retry to send the
  *              initilize request after showing the message provided in the
  *              ResponseError.
  */
case class InitializeError(retry: Boolean)

/**
  * Defines how the host (editor) should sync document changes to the
  * language  server.
  */
sealed trait TextDocumentSyncKind
object TextDocumentSyncKind {
  /** Documents should not be synced at all. */
  object None extends TextDocumentSyncKind

  /** Documents are synced by always sending the full content of the
    * document. */
  object Full extends TextDocumentSyncKind

  /** Documents are synced by sending the full content on open. After that
    * only incremental updates to the document are sent. */
  object Incremental extends TextDocumentSyncKind
}

/**
  * Completion options.
  * @param resolveProvider   The server provides support to resolve
  *                          additional information for a completion item.
  * @param triggerCharacters The characters that trigger completion
  *                          automatically.
  */
case class CompletionOptions(resolveProvider: Option[Boolean] = None,
                             triggerCharacters: Option[Seq[String]] = None)

/**
  * Signature help options.
  * @param triggerCharacters The characters that trigger signature help
  *                          automatically.
  */
case class SignatureHelpOptions(triggerCharacters: Option[Seq[String]] = None)

/**
  * Code Lens options.
  * @param resolveProvider Code lens has a resolve provider as well.
  */
case class CodeLensOptions(resolveProvider: Option[Boolean] = None)

/**
  * Format document on type options
  * @param firstTriggerCharacter A character on which formatting should be
  *                              triggered, like `}`.
  * @param moreTriggerCharacter  More trigger characters.
  */
case class DocumentOnTypeFormattingOptions(
  firstTriggerCharacter: String,
  moreTriggerCharacter: Option[Seq[String]] = None)

/**
  * The server can signal the following capabilities
  * @param textDocumentSync                 Defines how text documents are
  *                                         synced.
  * @param hoverProvider                    The server provides hover support.
  * @param completionProvider               The server provides completion
  *                                         support.
  * @param signatureHelpProvider            The server provides signature help
  *                                         support.
  * @param definitionProvider               The server provides goto
  *                                         definition support.
  * @param referenceProvider                The server provides find
  *                                         references support.
  * @param documentHighlightProvider        The server provides document
  *                                         highlight support.
  * @param documentSymbolProvider           The server provides document
  *                                         symbol support.
  * @param workspaceSymbolProvider          The server provides workspace
  *                                         symbol support.
  * @param codeActionProvider               The server provides code actions.
  * @param codeLensProvider                 The server provides code lens.
  * @param documentFormattingProvider       The server provides document
  *                                         formatting.
  * @param documentRangeFormattingProvider  The server provides document
  *                                         range formatting.
  * @param documentOnTypeFormattingProvider The server provides document
  *                                         formatting on typing.
  * @param renameProvider                   The server provides rename support.
  */
case class ServerCapabilities(
  textDocumentSync: Option[TextDocumentSyncKind] = None,
  hoverProvider: Option[Boolean] = None,
  completionProvider: Option[CompletionOptions] = None,
  signatureHelpProvider: Option[SignatureHelpOptions] = None,
  definitionProvider: Option[Boolean] = None,
  referenceProvider: Option[Boolean] = None,
  documentHighlightProvider: Option[Boolean] = None,
  documentSymbolProvider: Option[Boolean] = None,
  workspaceSymbolProvider: Option[Boolean] = None,
  codeActionProvider: Option[Boolean] = None,
  codeLensProvider: Option[CodeLensOptions] = None,
  documentFormattingProvider: Option[Boolean] = None,
  documentRangeFormattingProvider: Option[Boolean] = None,
  documentOnTypeFormattingProvider: Option[DocumentOnTypeFormattingOptions] = None,
  renameProvider: Option[Boolean] = None)

/**
  * @param capabilities The capabilities the language server provides.
  */
case class InitializeResult(capabilities: ServerCapabilities)

/**
  * @param title A short title like 'Retry', 'Open Log' etc.
  */
case class MessageActionItem(title: String)

sealed trait MessageType
object MessageType {
  /** An error message. */
  object Error extends MessageType
  /** A warning message */
  object Warning extends MessageType
  /** An information message. */
  object Info extends MessageType
  /** A log message. */
  object Log extends MessageType
}

/**
  * An event describing a change to a text document. If range and rangeLength
  * are omitted the new text is considered to be the full content of the
  * document.
  *
  * @param range       The range of the document that changed.
  * @param rangeLength The length of the range that got replaced.
  * @param text        The new text of the document.
  */
case class TextDocumentContentChangeEvent(text: String,
  range: Option[Range] = None, rangeLength: Option[Int] = None)

/**
  * The file event type.
  */
sealed trait FileChangeType
object FileChangeType {
  /** The file got created */
  object Created extends FileChangeType
  /** The file got changed */
  object Changed extends FileChangeType
  /** The file got deleted */
  object Deleted extends FileChangeType
}

/**
  * An event describing a file change
  * @param uri            The file's URI
  * @param fileChangeType The change type
  */
case class FileEvent(uri: String, fileChangeType: FileChangeType)

/**
  * The kind of a completion entry.
  */
sealed trait CompletionItemKind
object CompletionItemKind {
  object Text extends CompletionItemKind
  object Method extends CompletionItemKind
  object Function extends CompletionItemKind
  object Constructor extends CompletionItemKind
  object Field extends CompletionItemKind
  object Variable extends CompletionItemKind
  object Class extends CompletionItemKind
  object Interface extends CompletionItemKind
  object Module extends CompletionItemKind
  object Property extends CompletionItemKind
  object Unit extends CompletionItemKind
  object Value extends CompletionItemKind
  object Enum extends CompletionItemKind
  object Keyword extends CompletionItemKind
  object Snippet extends CompletionItemKind
  object Color extends CompletionItemKind
  object File extends CompletionItemKind
  object Reference extends CompletionItemKind
}

/**
  * @param label         The label of this completion item. By default also
  *                      the text that is inserted when selecting this
  *                      completion.
  * @param kind          The kind of this completion item. Based of the kind
  *                      an icon is chosen by the editor.
  * @param detail        A human-readable string with additional information
  *                      about this item, like type or symbol information.
  * @param documentation A human-readable string that represents a doc-comment.
  * @param sortText      A string that shoud be used when comparing this item
  *                      with other items. When `falsy` the label is used.
  * @param filterText    A string that should be used when filtering a set of
  *                      completion items. When `falsy` the label is used.
  * @param insertText    A string that should be inserted a document when
  *                      selecting this completion. When `falsy` the label is
  *                      used.
  * @param textEdit      An edit which is applied to a document when selecting
  *                      this completion. When an edit is provided the value of
  *                      insertText is ignored.
  * @param additionalTextEdits An optional array of additional text edits
  *                            that are applied when selecting this
  *                            completion. Edits must not overlap with the
  *                            main edit nor with themselves.
  * @param command       An optional command that is executed *after*
  *                      inserting this completion. *Note* that additional
  *                      modifications to the current document should be
  *                      described with the additionalTextEdits-property.
  * @param data          An data entry field that is preserved on a completion
  *                      item between a completion and a completion resolve
  *                      request.
  */
case class CompletionItem(
  label: String,
  kind: Option[CompletionItemKind] = None,
  detail: Option[String] = None,
  documentation: Option[String] = None,
  sortText: Option[String] = None,
  filterText: Option[String] = None,
  insertText: Option[String] = None,
  textEdit: Option[TextEdit] = None,
  additionalTextEdits: Option[Seq[TextEdit]] = None,
  command: Option[Command] = None,
  data: Option[Json] = None)

/**
  * Represents a collection of [completion items](#CompletionItem) to be
  * presented in the editor.
  *
  * @param isIncomplete This list it not complete. Further typing should
  *                     result in recomputing this list.
  */
case class CompletionList(items: Seq[CompletionItem],
                          isIncomplete: Boolean = false)

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

/**
  * Represents a parameter of a callable-signature. A parameter can have a
  * label and a doc-comment.
  * @param label         The label of this signature. Will be shown in the UI.
  * @param documentation The human-readable doc-comment of this signature.
  *                      Will be shown in the UI but can be omitted.
  */
case class ParameterInformation(label: String,
                                documentation: Option[String])

/**
  * Represents the signature of something callable. A signature can have a
  * label, like a function-name, a doc-comment, and a set of parameters.
  * @param label         The label of this signature. Will be shown in the UI.
  * @param documentation The human-readable doc-comment of this signature.
  *                      Will be shown in the UI but can be omitted.
  * @param parameters    The parameters of this signature.
  */
case class SignatureInformation(label: String,
                                documentation: Option[String],
                                parameters: Option[Seq[ParameterInformation]])


/**
  * Signature help represents the signature of something callable. There can
  * be multiple signature but only one active and only one active parameter.
  *
  * @param signatures      One or more signatures.
  * @param activeSignature The active signature.
  * @param activeParameter The active parameter of the active signature.
  */
case class SignatureHelp(signatures: Seq[SignatureInformation],
                         activeSignature: Option[Int],
                         activeParameter: Option[Int])

/**
  * @param includeDeclaration Include the declaration of the current symbol.
  */
case class ReferenceContext(includeDeclaration: Boolean)

/**
  * A document highlight kind.
  */
sealed trait DocumentHighlightKind
object DocumentHighlightKind {
  /** A textual occurrance. */
  object Text extends DocumentHighlightKind

  /** Read-access of a symbol, like reading a variable. */
  object Read extends DocumentHighlightKind

  /** Write-access of a symbol, like writing to a variable. */
  object Write extends DocumentHighlightKind
}

/**
  * A document highlight is a range inside a text document which deserves
  * special attention. Usually a document highlight is visualized by changing
  * the background color of its range.
  * @param range The range this highlight applies to.
  * @param kind  The highlight kind, default is DocumentHighlightKind.Text.
  */
case class DocumentHighlight(range: Range,
                             kind: Option[DocumentHighlightKind])

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

/**
  * A code lens represents a command that should be shown along with source
  * text, like the number of references, a way to run tests, etc.
  *
  * A code lens is _unresolved_ when no command is associated to it. For
  * performance reasons the creation of a code lens and resolving should be
  * done in two stages.
  *
  * @param range   The range in which this code lens is valid. Should only
  *                span a single line.
  * @param command The command this code lens represents.
  * @param data    A data entry field that is preserved on a code lens item
  *                between a code lens and a code lens resolve request.
  */
case class CodeLens(range: Range, command: Option[Command], data: Option[Json])

/**
  * Contains additional diagnostic information about the context in which a
  * code action is run.
  * @param diagnostics An array of diagnostics.
  */
case class CodeActionContext(diagnostics: Seq[Diagnostic])

/**
  * Value-object describing what options formatting should use.
  * @param tabSize      Size of a tab in spaces.
  * @param insertSpaces Prefer spaces over tabs.
  */
case class FormattingOptions(tabSize: Int, insertSpaces: Boolean)