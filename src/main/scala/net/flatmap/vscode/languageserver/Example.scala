package net.flatmap.vscode.languageserver

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, FlowShape, IOResult}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Merge, Sink, Source, StreamConverters}
import akka.util.ByteString
import io.circe.Json
import net.flatmap.jsonrpc
import net.flatmap.jsonrpc._
import net.flatmap.jsonrpc.util.TypePartition

import scala.concurrent.{Future, Promise}

/**
  * Created by martin on 21/10/2016.
  */
object Example extends App{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher = system.dispatcher

  import Codec._

  val local = Local[LanguageServer]
  val remote = Remote[LanguageClient](Id.standard)

  val in = StreamConverters.fromInputStream(() => System.in)
  val out = StreamConverters.fromOutputStream(() => System.out)

  val connection = Connection.bidi[LanguageServer,LanguageClient with RemoteConnection](
    local,remote,new ExampleServer(_))

  val interface =
    in.viaMat(connection)(Keep.right).to(out).run()
}
