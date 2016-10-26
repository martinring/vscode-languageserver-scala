package net.flatmap.vscode.languageserver

import java.net.URI

/**
  * The file event type.
  */
sealed trait FileChangeType
object FileChangeType {
  /** The file got created */
  case object Created extends FileChangeType
  /** The file got changed */
  case object Changed extends FileChangeType
  /** The file got deleted */
  case object Deleted extends FileChangeType
}

/**
  * An event describing a file change
  * @param uri            The file's URI
  * @param fileChangeType The change type
  */
case class FileEvent(uri: URI, fileChangeType: FileChangeType)
