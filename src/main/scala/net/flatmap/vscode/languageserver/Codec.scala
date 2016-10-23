package net.flatmap.vscode.languageserver

import io.circe._
import io.circe.generic.semiauto._

/**
  * Created by martin on 22/10/2016.
  */
object Codec {
  implicit val encodePosition = deriveEncoder[Position]
  implicit val decodePosition = deriveDecoder[Position]

  implicit val encodeRange = deriveEncoder[Range]
  implicit val decodeRange = deriveDecoder[Range]

  implicit val encodeLocation = deriveEncoder[Location]
  implicit val decodeLocation = deriveDecoder[Location]

  implicit val encodeDiagnosticCode = Encoder.instance[DiagnosticCode] {
    case DiagnosticCode.Int(i) => Json.fromInt(i)
    case DiagnosticCode.String(s) => Json.fromString(s) }
  implicit val decodeDiagnosticCode =
    Decoder.decodeInt.map[DiagnosticCode](DiagnosticCode.Int) or
    Decoder.decodeString.map[DiagnosticCode](DiagnosticCode.String)

  implicit val (
    encodeDiagnosticSeverity,
    decodeDiagnosticSeverity
  ) = Enum(
    DiagnosticSeverity.Error,
    DiagnosticSeverity.Warning,
    DiagnosticSeverity.Information,
    DiagnosticSeverity.Hint
  )

  implicit val encodeDiagnostic = deriveEncoder[Diagnostic]
  implicit val decodeDiagnostic = deriveDecoder[Diagnostic]

  implicit val encodeCommand = deriveEncoder[Command]
  implicit val decodeCommand = deriveDecoder[Command]

  implicit val encodeTextEdit = deriveEncoder[TextEdit]
  implicit val decodeTextEdit = deriveDecoder[TextEdit]

  implicit val encodeWorkspaceEdit = deriveEncoder[WorkspaceEdit]
  implicit val decodeWorkspaceEdit = deriveDecoder[WorkspaceEdit]

  implicit val encodeTextDocumentIdentifier = deriveEncoder[TextDocumentIdentifier]
  implicit val decodeTextDocumentIdentifier = deriveDecoder[TextDocumentIdentifier]

  implicit val encodeTextDocumentItem = deriveEncoder[TextDocumentItem]
  implicit val decodeTextDocumentItem = deriveDecoder[TextDocumentItem]

  implicit val encodeVersionedTextDocumentIdentifier = deriveEncoder[VersionedTextDocumentIdentifier]
  implicit val decodeVersionedTextDocumentIdentifier = deriveDecoder[VersionedTextDocumentIdentifier]

  implicit val encodeClientCapabilities =
    Encoder.instance[ClientCapabilities](_ => Json.obj())
  implicit val decodeClientCapabilities =
    Decoder.const[ClientCapabilities](ClientCapabilities.Default)

  implicit val encodeInitializeError = deriveEncoder[InitializeError]
  implicit val decodeInitializeError = deriveDecoder[InitializeError]

  implicit val (
    encodeTextDocumentSyncKind,
    decodeTextDocumentSyncKind
  ) = Enum(
    TextDocumentSyncKind.None,
    TextDocumentSyncKind.Full,
    TextDocumentSyncKind.Incremental
  )

  implicit val encodeCompletionOptions = deriveEncoder[CompletionOptions]
  implicit val decodeCompletionOptions = deriveDecoder[CompletionOptions]

  implicit val encodeSignatureHelpOptions = deriveEncoder[SignatureHelpOptions]
  implicit val decodeSignatureHelpOptions = deriveDecoder[SignatureHelpOptions]

  implicit val encodeCodeLensOptions = deriveEncoder[CodeLensOptions]
  implicit val decodeCodeLensOptions = deriveDecoder[CodeLensOptions]

  implicit val encodeDocumentOnTypeFormattingOptions = deriveEncoder[DocumentOnTypeFormattingOptions]
  implicit val decodeDocumentOnTypeFormattingOptions = deriveDecoder[DocumentOnTypeFormattingOptions]

  implicit val encodeServerCapabilities = deriveEncoder[ServerCapabilities]
  implicit val decodeServerCapabilities = deriveDecoder[ServerCapabilities]

  implicit val encodeInitializeResult = deriveEncoder[InitializeResult]
  implicit val decodeInitializeResult = deriveDecoder[InitializeResult]

  implicit val encodeMessageActionItem = deriveEncoder[MessageActionItem]
  implicit val decodeMessageActionItem = deriveDecoder[MessageActionItem]

  implicit val (
    encodeMessageType,
    decodeMessageType
    ) = Enum(
    MessageType.Error,
    MessageType.Warning,
    MessageType.Info,
    MessageType.Log
  )

