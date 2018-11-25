package ru._2gis.api.controller

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler
import ru._2gis.api.view._
import ru._2gis.api.marshalling._

/** Кастомный обработчик для отрисовки исключений в стандартном json-формате */
trait CustomExceptionHandler {

  import Marshallers._

  protected implicit val customExceptionHandler: ExceptionHandler = ExceptionHandler {
    case ex => complete(Responses.internalError(ex))
  }

}
