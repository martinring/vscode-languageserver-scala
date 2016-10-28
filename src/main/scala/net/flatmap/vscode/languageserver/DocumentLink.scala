package net.flatmap.vscode.languageserver

import java.net.URI

/**
  * A document link is a range in a text document that links to an internal or external resource, like another
  * text document or a web site.
  */
case class DocumentLink(range: Range, target: URI)
