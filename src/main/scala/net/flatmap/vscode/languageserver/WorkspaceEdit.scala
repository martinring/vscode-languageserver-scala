package net.flatmap.vscode.languageserver

import java.net.URI

/**
  * A workspace edit represents changes to many resources managed in the
  * workspace.
  *
  * @param changes Holds changes to existing resources.
  */
case class WorkspaceEdit(changes: Map[URI,Seq[TextEdit]]) {
  /**
    * The number of affected resources.
    */
  def size: Int = changes.size

  /**
    * Replace the given range with given text for the given resource.
    *
    * @param uri A resource identifier.
    * @param range A range.
    * @param newText A string.
    */
  def replace(uri: URI, range: Range, newText: String): WorkspaceEdit =
    WorkspaceEdit(changes.updated(
      uri,
      changes.getOrElse(uri,Seq.empty) :+ TextEdit.replace(range,newText)
    ))

  /**
    * Insert the given text at the given position.
    *
    * @param uri A resource identifier.
    * @param position A position.
    * @param newText A string.
    */
  def insert(uri: URI, position: Position, newText: String): WorkspaceEdit =
    WorkspaceEdit(changes.updated(
      uri,
      changes.getOrElse(uri,Seq.empty) :+ TextEdit.insert(position,newText)
    ))

  /**
    * Delete the text at the given range.
    *
    * @param uri A resource identifier.
    * @param range A range.
    */
  def delete(uri: URI, range: Range): WorkspaceEdit =
    WorkspaceEdit(changes.updated(
     uri,
     changes.getOrElse(uri,Seq.empty) :+ TextEdit.delete(range)
   ))

  /**
    * Check if this edit affects the given resource.
    * @param uri A resource identifier.
    * @return `true` if the given resource will be touched by this edit.
    */
  def has(uri: URI): Boolean = changes.contains(uri)

  /**
    * Get the text edits for a resource.
    *
    * @param uri A resource identifier.
    * @return An array of text edits.
    */
  def get(uri: URI): Seq[TextEdit] = changes.getOrElse(uri,Seq.empty)
}

object WorkspaceEdit {
  /**
    * @return No op
    */
  def empty = WorkspaceEdit(Map.empty)
}