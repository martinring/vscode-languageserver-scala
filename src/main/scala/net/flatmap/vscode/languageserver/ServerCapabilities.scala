package net.flatmap.vscode.languageserver

import io.circe.Json
import net.flatmap.jsonrpc.JsonRPC

import scala.concurrent.Future

/**
  * Defines how the host (editor) should sync document changes to the
  * language  server.
  */
sealed trait TextDocumentSyncKind
object TextDocumentSyncKind {
  /** Documents should not be synced at all. */
  case object None extends TextDocumentSyncKind

  /** Documents are synced by always sending the full content of the
    * document. */
  case object Full extends TextDocumentSyncKind

  /** Documents are synced by sending the full content on open. After that
    * only incremental updates to the document are sent. */
  case object Incremental extends TextDocumentSyncKind
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
  * Document link options
  * @param resolveProvider Document links have a resolve provider as well.
  */
case class DocumentLinkOptions(resolveProvider: Option[Boolean] = None)

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
  * @param documentLinkProvider             The server provides document link support.
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
  renameProvider: Option[Boolean] = None,
  documentLinkProvider: Option[DocumentLinkOptions] = None)

/**
  * @param retry Indicates whether the client should retry to send the
  *              initilize request after showing the message provided in the
  *              ResponseError.
  */
case class InitializeError(retry: Boolean)

/**
  * @param capabilities The capabilities the language server provides.
  */
case class InitializeResult(capabilities: ServerCapabilities)

object ServerCapabilities {
  trait HoverProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(hoverProvider = Some(true))

