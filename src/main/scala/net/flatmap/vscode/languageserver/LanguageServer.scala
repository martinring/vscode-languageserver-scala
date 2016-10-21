package net.flatmap.vscode.languageserver

import io.circe.Json
import net.flatmap.jsonrpc.JsonRPCNamespace

import scala.concurrent.Future

object LanguageServer {
  trait TextDocument {
    /**
      * The document open notification is sent from the client to the server
      * to signal newly opened text documents. The document's truth is now
      * managed by the client and the server must not try to read the
      * document's truth using the document's uri.
      *
      * @param textDocument The document that was opened.
      */
    def didOpen(textDocument: TextDocumentItem)

    /**
      * The document change notification is sent from the client to the
      * server to signal changes to a text document. In 2.0 the shape of the
      * params has changed to include proper version numbers and language ids.
      * @param textDocument   The document that did change. The version
      *                       number points to the version after all provided
      *                       content changes have been applied.
      * @param contentChanges The actual content changes.
      */
    def didChange(textDocument: VersionedTextDocumentIdentifier,
                  contentChanges: Seq[TextDocumentContentChangeEvent])

    /**
      * The document close notification is sent from the client to the server
      * when the document got closed in the client. The document's truth now
      * exists where the document's uri points to (e.g. if the document's uri
      * is a file uri the truth now exists on disk).
      * @param textDocument The document that was closed.
      */
    def didClose(textDocument: TextDocumentIdentifier)

    /**
      * The document save notification is sent from the client to the server
      * when the document was saved in the client.
      *
      * @param textDocument The document that was saved.
      */
    def didSave(textDocument: TextDocumentIdentifier)

    /**
      * The watched files notification is sent from the client to the server
      * when the client detects changes to files watched by the language client.
      * @param changes The actual file events.
      */
    def didChangeWatchedFiles(changes: Seq[FileEvent])

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
    def completion(textDocument: TextDocumentIdentifier,
                   position: Position): Future[CompletionList]

    /**
      * The hover request is sent from the client to the server to request
      * hover information at a given text document position.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      */
    def hover(textDocument: TextDocumentIdentifier,
              position: Position): Future[Hover]

    /**
      * The signature help request is sent from the client to the server to
      * request signature information at a given cursor position.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      */
    def signatureHelp(textDocument: TextDocumentIdentifier,
                      position: Position): Future[SignatureHelp]

    /**
      * The goto definition request is sent from the client to the server to
      * resolve the definition location of a symbol at a given text document
      * position.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      */
    def definition(textDocument: TextDocumentIdentifier,
                   position: Position): Future[Seq[Location]]

    /**
      * The goto definition request is sent from the client to the server to
      * resolve the definition location of a symbol at a given text document
      * position.
      *
      * @param textDocument The text document.
      * @param position     The position inside the text document.
      * @param includeDeclaration Include the declaration of the current symbol.
      */
    def references(textDocument: TextDocumentIdentifier,
                   position: Position,
                   includeDeclaration: Boolean): Future[Seq[Location]]

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
    def documentHighlight(textDocument: TextDocumentIdentifier,
                          position: Position): Future[Seq[DocumentHighlight]]

    /**
      * The document symbol request is sent from the client to the server to
      * list all symbols found in a given text document.
      *
      * @param textDocument The text document.
      */
    def documentSymbol(textDocument: TextDocumentIdentifier):
      Future[Seq[SymbolInformation]]

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
    def codeAction(textDocument: TextDocumentIdentifier,
                   range: Range,
                   context: CodeActionContext): Future[Seq[Command]]

    /**
      * The code lens request is sent from the client to the server to
      * compute code lenses for a given text document.
      *
      * @param textDocument The document to request code lens for.
      */
    def codeLens(textDocument: TextDocumentIdentifier): Future[Seq[CodeLens]]

    /**
      * The document formatting request is sent from the server to the client
      * to format a whole document.
      *
      * @param textDocument The document to format.
      * @param options      The format options.
      */
    def formatting(textDocument: TextDocumentIdentifier,
                   options: FormattingOptions): Future[Seq[TextEdit]]

