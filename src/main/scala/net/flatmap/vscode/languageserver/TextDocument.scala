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
                            text: String)

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
