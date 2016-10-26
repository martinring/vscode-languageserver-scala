package net.flatmap.vscode.languageserver

import java.net.URI


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
  */
case class TextLine(lineNumber: Int,
                    text: String,
                    range: Range) {
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
                            text: String) {
  def lines: Iterable[TextLine] =
    Util.unfold[TextLine,(String,Int)]((text,0)) {
      case ("",n) => None
      case (text,n) =>
        Util.lineBreak.findFirstMatchIn(text).fold {
          Some(TextLine(n,text,Range(n,0,n,text.length)), ("", n+1))
        } { case m =>
          Some(TextLine(n,text.take(m.start),Range(n,0,n,m.start)), (text.drop(m.end), n+1))
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