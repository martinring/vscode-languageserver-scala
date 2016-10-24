package net.flatmap.vscode.languageserver

/**
  * Represents a parameter of a callable-signature. A parameter can have a
  * label and a doc-comment.
  * @param label         The label of this signature. Will be shown in the UI.
  * @param documentation The human-readable doc-comment of this signature.
  *                      Will be shown in the UI but can be omitted.
  */
case class ParameterInformation(label: String,
                                documentation: Option[String])

/**
  * Represents the signature of something callable. A signature can have a
  * label, like a function-name, a doc-comment, and a set of parameters.
  * @param label         The label of this signature. Will be shown in the UI.
  * @param documentation The human-readable doc-comment of this signature.
  *                      Will be shown in the UI but can be omitted.
  * @param parameters    The parameters of this signature.
  */
case class SignatureInformation(label: String,
                                documentation: Option[String],
                                parameters: Option[Seq[ParameterInformation]])


/**
  * Signature help represents the signature of something callable. There can
  * be multiple signature but only one active and only one active parameter.
  *
  * @param signatures      One or more signatures.
  * @param activeSignature The active signature.
  * @param activeParameter The active parameter of the active signature.
  */
case class SignatureHelp(signatures: Seq[SignatureInformation],
                         activeSignature: Option[Int],
                         activeParameter: Option[Int])
