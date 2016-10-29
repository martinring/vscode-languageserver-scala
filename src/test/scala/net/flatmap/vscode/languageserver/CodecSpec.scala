package net.flatmap.vscode.languageserver

import cats.data.Xor
import org.scalatest._
import io.circe.parser._

class CodecSpec extends FunSuite with Matchers {
  override def suiteName: String = "Codec Validator"

  test("Missing Seqs") {
    val json = parse(
      """
        |{
        |  "title": "foo",
        |  "command": "bar"
        |}
      """.stripMargin
    )

    val res = for {
      json <- json
      command <- Codec.decodeCommand.decodeJson(json)
    } yield command

    res shouldBe (Xor.Right(Command("foo", "bar", Seq.empty)))
  }
}

