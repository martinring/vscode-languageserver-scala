package net.flatmap.vscode.languageserver

import java.net.URI

import scala.collection.mutable.ArrayBuffer

/**
  * A simple text document. Not to be implemenented.
  */
trait TextDocument {
  /**
    * The associated URI for this document. Most documents have the __file__-scheme, indicating that they
    * represent files on disk. However, some documents may have other schemes indicating that they are not
    * available on disk.
    */
  def uri: URI

  /**
    * The identifier of the language associated with this document.
    */
  def languageId: String

  /**
    * The version number of this document (it will strictly increase after each
    * change, including undo/redo).
    */
  def version: Int

  /**
    * Get the text of this document.
    */
  def text: String

  /**
    * Converts a zero-based offset to a position.
    *
    * @param offset A zero-based offset.
    * @return A valid [position](#Position).
    */
  def positionAt(offset: Int): Position

  /**
    * Converts the position to a zero-based offset.
    *
    * The position will be [adjusted](#TextDocument.validatePosition).
    *
    * @param position A position.
    * @return A valid zero-based offset.
    */
  def offsetAt(position: Position): Int

  /**
    * The number of lines in this document.
    */
  def lineCount: Int
}

/**
  * Text documents are identified using a URI. On the protocol level, URIs
  * are passed as strings. The corresponding JSON structure looks like this:
  *
  * @param uri The text document's URI.
  */
case class TextDocumentIdentifier(uri: URI)

/**
  * Represents a line of text, such as a line of source code.
  *
  * TextLine objects are __immutable__. When a [document](#TextDocument) changes,
  * previously retrieved lines will not represent the latest state.
  *
  * @param lineNumber The zero-based line number.
  * @param text       The text of this line without the line separator characters.
  * @param range      The range this line covers without the line separator characters.
  * @param rangeIncludingLineBreak The range this line covers with the line separator characters.
  */
case class TextLine(lineNumber: Int,
                    text: String,
                    range: Range,
                    rangeIncludingLineBreak: Range) {
  /**
    * The offset of the first character which is not a whitespace character as defined
    * by `/\s/`. **Note** that if a line is all whitespaces the length of the line is returned.
    */
  def firstNonWhitespaceCharacterIndex: Int = text.indexWhere(!_.isWhitespace)

  /**
    * Whether this line is whitespace only
    */
  def isEmptyOrWhitespace: Boolean = text.forall(_.isWhitespace)
}

/**
  * An item to transfer a text document from the client to the server.
  *
  * @param uri        The text document's URI.
  * @param languageId The text document's language identifier.
  * @param version    The version number of this document (it will strictly
  *                   increase after each change, including undo/redo).
  * @param text       The content of the opened text document.
  */
