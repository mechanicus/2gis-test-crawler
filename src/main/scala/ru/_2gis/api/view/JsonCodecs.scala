package ru._2gis.api.view

import java.net.URL

import argonaut._, Argonaut._
import ru._2gis.api.crawler.CompanyInfo
import ru._2gis.api.crawler.{Complete, ExecutionStatus, Incomplete, Record}


object JsonCodecs extends EncodeJsons {

  implicit def encodeIndexedSeq[A: EncodeJson]: EncodeJson[IndexedSeq[A]] = EncodeJson {
    indexedSeq => indexedSeq.toVector.asJson
  }

  implicit def encodeJsonResponse[A: EncodeJson]: EncodeJson[JsonResponse[A]] = EncodeJson {
    case Success(code, status, result) =>
      ("code" := code) ->:
      ("status" := status) ->:
      ("result" := result) ->:
      jEmptyObject
    case Error(code, status, message) =>
      ("code" := code) ->:
      ("status" := status) ->:
      ("message" := message) ->:
      jEmptyObject
  }

  implicit val encodeUrlKey: EncodeJsonKey[URL] = EncodeJsonKey.from(_.toString)

  implicit val encodeUrl: EncodeJson[URL] = EncodeJson {
    url => url.toString.asJson
  }

  implicit val codecCompanyInfo: CodecJson[CompanyInfo] =
    casecodec1(CompanyInfo.apply, CompanyInfo.unapply)("title")

  implicit val encodeRecord: EncodeJson[Record] = EncodeJson {
    case Record(url, Left(message)) =>
      ("url" := url) ->:
      ("error" := message) ->:
      jEmptyObject
    case Record(url, Right(companyInfo)) =>
      ("url" := url) ->:
      ("info" := companyInfo) ->:
      jEmptyObject
  }

  implicit val encodeExecutionStatus: EncodeJson[ExecutionStatus] = EncodeJson {
    case Incomplete(remainingTasks, results) =>
      ("status" := "incomplete") ->:
      ("remainingTasks" := remainingTasks) ->:
      ("results" := results) ->:
      jEmptyObject
    case Complete(results) =>
      ("status" := "complete") ->:
      ("results" := results) ->:
      jEmptyObject
  }

}
