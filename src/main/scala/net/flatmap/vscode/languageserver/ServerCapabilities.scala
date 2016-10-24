package net.flatmap.vscode.languageserver

/**
  * Defines how the host (editor) should sync document changes to the
  * language  server.
  */
sealed trait TextDocumentSyncKind
object TextDocumentSyncKind {
  /** Documents should not be synced at all. */
  object None extends TextDocumentSyncKind

  /** Documents are synced by always sending the full content of the
    * document. */
  object Full extends TextDocumentSyncKind

  /** Documents are synced by sending the full content on open. After that
    * only incremental updates to the document are sent. */
  object Incremental extends TextDocumentSyncKind
}

/**
  * Completion options.
  * @param resolveProvider   The server provides support to resolve
  *                          additional information for a completion item.
  * @param triggerCharacters The characters that trigger completion
  *                          automatically.
  */
case class CompletionOptions(resolveProvider: Option[Boolean] = None,
                             triggerCharacters: Option[Seq[String]] = None)

/**
  * Signature help options.
  * @param triggerCharacters The characters that trigger signature help
  *                          automatically.
  */
case class SignatureHelpOptions(triggerCharacters: Option[Seq[String]] = None)

/**
  * Code Lens options.
  * @param resolveProvider Code lens has a resolve provider as well.
  */
case class CodeLensOptions(resolveProvider: Option[Boolean] = None)

/**
  * Format document on type options
  * @param firstTriggerCharacter A character on which formatting should be
  *                              triggered, like `}`.
  * @param moreTriggerCharacter  More trigger characters.
  */
case class DocumentOnTypeFormattingOptions(
  firstTriggerCharacter: String,
  moreTriggerCharacter: Option[Seq[String]] = None)

/**
  * The server can signal the following capabilities
  * @param textDocumentSync                 Defines how text documents are
  *                                         synced.
  * @param hoverProvider                    The server provides hover support.
  * @param completionProvider               The server provides completion
  *                                         support.
  * @param signatureHelpProvider            The server provides signature help
  *                                         support.
  * @param definitionProvider               The server provides goto
  *                                         definition support.
  * @param referenceProvider                The server provides find
  *                                         references support.
  * @param documentHighlightProvider        The server provides document
  *                                         highlight support.
  * @param documentSymbolProvider           The server provides document
  *                                         symbol support.
  * @param workspaceSymbolProvider          The server provides workspace
  *                                         symbol support.
  * @param codeActionProvider               The server provides code actions.
  * @param codeLensProvider                 The server provides code lens.
  * @param documentFormattingProvider       The server provides document
  *                                         formatting.
  * @param documentRangeFormattingProvider  The server provides document
  *                                         range formatting.
  * @param documentOnTypeFormattingProvider The server provides document
  *                                         formatting on typing.
  * @param renameProvider                   The server provides rename support.
  */
case class ServerCapabilities(
  textDocumentSync: Option[TextDocumentSyncKind] = None,
  hoverProvider: Option[Boolean] = None,
  completionProvider: Option[CompletionOptions] = None,
  signatureHelpProvider: Option[SignatureHelpOptions] = None,
  definitionProvider: Option[Boolean] = None,
  referenceProvider: Option[Boolean] = None,
  documentHighlightProvider: Option[Boolean] = None,
  documentSymbolProvider: Option[Boolean] = None,
  workspaceSymbolProvider: Option[Boolean] = None,
  codeActionProvider: Option[Boolean] = None,
  codeLensProvider: Option[CodeLensOptions] = None,
  documentFormattingProvider: Option[Boolean] = None,
  documentRangeFormattingProvider: Option[Boolean] = None,
  documentOnTypeFormattingProvider: Option[DocumentOnTypeFormattingOptions] = None,
  renameProvider: Option[Boolean] = None)

/**
  * @param retry Indicates whether the client should retry to send the
  *              initilize request after showing the message provided in the
  *              ResponseError.
  */
case class InitializeError(retry: Boolean)

/**
  * @param capabilities The capabilities the language server provides.
  */
case class InitializeResult(capabilities: ServerCapabilities)