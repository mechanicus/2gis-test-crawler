package ru._2gis.api.marshalling

import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import argonaut._, Argonaut._
import ru._2gis.api.view._


object Marshallers {

  import JsonCodecs._

  implicit def jsonResponseMarshaller[A : EncodeJson]: ToResponseMarshaller[JsonResponse[A]] = {
    Marshaller.withFixedContentType(ContentTypes.`application/json`) { response =>
      HttpResponse(StatusCodes.custom(response.code, response.status), entity = HttpEntity(response.asJson.spaces4))
    }
  }

}
