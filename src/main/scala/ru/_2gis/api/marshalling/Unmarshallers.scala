package ru._2gis.api.marshalling

import java.net.URL

import akka.http.scaladsl.unmarshalling.{FromStringUnmarshaller, Unmarshaller}


object Unmarshallers {

  implicit val urlsUnmarshaller: FromStringUnmarshaller[IndexedSeq[URL]] =
    Unmarshaller.strict { string =>
      if (string.isEmpty) {
        throw new IllegalArgumentException("urls value is empty")
      }
      val urlStrings = string.split(",")
      urlStrings.map(new URL(_)).toIndexedSeq
    }

}
