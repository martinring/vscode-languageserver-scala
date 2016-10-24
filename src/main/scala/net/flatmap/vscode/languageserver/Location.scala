package net.flatmap.vscode.languageserver

import java.net.URI

/**
  * Represents a location inside a resource, such as a line inside a text file.
  *
  * @param uri   The resource identifier of this location.
  * @param range The document range of this location.
  */
case class Location(uri: URI, range: Range)

object Location {
  def apply(uri: URI, position: Position): Location =
    Location(uri,Range(position,position))
}