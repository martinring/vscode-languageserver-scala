package net.flatmap.vscode.languageserver

import akka.stream.Materializer
import io.circe._
import io.circe.generic.auto._
import net.flatmap.jsonrpc._
import io.circe.syntax._
import net.flatmap.jsonrpc.{Connection, ErrorCodes, ResponseError, ResponseMessage}

import scala.concurrent.{ExecutionContext, Future, Promise}

class LanguageServer(connection: Connection, provider: LanguageServiceProvider) {
  private val service = Promise[LanguageService]
  def run()(implicit executor: ExecutionContext, materializer: Materializer) = connection.run {
    case RequestMessage(id, "initialize", Some(params)) =>
      params.as[InitializeParams].foreach { x =>
        service.completeWith(provider.initialize(x.processId, x.rootPath, x.initializeOptions, x.capabilities))
        service.future.onSuccess {
          case x => connection.send(ResponseMessage(id,Some(x.capabilities.asJson),None))
        }
      }
    case RequestMessage(id,method,Some(params)) =>
      val result = (method,service.future.value) match {
        case ("workspace/symbol",Some(service: WorkspaceSymbolProvider)) =>
          params.as[WorkspaceSymbolParams].map(x => service.symbol(x.query).map(_.asJson))
        case ("codeLens/resolve",Some(service: CodeLensProvider)) =>
          params.as[CodeLens].map(x => service.resolveCodeLens(x).map(_.asJson))
        case ("textDocument/completion",Some(service: CompletionProvider)) =>
          params.as[TextDocumentPositionParams].map(x => service.completion(x.textDocument,x.position).map(_.asJson))
        case ("completionItem/resolve", Some(service: CompletionProvider)) =>
          params.as[CompletionItem].map(x => service.resolveCompletionItem(x).map(_.asJson))
        case ("textDocument/hover", Some(service: HoverProvider)) =>
          params.as[TextDocumentPositionParams].map(x => service.hover(x.textDocument,x.position).map(_.asJson))
        case ("textDocument/signatureHelp", Some(service: SignatureHelpProvider)) =>
          params.as[TextDocumentPositionParams].map(x => service.signatureHelp(x.textDocument,x.position).map(_.asJson))
        case ("textDocument/definition", Some(service: DefinitionProvider)) =>
          params.as[TextDocumentPositionParams].map(x => service.definition(x.textDocument,x.position).map(_.asJson))
        case ("textDocument/references", Some(service: ReferenceProvider)) =>
          params.as[ReferenceParams].map(x => service.references(x.textDocument,x.position,x.context).map(_.asJson))
        case ("textDocument/documentHighlight", Some(service: DocumentHighlightProvider)) =>
          params.as[TextDocumentPositionParams].map(x => service.documentHighlight(x.textDocument,x.position).map(_.asJson))
        case ("textDocument/documentSymbol", Some(service: DocumentSymbolProvider)) =>
          params.as[DocumentSymbolParams].map(x => service.documentSymbol(x.textDocument).map(_.asJson))
        case ("textDocument/codeAction", Some(service: CodeActionProvider)) =>
          params.as[CodeActionParams].map(x => service.codeAction(x.textDocument,x.range,x.context).map(_.asJson))
        case ("textDocument/codeLens", Some(service: CodeLensProvider)) =>
          params.as[CodeLensParams].map(x => service.codeLens(x.textDocument).map(_.asJson))
        case ("textDocument/formatting", Some(service: DocumentFormattingProvider)) =>
          params.as[DocumentFormattingParams].map(x => service.formatting(x.textDocument,x.options).map(_.asJson))
        case ("textDocument/rangeFormatting", Some(service: DocumentRangeFormattingProvider)) =>
          params.as[DocumentRangeFormattingParams].map(x => service.rangeFormatting(x.textDocument,x.range,x.options).map(_.asJson))
        case ("textDocument/onTypeFormatting", Some(service: DocumentOnTypeFormattingProvider)) =>
          params.as[DocumentOnTypeFormattingParams].map(x => service.onTypeFormatting(x.textDocument,x.position,x.ch,x.options).map(_.asJson))
        case ("textDocument/rename", Some(service: RenameProvider)) =>
          params.as[RenameParams].map(x => service.rename(x.textDocument,x.position,x.newName).map(_.asJson))
      }
      val f = result.fold(failure =>
        Future.successful(ResponseMessage(id, None, Some(ResponseError(ErrorCodes.InvalidParams, failure.message, None))))
                           , x =>
          x.map(x => ResponseMessage(id, Some(x), None))
                         )
      f.foreach(connection.send)
    case RequestMessage(id,method,_) => method match {
      case "shutdown" =>
        service.future.foreach(_.shutdown().foreach(_ => connection.send(
          ResponseMessage(id,None,None)
        )))
    }
    case NotificationMessage(method,params) => method match {
      case "exit" =>
      case "workspace/didChangeConfiguration" =>
        service.future.foreach(server =>
        params.foreach(_.as[DidChangeConfigurationParams].map(x => server.didChangeConfiguration(x.settings))))
      case "workspace/didChangeWatchedFiles" =>
        service.future.foreach(server =>
        params.foreach(_.as[DidChangeWatchedFilesParams].map(x => server.didChangeWatchedFiles(x.changes))))
      case "textDocument/didOpen" =>
        service.future.foreach(server =>
        params.foreach(_.as[DidOpenTextDocumentParams].map(x => server.didOpen(x.textDocument))))
      case "textDocument/didChange" =>
        service.future.foreach(server =>
        params.foreach(_.as[DidChangeTextDocumentParams].map(x => server.didChange(x.textDocument,x.contentChanges))))
      case "textDocument/didClose" =>
        service.future.foreach(server =>
        params.foreach(_.as[DidCloseTextDocumentParams].map(x => server.didClose(x.textDocument))))
      case "textDocument/didSave" =>
        service.future.foreach(server =>
        params.foreach(_.as[DidSaveTextDocumentParams].map(x => server.didSave(x.textDocument))))
      case "$/cancelRequest" => // TODO
    }
  }
}

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
}