package net.flatmap.vscode.languageserver

import java.net.URI

import org.scalatest._

class TextDocumentSpec extends FunSuite with Matchers {
  override def suiteName: String = "Text Document Lines Model Validator"

  def newDocument(str: String) =
    TextDocumentItem(URI.create("file://foo/bar"),"text",0,str)

  test("Single Line") {
    val str = "Hello World";
    val lm = newDocument(str)
    lm.lineCount shouldBe 1
    for (i <- 0 to str.length) {
      lm.offsetAt(Position(0,i)) shouldBe i
      lm.positionAt(i) shouldBe Position(0,i)
    }
  }

  test("Multiple Lines") {
    val str = "ABCDE\nFGHIJ\nKLMNO\n"
    val lm = newDocument(str)
    lm.lineCount shouldBe 4
    for (i <- 0 to str.length) {
      val line = i / 6
      val column = i % 6
      lm.offsetAt(Position(line,column)) shouldBe i
      lm.positionAt(i) shouldBe Position(line,column)
    }
    lm.offsetAt(Position(3,0)) shouldBe 18
    lm.offsetAt(Position(3,1)) shouldBe 18
    lm.positionAt(18) shouldBe Position(3,0)
    lm.positionAt(19) shouldBe Position(3,0)
  }

  test("New line characters") {
    var str = "ABCDE\rFGHIJ"
    newDocument(str).lineCount shouldBe 2

    str = "ABCDE\nFGHIJ"
    newDocument(str).lineCount shouldBe 2

    str = "ABCDE\r\nFGHIJ"
    newDocument(str).lineCount shouldBe 2

    str = "ABCDE\n\nFGHIJ"
    newDocument(str).lineCount shouldBe 3

    str = "ABCDE\r\rFGHIJ"
    newDocument(str).lineCount shouldBe 3

    str = "ABCDE\n\rFGHIJ"
    newDocument(str).lineCount shouldBe 3
  }

  test("Invalid inputs") {
    val str = "Hello World";
    val lm = newDocument(str);

    // invalid position
    lm.offsetAt(Position(0,str.length)) shouldBe str.length
    lm.offsetAt(Position(0,str.length + 1)) shouldBe str.length
    lm.offsetAt(Position(2,3)) shouldBe str.length
    lm.offsetAt(Position(-1,3)) shouldBe 0
    lm.offsetAt(Position(0,-3)) shouldBe 0
    lm.offsetAt(Position(1,-3)) shouldBe str.length

    // invalid offsets
    lm.positionAt(-1) shouldBe Position(0,0)
    lm.positionAt(str.length) shouldBe Position(0,str.length)
    lm.positionAt(str.length + 3) shouldBe Position(0,str.length)
  }

  test("Lines") {
    val str = "ABCDE\nFGHIJ\nKLMNO\n"
    val lm = newDocument(str)

    lm.lineAt(-1) shouldBe None
    lm.lineAt(0).get.text shouldBe "ABCDE"
    lm.lineAt(1).get.text shouldBe "FGHIJ"
    lm.lineAt(2).get.text shouldBe "KLMNO"
    lm.lineAt(3).get.text shouldBe ""
    lm.lineAt(4) shouldBe None

    lm.lineAt(0).get.range shouldBe Range(Position(0,0),Position(0,5))
    lm.lineAt(1).get.range shouldBe Range(Position(1,0),Position(1,5))
    lm.lineAt(2).get.range shouldBe Range(Position(2,0),Position(2,5))
    lm.lineAt(3).get.range shouldBe Range(Position(3,0),Position(3,0))

    lm.lineAt(3).get.rangeIncludingLineBreak shouldBe Range(Position(3,0),Position(3,0))

  }
}