    /**
      * The document range formatting request is sent from the client to the
      * server to format a given range in a document.
      *
      * @param textDocument The document to format.
      * @param range        The range to format
      * @param options      The format options
      * @return
      */
    def rangeFormatting(textDocument: TextDocumentIdentifier,
                        range: Range,
                        options: FormattingOptions): Future[Seq[TextEdit]]

    /**
      * The document on type formatting request is sent from the client to
      * the server to format parts of the document during typing.
      *
      * @param textDocument The document to format.
      * @param position     The position at which this request was sent.
      * @param ch           The character that has been typed.
      * @param options      The format options.
      */
    def onTypeFormatting(textDocument: TextDocumentIdentifier,
                         position: Position,
                         ch: String,
                         options: FormattingOptions): Future[Seq[TextEdit]]

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
    def rename(textDocument: TextDocumentIdentifier,
               position: Position,
               newName: String)
  }

  trait CompletionItem {
    /**
      * The request is sent from the client to the server to resolve
      * additional information for a given completion item.
      */
    def resolve(label: String,
                kind: Option[CompletionItemKind],
                detail: Option[String],
                documentation: Option[String],
                sortText: Option[String],
                filterText: Option[String],
                insertText: Option[String],
                textEdit: Option[TextEdit],
                additionalTextEdits: Option[Seq[TextEdit]],
                command: Option[Command],
                data: Option[Json]): Future[CompletionItem]
  }

  trait CodeLens {
    /**
      * The code lens resolve request is sent from the client to the server
      * to resolve the command for a given code lens item.
      */
    def resolve(range: Range,
                command: Option[Command],
                data: Option[Json]): Future[CodeLens]
  }

  trait Workspace {
    /**
      * The workspace symbol request is sent from the client to the server to
      * list project-wide symbols matching the query string.
      *
      * @param query A non-empty query string
      */
    def symbol(query: String): Future[Seq[SymbolInformation]]
  }
}

trait LanguageServer {
  /**
    * The initialize request is sent as the first request from the client to
    * the server.
    *
    * @param processId The process Id of the parent process that started the
    *                  server. Is None if the process has not been started by
    *                  another process. If the parent process is not alive
    *                  then the server should exit (see exit notification)
    *                  its process.
    * @param rootPath  The rootPath of the workspace. Is None if no folder is
    *                  open.
    * @param initializationOptions User provided initialization options.
    * @param capabilities The capabilities provided by the client (editor)
    */
  def initialize(processId: Option[Int],
                 rootPath: Option[String],
                 initializationOptions: Option[Json],
                 capabilities: ClientCapabilities): Future[InitializeResult]

  /** The shutdown request is sent from the client to the server. It asks the
    * server to shut down, but to not exit (otherwise the response might not be
    * delivered correctly to the client). There is a separate exit
    * notification that asks the server to exit.
    * @return code and message set in case an exception happens during
    *         shutdown request.
    */
  def shutdown(): Future[Unit]

  /**
    * A notification to ask the server to exit its process. The server should
    * exit with success code 0 if the shutdown request has been received
    * before; otherwise with error code 1.
    */
  def exit()

  @JsonRPCNamespace("textDocument/")
  def textDocument: LanguageServer.TextDocument

  @JsonRPCNamespace("completionItem/")
  def completionItem: LanguageServer.CompletionItem

  @JsonRPCNamespace("workspace/")
  val workspace: LanguageServer.Workspace

  @JsonRPCNamespace("codeLens/")
  val codeLens: LanguageServer.CodeLens
}

