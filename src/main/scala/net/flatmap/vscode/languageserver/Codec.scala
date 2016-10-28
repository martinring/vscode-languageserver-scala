package net.flatmap.vscode.languageserver

import java.net.URI

import cats.data.Xor
import io.circe._
import io.circe.generic.semiauto._

import scala.util.Try

/**
  * Created by martin on 22/10/2016.
  */
object Codec {
  implicit val encodeURI = Encoder.encodeString.contramap((uri: URI) => uri.toString)
  implicit val decodeURI = Decoder.decodeString.map(URI.create)

  implicit val encodeTrace = Encoder.instance[Trace] {
    case Trace.Off => Json.fromString("off")
    case Trace.Messages => Json.fromString("messages")
    case Trace.Verbose => Json.fromString("verbose")
  }
  implicit val decodeTrace =
    Decoder.decodeString.emap[Trace] {
      case "off" => Xor.right(Trace.Off)
      case "messages" => Xor.right(Trace.Messages)
      case "verbose" => Xor.right(Trace.Verbose)
      case other => Xor.left(s"invalid value for trace: '$other'")
    }

  implicit val uriKeyEncoder = KeyEncoder.instance[URI](_.toString)
  implicit val uriKeyDecoder = KeyDecoder.instance[URI](s => Try(URI.create(s)).toOption)

  implicit val encodePosition = deriveEncoder[Position]
  implicit val decodePosition = deriveDecoder[Position]

  implicit val encodeRange =
    Encoder.forProduct2[Position,Position,Range]("start","end")(r => (r.start,r.end))

