package net.flatmap.vscode.languageserver

import io.circe.{Decoder, Encoder}

private object Enum {
  def apply[T](values: T*): (Encoder[T],Decoder[T]) = {
    val encode =
      Encoder.encodeInt.contramap(values.zipWithIndex.toMap.mapValues(_ + 1))
    val decode =
      Decoder.decodeInt.map(_ - 1).map(values)
    (encode,decode)
  }
}
