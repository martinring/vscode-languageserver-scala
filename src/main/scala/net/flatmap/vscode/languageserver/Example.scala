package net.flatmap.vscode.languageserver

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, StreamConverters}
import akka.util.ByteString
import io.circe.Json
import net.flatmap.jsonrpc
import net.flatmap.jsonrpc._

import scala.concurrent.{Future, Promise}

/**
  * Created by martin on 21/10/2016.
  */
object Example extends App{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher = system.dispatcher

  val in = Source.single(Request(Id.Long(0),"initialize",NamedParameters(Map(
    "capabilities" -> Json.obj()
  ))))
    .via(jsonrpc.Codec.encoder)
    .via(Flow.fromFunction(jsonrpc.Codec.jsonPrinter.pretty))
    .via(Framing.byteStringFramer)
    .via(Flow.fromFunction[ByteString,ByteString] { bs =>
      println(bs.decodeString(StandardCharsets.UTF_8))
      bs
    })

  import Codec._

  val client = Promise[LanguageClient]
  val local = Local[LanguageServer](new ExampleServer(client.future))
  val remote = Remote[LanguageClient](Id.standard)
  val out = Sink.foreach[ByteString](x => println(x.decodeString(StandardCharsets.UTF_8)))

  val con = Connection.create(local,remote)

  client.success(Connection.open(in,out,con))

  readLine()
  system.terminate()
}
