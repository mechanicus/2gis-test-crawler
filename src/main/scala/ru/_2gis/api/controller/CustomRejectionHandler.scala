package ru._2gis.api.controller

import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.http.scaladsl.server.RejectionHandler
import argonaut._, Argonaut._
import ru._2gis.api.view._

/**
  * Кастомный `RejectionHandler`, который мапит стандартные сообщения
  * фреймворка об ошибках HTTP в json формат.
  */
trait CustomRejectionHandler {

  import JsonCodecs._

  protected implicit val rejectionHandler: RejectionHandler = RejectionHandler.default.mapRejectionResponse {
    case response@HttpResponse(_, _, entity: HttpEntity.Strict, _) =>
      val code = response.status.intValue()
      val status = response.status.reason.toUpperCase.replaceAll(" ", "_")
      val message = entity.data.utf8String
      response.withEntity(Responses.error(code, status, message).asJson.spaces4)
    case other => other
  }

}
