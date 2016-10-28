package net.flatmap.vscode.languageserver

import javax.swing.text.DocumentFilter

import net.flatmap.jsonrpc.JsonRPC

case class DocumentFilter(language: Option[String],
                          scheme: Option[String],
                          pattern: Option[String])

object DocumentFilter {
  def language(language: String) = DocumentFilter(Some(language),None,None)
  def scheme(scheme: String) = DocumentFilter(None,Some(scheme),None)
  def pattern(pattern: String) = DocumentFilter(None,None,Some(pattern))
}

case class DocumentSelector(filters: DocumentFilter*)

case class DocumentOptions(selector: Option[DocumentSelector])

trait Registration {
  @JsonRPC.Named("client/registrationRequest")
  def register(id: String,
               method: String,
               registerOptions: DocumentOptions)
}