    /**
      * The hover request is sent from the client to the server to request
      * hover information at a given text document position.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      */
    @JsonRPC.Named("textDocument/hover")
    def hover(textDocument: TextDocumentIdentifier,
              position: Position): Future[Hover]
  }

  trait CompletionProvider extends LanguageServer {
    def completionOptions: CompletionOptions = CompletionOptions()

    override def capabilities = super.capabilities.copy(completionProvider = Some(completionOptions))

    /**
      * The Completion request is sent from the client to the server to
      * compute completion items at a given cursor position. Completion items
      * are presented in the IntelliSense user interface. If computing full
      * completion items is expensive, servers can additionally provide a
      * handler for the completion item resolve request
      * ('completionItem/resolve'). This request is sent when a completion
      * item is selected in the user interface. A typically use case is for
      * example: the 'textDocument/completion' request doesn't fill in the
      * documentation property for returned completion items since it is
      * expensive to compute. When the item is selected in the user interface
      * then a 'completionItem/resolve' request is sent with the selected
      * completion item as a param. The returned completion item should have
      * the documentation property filled in.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      */
    @JsonRPC.Named("textDocument/completion")
    def completion(textDocument: TextDocumentIdentifier,
                   position: Position): Future[CompletionList]

    /**
      * The request is sent from the client to the server to resolve
      * additional information for a given completion item.
      */
    @JsonRPC.SpreadParam
    @JsonRPC.Named("completionItem/resolve")
    def resolveCompletionItem(item: CompletionItem): Future[CompletionItem]
  }

  trait SignatureHelpProvider extends LanguageServer {
    def signatureHelpOptions: SignatureHelpOptions = SignatureHelpOptions()

    override def capabilities: ServerCapabilities = super.capabilities.copy(signatureHelpProvider = Some(signatureHelpOptions))

    /**
      * The signature help request is sent from the client to the server to
      * request signature information at a given cursor position.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      */
    @JsonRPC.Named("textDocument/signatureHelp")
    def signatureHelp(textDocument: TextDocumentIdentifier,
                      position: Position): Future[SignatureHelp]

  }

  trait DefinitionProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(definitionProvider = Some(true))

    /**
      * The goto definition request is sent from the client to the server to
      * resolve the definition location of a symbol at a given text document
      * position.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      */
    @JsonRPC.Named("textDocument/definition")
    def definition(textDocument: TextDocumentIdentifier,
                   position: Position): Future[Seq[Location]]
  }

  trait ReferenceProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(renameProvider = Some(true))

    /**
      * The goto definition request is sent from the client to the server to
      * resolve the definition location of a symbol at a given text document
      * position.
      *
      * @param textDocument       The text document.
      * @param position           The position inside the text document.
      * @param includeDeclaration Include the declaration of the current symbol.
      */
    @JsonRPC.Named("textDocument/references")
    def references(textDocument: TextDocumentIdentifier,
                   position: Position,
                   includeDeclaration: Boolean): Future[Seq[Location]]

  }

  trait DocumentHighlightProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(documentHighlightProvider = Some(true))

    /**
      * The document highlight request is sent from the client to the server
      * to resolve a document highlights for a given text document position.
      * For programming languages this usually highlights all references to
      * the symbol scoped to this file. However we kept
      * 'textDocument/documentHighlight' and 'textDocument/references'
      * separate requests since the first one is allowed to be more fuzzy.
      * Symbol matches usually have a DocumentHighlightKind of Read or Write
      * whereas fuzzy or textual matches use Textas the kind.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      */
    @JsonRPC.Named("textDocument/documentHighlight")
    def documentHighlight(textDocument: TextDocumentIdentifier,
                          position: Position): Future[Seq[DocumentHighlight]]

  }

  trait DocumentSymbolProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(documentSymbolProvider = Some(true))

    /**
      * The document symbol request is sent from the client to the server to
      * list all symbols found in a given text document.
      *
      * @param textDocument The text document.
      */
    @JsonRPC.Named("textDocument/documentSymbol")
    def documentSymbol(textDocument: TextDocumentIdentifier):
    Future[Seq[SymbolInformation]]
  }

  trait WorkspaceSymbolProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(workspaceSymbolProvider = Some(true))

    /**
      * The workspace symbol request is sent from the client to the server to
      * list project-wide symbols matching the query string.
      *
      * @param query A non-empty query string
      */
    @JsonRPC.Named("workspace/symbol")
    def workspaceSymbol(query: String): Future[Seq[SymbolInformation]]
  }

  trait CodeActionProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(codeActionProvider = Some(true))


    /**
      * The code action request is sent from the client to the server to
      * compute commands for a given text document and range. The request is
      * triggered when the user moves the cursor into a problem marker in the
      * editor or presses the lightbulb associated with a marker.
      *
      * @param textDocument The document in which the command was invoked.
      * @param range        The range for which the command was invoked.
      * @param context      Context carrying additional information.
      */
    @JsonRPC.Named("textDocument/codeAction")
    def codeAction(textDocument: TextDocumentIdentifier,
                   range: Range,
                   context: CodeActionContext): Future[Seq[Command]]
  }

  trait CodeLensProvider extends LanguageServer {
    def codeLensOptions: CodeLensOptions = CodeLensOptions()

    override def capabilities: ServerCapabilities = super.capabilities.copy(codeLensProvider = Some(codeLensOptions))

    /**
      * The code lens request is sent from the client to the server to
      * compute code lenses for a given text document.
      *
      * @param textDocument The document to request code lens for.
      */
    @JsonRPC.Named("textDocument/codeLens")
    def codeLens(textDocument: TextDocumentIdentifier): Future[Seq[CodeLens]]

    /**
      * The code lens resolve request is sent from the client to the server
      * to resolve the command for a given code lens item.
      */
    @JsonRPC.Named("codeLens/resolve")
    def resolveCodeLens(range: Range,
                        command: Option[Command],
                        data: Option[Json]): Future[CodeLens]
  }

  trait DocumentFormattingProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(documentFormattingProvider = Some(true))


    /**
      * The document formatting request is sent from the server to the client
      * to format a whole document.
      *
      * @param textDocument The document to format.
      * @param options      The format options.
      */
    @JsonRPC.Named("textDocument/formatting")
    def formatting(textDocument: TextDocumentIdentifier,
                   options: FormattingOptions): Future[Seq[TextEdit]]
  }

  trait DocumentRangeFormattingProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(documentRangeFormattingProvider = Some(true))


    /**
      * The document range formatting request is sent from the client to the
      * server to format a given range in a document.
      *
      * @param textDocument The document to format.
      * @param range        The range to format
      * @param options      The format options
      * @return
      */
    @JsonRPC.Named("textDocument/rangeFormatting")
    def rangeFormatting(textDocument: TextDocumentIdentifier,
                        range: Range,
                        options: FormattingOptions): Future[Seq[TextEdit]]
  }

  trait DocumentOnTypeFormattingProvider extends LanguageServer {
    def documentOnTypeFormattingOptions: DocumentOnTypeFormattingOptions

    override def capabilities: ServerCapabilities = super.capabilities.copy(documentOnTypeFormattingProvider = Some(documentOnTypeFormattingOptions))

    /**
      * The document on type formatting request is sent from the client to
      * the server to format parts of the document during typing.
      *
      * @param textDocument The document to format.
      * @param position     The position at which this request was sent.
      * @param ch           The character that has been typed.
      * @param options      The format options.
      */
    @JsonRPC.Named("textDocument/onTypeFormatting")
    def onTypeFormatting(textDocument: TextDocumentIdentifier,
                         position: Position,
                         ch: String,
                         options: FormattingOptions): Future[Seq[TextEdit]]

  }

  trait RenameProvider extends LanguageServer {
    override def capabilities = super.capabilities.copy(renameProvider = Some(true))

    /**
      * The rename request is sent from the client to the server to perform a
      * workspace-wide rename of a symbol.
      *
      * @param textDocument The document to format.
      * @param position     The position at which this request was sent.
      * @param newName      The new name of the symbol. If the given name is
      *                     not valid the request must return a
      *                     [ResponseError](#ResponseError) with an
      *                     appropriate message set.
      */
    @JsonRPC.Named("textDocument/rename")
    def rename(textDocument: TextDocumentIdentifier,
               position: Position,
               newName: String)

  }

  trait DocumentLinkProvider extends LanguageServer {
    def documentLinkOptions = DocumentLinkOptions()

    override def capabilities = super.capabilities.copy(
      documentLinkProvider = Some(documentLinkOptions))

    @JsonRPC.Named("textDocument/documentLink")
    def documentLink(textDocument: TextDocumentIdentifier): Future[Seq[DocumentLink]]

    @JsonRPC.Named("documentLink/resolve")
    @JsonRPC.SpreadParam
    def resolveDocumentLink(link: DocumentLink): Future[DocumentLink]
  }
}