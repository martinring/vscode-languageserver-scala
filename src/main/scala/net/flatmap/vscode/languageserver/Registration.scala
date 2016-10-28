package net.flatmap.vscode.languageserver

import akka.actor.Cancellable

import scala.concurrent.{ExecutionContext, Future}

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
  def register(id: String,
               method: String,
               registerOptions: DocumentOptions)
              (implicit ec: ExecutionContext): Future[Cancellable] =
    registrationRequest(id,method,registerOptions).map { x =>
      new Cancellable {
        private var cancelled = false
        def isCancelled: Boolean = cancelled
        def cancel(): Boolean = {
          unregistrationRequest(id,method).map(_ => cancelled = true)
          true
        }
      }
    }

  /**
    * Register the given request or notification on the other side. Since requests can be sent from the client
    * to the server and vice versa this request can be sent into both directions.
    *
    * @param id              The id used to register the request.
    *                        The id can be used to deregister the request again.
    * @param method          The method to register for.
    * @param registerOptions Options necessary for the registration.
    */
  def registrationRequest(id: String,
                          method: String,
                          registerOptions: DocumentOptions): Future[Unit]

  /**
    * Unregisters the given request on the other side.
    * @param id  The id used to unregister the request or notification. Usually an id
    *            provided during the register request.
    * @param method The method to unregister for.
    */
  def unregistrationRequest(id: String,
                            method: String): Future[Unit]
}