/*
import akka.stream.Materializer
import io.circe._
import io.circe.generic.auto._
import net.flatmap.jsonrpc._
import io.circe.syntax._
import net.flatmap.jsonrpc.{Connection, ErrorCodes, ResponseError, ResponseMessage}

import scala.concurrent.{ExecutionContext, Future, Promise}

trait LanguageServiceProvider {
  def initialize(processId: Int, rootPath: String, initializationOptions: Option[Json], capabilities: ClientCapabilities): Future[LanguageService]
}

trait LanguageService {
  private [languageserver] def capabilities: ServerCapabilities= ServerCapabilities(Some(textDocumentSyncKind))
  def shutdown(): Future[Unit]
  def exit(): Unit
  def textDocumentSyncKind: TextDocumentSyncKind
  def didChangeConfiguration(settings: Json): Unit
  def didChangeWatchedFiles(changes: Array[FileEvent]): Unit
  def didOpen(textDocument: TextDocumentItem): Unit
  def didChange(textDocument: VersionedTextDocumentIdentifier, contentChanges: Array[TextDocumentContentChangeEvent]): Unit
  def didClose(textDocument: TextDocumentIdentifier): Unit
  def didSave(textDocument: TextDocumentIdentifier): Unit
}

trait HoverProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(hoverProvider = true)
  def hover(textDocument: TextDocumentIdentifier, position: Position): Future[Hover]
}

trait CompletionProvider extends LanguageService {
  def completionOptions: CompletionOptions
  override private [languageserver] def capabilities = super.capabilities.copy(completionProvider = Some(completionOptions))
  def completion(textDocument: TextDocumentIdentifier, position: Position): Future[CompletionList]
  def resolveCompletionItem(item: net.flatmap.vscode.languageserver.CompletionItem): Future[net.flatmap.vscode.languageserver.CompletionItem]
}

trait SignatureHelpProvider extends LanguageService {
  def signatureHelpOptions: SignatureHelpOptions
  override private[languageserver] def capabilities: ServerCapabilities = super.capabilities.copy(signatureHelpProvider = Some(signatureHelpOptions))
  def signatureHelp(textDocument: TextDocumentIdentifier, position: Position): Future[SignatureHelp]
}

trait DefinitionProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(definitionProvider = true)
  def definition(textDocument: TextDocumentIdentifier, position: Position): Future[Array[Location]]
}

trait ReferenceProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(renameProvider = true)
  def references(textDocument: TextDocumentIdentifier, position: Position, context: ReferenceContext): Future[Array[Location]]
}

trait DocumentHighlightProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(documentHighlightProvider = true)
  def documentHighlight(textDocument: TextDocumentIdentifier, position: Position): Future[Array[DocumentHighlight]]
}

trait DocumentSymbolProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(documentSymbolProvider = true)
  def documentSymbol(textDocument: TextDocumentIdentifier): Future[Array[SymbolInformation]]
}

trait WorkspaceSymbolProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(workspaceSymbolProvider = true)
  def symbol(query: String): Future[Array[SymbolInformation]]
}

trait CodeActionProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(codeActionProvider = true)
  def codeAction(textDocument: TextDocumentIdentifier, range: Range, context: CodeActionContext): Future[Array[Command]]
}

trait CodeLensProvider extends LanguageService {
  def codeLensOptions: CodeLensOptions
  override private[languageserver] def capabilities: ServerCapabilities = super.capabilities.copy(codeLensProvider = Some(codeLensOptions))
  def codeLens(textDocument: TextDocumentIdentifier): Future[Array[net.flatmap.vscode.languageserver.CodeLens]]
  def resolveCodeLens(codeLens: net.flatmap.vscode.languageserver.CodeLens): Future[net.flatmap.vscode.languageserver.CodeLens]
}

trait DocumentFormattingProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(documentFormattingProvider = true)
  def formatting(textDocument: TextDocumentIdentifier, options: FormattingOptions): Future[Array[TextEdit]]
}

trait DocumentRangeFormattingProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(documentRangeFormattingProvider = true)
  def rangeFormatting(textDocument: TextDocumentIdentifier, range: Range, options: FormattingOptions): Future[Array[TextEdit]]
}

trait DocumentOnTypeFormattingProvider extends LanguageService {
  def documentOnTypeFormattingOptions: DocumentOnTypeFormattingOptions
  override private[languageserver] def capabilities: ServerCapabilities = super.capabilities.copy(documentOnTypeFormattingProvider = Some(documentOnTypeFormattingOptions))
  def onTypeFormatting(textDocument: TextDocumentIdentifier, position: Position, ch: String, options: FormattingOptions): Future[Array[TextEdit]]
}

trait RenameProvider extends LanguageService {
  override private [languageserver] def capabilities = super.capabilities.copy(renameProvider = true)
  def rename(textDocument: TextDocumentIdentifier, position: Position, newName: String): Future[WorkspaceEdit]
}*/