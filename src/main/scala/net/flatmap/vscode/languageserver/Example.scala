package net.flatmap.vscode.languageserver

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import io.circe.Json
import net.flatmap.jsonrpc._

import scala.concurrent.{Future, Promise}

class ExampleServer(client: Future[LanguageClient]) extends LanguageServer {
  override def initialize(processId: Option[Int],
                          rootPath: Option[String],
                          initializationOptions: Option[Json],
                          capabilities: ClientCapabilities):
    Future[InitializeResult] = client.map { client =>
    InitializeResult(ServerCapabilities())
  }

  override def shutdown(): Future[Unit] = ???
  override def exit(): Unit = ???
  override val textDocument: LanguageServer.TextDocument = _
  override val completionItem: LanguageServer.CompletionItem = _
  override val workspace: LanguageServer.Workspace = _
  override val codeLens: LanguageServer.CodeLens = _
}

object Example extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher = system.dispatcher

  val clientInterface = Promise[LanguageClient]
  val remote          = LanguageClient.messageFlow
  val server          = new ExampleServer(clientInterface.future)
  def local           = Local[LanguageServer](server)

  val connection = Connection.create(local,remote)

  val in  = StreamConverters.fromInputStream(() => System.in)
  val out = StreamConverters.fromOutputStream(() => System.out)

  clientInterface.trySuccess(Connection.open(in,out,connection))
}