  implicit val encodeTextDocumentContentChangeEvent = deriveEncoder[TextDocumentContentChangeEvent]
  implicit val decodeTextDocumentContentChangeEvent = deriveDecoder[TextDocumentContentChangeEvent]

  implicit val (
    encodeFileChangeType,
    decodeFileChangeType
    ) = Enum(
    FileChangeType.Created,
    FileChangeType.Changed,
    FileChangeType.Deleted
  )

  implicit val encodeFileEvent = deriveEncoder[FileEvent]
  implicit val decodeFileEvent = deriveDecoder[FileEvent]

  implicit val (
    encodeCompletionItemKind,
    decodeCompletionItemKind
  ) = Enum(
    CompletionItemKind.Text,
    CompletionItemKind.Method,
    CompletionItemKind.Function,
    CompletionItemKind.Constructor,
    CompletionItemKind.Field,
    CompletionItemKind.Variable,
    CompletionItemKind.Class,
    CompletionItemKind.Interface,
    CompletionItemKind.Module,
    CompletionItemKind.Property,
    CompletionItemKind.Unit,
    CompletionItemKind.Value,
    CompletionItemKind.Enum,
    CompletionItemKind.Keyword,
    CompletionItemKind.Snippet,
    CompletionItemKind.Color,
    CompletionItemKind.File,
    CompletionItemKind.Reference
  )

  implicit val encodeCompletionItem = deriveEncoder[CompletionItem]
  implicit val decodeCompletionItem = deriveDecoder[CompletionItem]

  implicit val encodeCompletionList = deriveEncoder[CompletionList]
  implicit val decodeCompletionList =
    Decoder[Seq[CompletionItem]].map(CompletionList(_)) or
    deriveDecoder[CompletionList]

  implicit val encodeMarkedString = Encoder.instance[MarkedString] {
    case MarkedString(s,None) => Json.fromString(s)
    case MarkedString(s,Some(l)) => Json.obj(
      "language" -> Json.fromString(s),
      "value" -> Json.fromString(l)
    )}
  implicit val decodeMarkedString =
    Decoder.decodeString.map(MarkedString(_)) or
    Decoder.forProduct2("language","value")(MarkedString.apply)

  implicit val encodeHover = deriveEncoder[Hover]
  implicit val decodeHover = deriveDecoder[Hover]

  implicit val encodeParameterInformation = deriveEncoder[ParameterInformation]
  implicit val decodeParameterInformation = deriveDecoder[ParameterInformation]

  implicit val encodeSignatureInformation = deriveEncoder[SignatureInformation]
  implicit val decodeSignatureInformation = deriveDecoder[SignatureInformation]

  implicit val encodeSignatureHelp = deriveEncoder[SignatureHelp]
  implicit val decodeSignatureHelp = deriveDecoder[SignatureHelp]

  implicit val encodeReferenceContext = deriveEncoder[ReferenceContext]
  implicit val decodeReferenceContext = deriveDecoder[ReferenceContext]

  implicit val (
    encodeDocumentHighlightKind,
    decodeDocumentHighlightKind
  ) = Enum(
    DocumentHighlightKind.Text,
    DocumentHighlightKind.Read,
    DocumentHighlightKind.Write
  )

  implicit val encodeDocumentHighlight = deriveEncoder[DocumentHighlight]
  implicit val decodeDocumentHighlight = deriveDecoder[DocumentHighlight]

  implicit val (
    encodeSymbolKind,
    decodeSymbolKind
  ) = Enum(
    SymbolKind.File,
    SymbolKind.Module,
    SymbolKind.Namespace,
    SymbolKind.Package,
    SymbolKind.Class,
    SymbolKind.Method,
    SymbolKind.Property,
    SymbolKind.Field,
    SymbolKind.Constructor,
    SymbolKind.Enum,
    SymbolKind.Interface,
    SymbolKind.Function,
    SymbolKind.Variable,
    SymbolKind.Constant,
    SymbolKind.String,
    SymbolKind.Number,
    SymbolKind.Boolean,
    SymbolKind.Array
  )

  implicit val encodeSymbolInformation = deriveEncoder[SymbolInformation]
  implicit val decodeSymbolInformation = deriveDecoder[SymbolInformation]

  implicit val encodeCodeLens = deriveEncoder[CodeLens]
  implicit val decodeCodeLens = deriveDecoder[CodeLens]

  implicit val encodeCodeActionContext = deriveEncoder[CodeActionContext]
  implicit val decodeCodeActionContext = deriveDecoder[CodeActionContext]

  implicit val encodeFormattingOptions = deriveEncoder[FormattingOptions]
  implicit val decodeFormattingOptions = deriveDecoder[FormattingOptions]

}