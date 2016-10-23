package net.flatmap.vscode.languageserver

import io.circe.Json
import net.flatmap.jsonrpc._

import scala.concurrent.{ExecutionContext, Future}

class ExampleServer(client: Future[LanguageClient])(implicit ec: ExecutionContext)
  extends LanguageServer {

  /**
    * The initialize request is sent as the first request from the client to
    * the server.
    *
    * @param processId             The process Id of the parent process that started the
    *                  server. Is None if the process has not been started by
    *                              another process. If the parent process is not alive
    *                              then the server should exit (see exit notification)
    *                              its process.
    * @param rootPath              The rootPath of the workspace. Is None if no folder is
    *                              open.
    * @param initializationOptions User provided initialization options.
    * @param capabilities          The capabilities provided by the client (editor)
    */
  def initialize(processId: Option[Int],
                 rootPath: Option[String],
                 initializationOptions: Option[Json],
                 capabilities: ClientCapabilities): Future[InitializeResult]
  = {
    client.map { client =>
      client.window.showMessage(MessageType.Info,"Hello from Scala!")
      InitializeResult(ServerCapabilities())
    }
  }

  /** The shutdown request is sent from the client to the server. It asks the
    * server to shut down, but to not exit (otherwise the response might not be
    * delivered correctly to the client). There is a separate exit
    * notification that asks the server to exit.
    *
    * @return code and message set in case an exception happens during
    *         shutdown request.
    */
  def shutdown(): Future[Unit] = ???

  /**
    * A notification to ask the server to exit its process. The server should
    * exit with success code 0 if the shutdown request has been received
    * before; otherwise with error code 1.
    */
  def exit(): Unit = println("exit")

  @JsonRPCNamespace(prefix = "textDocument/")
  def textDocument: LanguageServer.TextDocumentOperations = ???

  @JsonRPCNamespace(prefix = "completionItem/")
  def completionItem: LanguageServer.CompletionItemOperations = ???

  @JsonRPCNamespace(prefix = "workspace/")
  def workspace: LanguageServer.WorkspaceOperations = ???

  @JsonRPCNamespace(prefix = "codeLens/")
  def codeLens: LanguageServer.CodeLensOperations = ???
}