  implicit val decodeRange =
    Decoder.forProduct2[Position,Position,Range]("start","end")(Range.apply)

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
  ) = Enum[DiagnosticSeverity](
    DiagnosticSeverity.Error,
    DiagnosticSeverity.Warning,
    DiagnosticSeverity.Information,
    DiagnosticSeverity.Hint
  )

  implicit val encodeDiagnostic = deriveEncoder[Diagnostic]
  implicit val decodeDiagnostic = deriveDecoder[Diagnostic]

  implicit val encodeCommand = deriveEncoder[Command]
  implicit val decodeCommand =
    Decoder.forProduct3("title","command","arguments")(
      (title: String, command: String, arguments: Option[Seq[Json]]) =>
        Command(title,command,arguments.getOrElse(Seq.empty))
    )

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

  implicit val encodeClientCapabilities = deriveEncoder[ClientCapabilities]
  implicit val decodeClientCapabilities = deriveDecoder[ClientCapabilities]

  implicit val encodeInitializeError = deriveEncoder[InitializeError]
  implicit val decodeInitializeError = deriveDecoder[InitializeError]

  implicit val (
    encodeTextDocumentSyncKind,
    decodeTextDocumentSyncKind
  ) = Enum[TextDocumentSyncKind](
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

  implicit val encodeLinkOptions = deriveEncoder[DocumentLinkOptions]
  implicit val decodeLinkOptions = deriveDecoder[DocumentLinkOptions]

  implicit val encodeServerCapabilities = deriveEncoder[ServerCapabilities]
  implicit val decodeServerCapabilities = deriveDecoder[ServerCapabilities]

  implicit val encodeInitializeResult = deriveEncoder[InitializeResult]
  implicit val decodeInitializeResult = deriveDecoder[InitializeResult]

  implicit val encodeMessageActionItem = deriveEncoder[MessageActionItem]
  implicit val decodeMessageActionItem = deriveDecoder[MessageActionItem]

  implicit val (
    encodeMessageType,
    decodeMessageType
    ) = Enum[MessageType](
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
    ) = Enum[FileChangeType](
    FileChangeType.Created,
    FileChangeType.Changed,
    FileChangeType.Deleted
  )

  implicit val encodeFileEvent = deriveEncoder[FileEvent]
  implicit val decodeFileEvent = deriveDecoder[FileEvent]

  implicit val (
    encodeCompletionItemKind,
    decodeCompletionItemKind
  ) = Enum[CompletionItemKind](
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
  implicit val decodeHover =
    Decoder.forProduct2("contents","range")(
      (contents: MarkedString, range: Option[Range]) =>
        Hover(Seq(contents),range)
    ) or Decoder.forProduct2("contents","range")(
      (contents: Seq[MarkedString], range: Option[Range]) =>
        Hover(contents,range)
    )


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
  ) = Enum[DocumentHighlightKind](
    DocumentHighlightKind.Text,
    DocumentHighlightKind.Read,
    DocumentHighlightKind.Write
  )

  implicit val encodeDocumentHighlight = deriveEncoder[DocumentHighlight]
  implicit val decodeDocumentHighlight = deriveDecoder[DocumentHighlight]

  implicit val (
    encodeSymbolKind,
    decodeSymbolKind
  ) = Enum[SymbolKind](
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

  implicit val encodeFormattingOption =
    Encoder.instance[FormattingOption] {
      case FormattingOption.Boolean(b) => Json.fromBoolean(b)
      case FormattingOption.Number(n) => Json.fromJsonNumber(n)
      case FormattingOption.String(s) => Json.fromString(s)
    }
  implicit val decodeFormattingOption =
    Decoder.decodeJsonNumber.map[FormattingOption](FormattingOption.Number) or
    Decoder.decodeString.map[FormattingOption](FormattingOption.String) or
    Decoder.decodeBoolean.map[FormattingOption](FormattingOption.Boolean)

  implicit val encodeFormattingOptions =
    Encoder.instance[FormattingOptions](f => Json.obj(
      (Map("tabSize" -> Json.fromInt(f.tabSize),
      "insertSpaces" -> Json.fromBoolean(f.insertSpaces)) ++
      f.furtherProperties.mapValues(encodeFormattingOption.apply)).toSeq :_*
    ))
  implicit val decodeFormattingOptions = {
    val base = Decoder.forProduct2("tabSize", "insertSpaces")((t: Int, i: Boolean) => (t, i)) and
      Decoder[Map[String,FormattingOption]]
    base.map {
      case ((tabSize,insertSpaces),further) =>
        FormattingOptions(tabSize,insertSpaces,further)
    }
  }

  implicit val encodeDocumentLink = deriveEncoder[DocumentLink]
  implicit val decodeDocumentLink = deriveDecoder[DocumentLink]

  implicit val (
    encodeTextDocumentSaveReason,
    decodeTextDocumentSaveReason
  ) = Enum[TextDocumentSaveReason](
    TextDocumentSaveReason.Manual,
    TextDocumentSaveReason.AfterDelay,
    TextDocumentSaveReason.FocusOut
  )

  implicit val encodeDocumentFilter = Encoder.instance[DocumentFilter] {
    case DocumentFilter(Some(language),None,None) => Json.fromString(language)
    case other => Json.obj(
      "language" -> other.language.fold(Json.Null)(Json.fromString),
      "scheme"   -> other.language.fold(Json.Null)(Json.fromString),
      "pattern"  -> other.language.fold(Json.Null)(Json.fromString)
    )
  }
  implicit val decodeDocumentFilter: Decoder[DocumentFilter] =
    Decoder.decodeString.map[DocumentFilter](DocumentFilter.language) or
    Decoder.forProduct3("language","scheme","pattern")(DocumentFilter.apply)

  implicit val encodeDocumentSelector = Encoder.instance[DocumentSelector] {
    case DocumentSelector(single) => encodeDocumentFilter(single)
    case DocumentSelector(multiple @ _*) => Encoder[Seq[DocumentFilter]].apply(multiple)
  }
  implicit val decodeDocumentSelector =
    decodeDocumentFilter.map[DocumentSelector](filter => DocumentSelector(filter)) or
    Decoder[Seq[DocumentFilter]].map[DocumentSelector](DocumentSelector.apply)

  implicit val encodeDocumentOptions = deriveEncoder[DocumentOptions]
  implicit val decodeDocumentOptions = deriveDecoder[DocumentOptions]
}
