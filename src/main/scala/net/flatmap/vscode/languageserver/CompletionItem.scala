package net.flatmap.vscode.languageserver

import io.circe.Json


/**
  * The kind of a completion entry.
  */
sealed trait CompletionItemKind
object CompletionItemKind {
  case object Text extends CompletionItemKind
  case object Method extends CompletionItemKind
  case object Function extends CompletionItemKind
  case object Constructor extends CompletionItemKind
  case object Field extends CompletionItemKind
  case object Variable extends CompletionItemKind
  case object Class extends CompletionItemKind
  case object Interface extends CompletionItemKind
  case object Module extends CompletionItemKind
  case object Property extends CompletionItemKind
  case object Unit extends CompletionItemKind
  case object Value extends CompletionItemKind
  case object Enum extends CompletionItemKind
  case object Keyword extends CompletionItemKind
  case object Snippet extends CompletionItemKind
  case object Color extends CompletionItemKind
  case object File extends CompletionItemKind
  case object Reference extends CompletionItemKind
}

/**
  * @param label         The label of this completion item. By default also
  *                      the text that is inserted when selecting this
  *                      completion.
  * @param kind          The kind of this completion item. Based of the kind
  *                      an icon is chosen by the editor.
  * @param detail        A human-readable string with additional information
  *                      about this item, like type or symbol information.
  * @param documentation A human-readable string that represents a doc-comment.
  * @param sortText      A string that shoud be used when comparing this item
  *                      with other items. When `falsy` the label is used.
  * @param filterText    A string that should be used when filtering a set of
  *                      completion items. When `falsy` the label is used.
  * @param insertText    A string that should be inserted a document when
  *                      selecting this completion. When `falsy` the label is
  *                      used.
  * @param textEdit      An edit which is applied to a document when selecting
  *                      this completion. When an edit is provided the value of
  *                      insertText is ignored.
  * @param additionalTextEdits An optional array of additional text edits
  *                            that are applied when selecting this
  *                            completion. Edits must not overlap with the
  *                            main edit nor with themselves.
  * @param command       An optional command that is executed *after*
  *                      inserting this completion. *Note* that additional
  *                      modifications to the current document should be
  *                      described with the additionalTextEdits-property.
  * @param data          An data entry field that is preserved on a completion
  *                      item between a completion and a completion resolve
  *                      request.
  */
case class CompletionItem(
  label: String,
  kind: Option[CompletionItemKind] = None,
  detail: Option[String] = None,
  documentation: Option[String] = None,
  sortText: Option[String] = None,
  filterText: Option[String] = None,
  insertText: Option[String] = None,
  textEdit: Option[TextEdit] = None,
  additionalTextEdits: Option[Seq[TextEdit]] = None,
  command: Option[Command] = None,
  data: Option[Json] = None)

/**
  * Represents a collection of [completion items](#CompletionItem) to be
  * presented in the editor.
  *
  * @param isIncomplete This list it not complete. Further typing should
  *                     result in recomputing this list.
  */
case class CompletionList(items: Seq[CompletionItem],
                          isIncomplete: Boolean = false)
