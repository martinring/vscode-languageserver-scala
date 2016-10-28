package net.flatmap.vscode.languageserver

import io.circe.Json
import net.flatmap.jsonrpc.JsonRPC

import scala.concurrent.Future

trait LanguageServer {
  def textDocumentSyncKind: TextDocumentSyncKind = TextDocumentSyncKind.None
  def capabilities: ServerCapabilities= ServerCapabilities(Some(textDocumentSyncKind))

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
                 capabilities: ClientCapabilities,
                 trace: Option[Trace]): Future[InitializeResult]

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

  /**
    * The document open notification is sent from the client to the server
    * to signal newly opened text documents. The document's truth is now
    * managed by the client and the server must not try to read the
    * document's truth using the document's uri.
    *
    * @param textDocument The document that was opened.
    */
  @JsonRPC.Named("textDocument/didOpen")
  def didOpen(textDocument: TextDocumentItem)

  /**
    * The document change notification is sent from the client to the
    * server to signal changes to a text document. In 2.0 the shape of the
    * params has changed to include proper version numbers and language ids.
    *
    * @param textDocument   The document that did change. The version
    *                       number points to the version after all provided
    *                       content changes have been applied.
    * @param contentChanges The actual content changes.
    */
  @JsonRPC.Named("textDocument/didChange")
  def didChange(textDocument: VersionedTextDocumentIdentifier,
                contentChanges: Seq[TextDocumentContentChangeEvent])

  /**
    * The document close notification is sent from the client to the server
    * when the document got closed in the client. The document's truth now
    * exists where the document's uri points to (e.g. if the document's uri
    * is a file uri the truth now exists on disk).
    *
    * @param textDocument The document that was closed.
    */
  @JsonRPC.Named("textDocument/didClose")
  def didClose(textDocument: TextDocumentIdentifier)

  /**
    * The document save notification is sent from the client to the server
    * when the document was saved in the client.
    *
    * @param textDocument The document that was saved.
    */
  @JsonRPC.Named("textDocument/didSave")
  def didSave(textDocument: TextDocumentIdentifier)

  /**
    * The watched files notification is sent from the client to the server
    * when the client detects changes to files watched by the language client.
    * @param changes The actual file events.
    */
  @JsonRPC.Named("textDocument/didChangeWatchedFiles")
  def didChangeWatchedFiles(changes: Seq[FileEvent])

  /**
    * A notification sent from the client to the server to signal the change
    * of configuration settings.
    * @param settings The actual changed settings
    */
  @JsonRPC.Named("workspace/didChangeConfiguration")
  def didChangeConfiguration(settings: Json)
}