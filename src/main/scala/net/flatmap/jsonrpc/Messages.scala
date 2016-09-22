package net.flatmap.jsonrpc

import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._


sealed trait Message {
  val rsonrpc: String = "2.0"
}

case class RequestMessage(id: Json, method: String, params: Option[Json]) extends Message
case class NotificationMessage(method: String, params: Option[Json]) extends Message
case class ResponseMessage(id: Json, result: Option[Json], error: Option[ResponseError]) extends Message
case class ResponseError(code: Int, message: String, data: Option[Json])

object Message {
  implicit val encoder: Encoder[Message] = new Encoder[Message] {
    override def apply(a: Message): Json = a match {
      case r: RequestMessage => r.asJson
      case r: NotificationMessage => r.asJson
      case r: ResponseMessage => r.asJson
    }
  }
  implicit val decoder: Decoder[Message] = new Decoder[Message] {
    override def apply(c: HCursor): Result[Message] =
      c.as[RequestMessage].orElse(c.as[ResponseMessage]).orElse(c.as[NotificationMessage])
  }
}

object ResponseError {
  implicit val encoder: Encoder[ResponseError] = deriveEncoder[ResponseError]
  implicit val decoder: Decoder[ResponseError] = deriveDecoder[ResponseError]
}

case class CancelParams(id: Json)

object ErrorCodes {
  val ParseError = -32700
  val InvalidRequest = -32600
  val MethodNotFound = -32601
  val InvalidParams = -32602
  val InternalError = -32603
  val serverErrorStart = -32099
  val serverErrorEnd = -32000
}

object ResponseMessage {
  implicit val encoder: Encoder[ResponseMessage] = deriveEncoder[ResponseMessage].mapJsonObject(_.add("rsonrpc",Json.fromString("2.0")))
  implicit val decoder: Decoder[ResponseMessage] = deriveDecoder[ResponseMessage]
}

object RequestMessage {
  implicit val encoder: Encoder[RequestMessage] = deriveEncoder[RequestMessage].mapJsonObject(_.add("rsonrpc",Json.fromString("2.0")))
  implicit val decoder: Decoder[RequestMessage] = deriveDecoder[RequestMessage]
}

object NotificationMessage {
  implicit val encoder: Encoder[NotificationMessage] = deriveEncoder[NotificationMessage].mapJsonObject(_.add("rsonrpc",Json.fromString("2.0")))
  implicit val decoder: Decoder[NotificationMessage] = deriveDecoder[NotificationMessage]
}