case class TextDocumentItem(uri: URI,
                            languageId: String,
                            version: Int,
                            text: String) extends TextDocument {
  /*def lines = text.lines.zipWithIndex.map {
    case (text,n) =>
      TextLine(
        n,
        text,
        Range(Position(n,0),Position(n,text.length)))
  }*/

  private lazy val lines = text.lines.toArray :+ ""

  /**
    * Returns a text line denoted by the line number. Note
    * that the returned object is *not* live and changes to the
    * document are not reflected.
    *
    * @param line A line number in [0, lineCount).
    * @return A [line](#TextLine).
    */
  def lineAt(line: Int): Option[TextLine] = {
    if (line < 0 || line >= lineCount) None
    else {
      val lineText = lines(line)
      Some(TextLine(
        lineNumber = line,
        text = lineText,
        range = Range(Position(line,0),Position(line,lineText.length)),
        rangeIncludingLineBreak = Range(Position(line,0),validatePosition(Position(line + 1,0)))
      ))
    }
  }

  /**
    * Returns a text line denoted by the position. Note
    * that the returned object is *not* live and changes to the
    * document are not reflected.
    *
    * The position will be [adjusted](#TextDocument.validatePosition).
    *
    * @see [TextDocument.lineAt](#TextDocument.lineAt)
    * @param position A position.
    * @return A [line](#TextLine).
    */
  def lineAt(position: Position): TextLine =
    lineAt(validatePosition(position).line).get

  private def isNewline(i: Int) =
    if (i >= text.length) false else text.charAt(i) match {
      case '\r' => (i + 1 == text.length) || text.charAt(i + 1) != '\n'
      case x => x == '\n'
    }

  private lazy val lineIndices: Array[Int] = {
    val buf = new ArrayBuffer[Int]
    buf += 0
    for (i <- 0 until text.length) if (isNewline(i)) buf += i + 1
    buf += text.length // sentinel, so that findLine below works smoother
    buf.toArray
  }

  private var lastLine = 0

  /**
    * Converts a zero-based offset to a position.
    *
    * @param offset A zero-based offset.
    * @return A valid [position](#Position).
    */
  def positionAt(offset: Int): Position =
    if (offset < 0) Position(0,0)
    else if (offset >= text.length) Position(lineCount - 1, text.length - lineIndices(lineCount - 1))
    else {
      val lines = lineIndices
      def findLine(lo: Int, hi: Int, mid: Int): Int =
        if (offset < lines(mid))
          findLine(lo, mid - 1, (lo + mid - 1) / 2)
        else if (offset >= lines(mid + 1))
          findLine(mid + 1, hi, (mid + 1 + hi) / 2)
        else mid
      lastLine = findLine(0,lines.length,lastLine)
      val idx = lines(lastLine)
      Position(lastLine, Math.max(0,Math.min(text.length - idx, offset - idx)))
    }

  /**
    * Converts the position to a zero-based offset.
    *
    * The position will be [adjusted](#TextDocument.validatePosition).
    *
    * @param position A position.
    * @return A valid zero-based offset.
    */
  def offsetAt(position: Position): Int = {
    val validated = validatePosition(position)
    lineIndices(validated.line) + validated.character
  }

  /**
    * The number of lines in this document.
    */
  lazy val lineCount: Int = lineIndices.length - 1

  /**
    * Ensure a range is completely contained in this document.
    *
    * @param range A range.
    * @return The given range or a new, adjusted range.
    */
  def validateRange(range: Range): Range =
    range.copy(
      start = validatePosition(range.start),
      end = validatePosition(range.end))

  /**
    * Ensure a position is contained in the range of this document.
    *
    * @param position A position.
    * @return The given position or a new, adjusted position.
    */
  def validatePosition(position: Position): Position = {
    if (position.line < 0)
      Position(0,0)
    else if (position.line >= lineCount)
      Position(lineCount - 1, text.length - lineIndices(lineCount - 1))
    else {
      if (position.character < 0) Position(position.line,0)
      else {
        val lineLength = lines(position.line).length
        if (position.character >= lineLength) position.copy(character = lineLength)
        else position
      }
    }
  }
}

/**
  * An identifier to denote a specific version of a text document.
  *
  * @param uri     The text document's URI.
  * @param version The version number of this document.
  */
case class VersionedTextDocumentIdentifier(uri: URI, version: Int)

/**
  * An event describing a change to a text document. If range and rangeLength
  * are omitted the new text is considered to be the full content of the
  * document.
  *
  * @param range       The range of the document that changed.
  * @param rangeLength The length of the range that got replaced.
  * @param text        The new text of the document.
  */
case class TextDocumentContentChangeEvent(text: String,
                                          range: Option[Range] = None,
                                          rangeLength: Option[Int] = None)

/**
  * Represents reasons why a text document is saved.
  */
sealed trait TextDocumentSaveReason
object TextDocumentSaveReason {
  /**
    * Manually triggered, e.g. by the user pressing save, by starting debugging,
    * or by an API call.
    */
  case object Manual extends TextDocumentSaveReason
  /**
    * Automatic after a delay.
    */
  case object AfterDelay extends TextDocumentSaveReason
  /**
    * When the editor lost focus.
    */
  case object FocusOut extends TextDocumentSaveReason
}