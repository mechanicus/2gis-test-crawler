package ru._2gis.api.crawler

import java.net.URL
import java.util.UUID

import akka.actor.Actor
import okhttp3.{OkHttpClient, Request, Response, ResponseBody}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru._2gis.api.{ErrorMessage, Title}

import scala.util.{Failure, Success, Try}


private[crawler]
final case class LoadCompanyInfo (
  taskId: UUID,
  url: URL
)
private[crawler]
final case class CompanyInfoLoadingResult(
  taskId: UUID,
  url: URL,
  result: Either[ErrorMessage, CompanyInfo]
)

/**  Актор, занимающийся загрузкой информации о компании (worker) */
private[crawler]
final class CompanyInfoLoader(client: OkHttpClient) extends Actor {

  override def receive: Receive = {
    case LoadCompanyInfo(id, url) =>
      sender() ! CompanyInfoLoadingResult(id, url, loadInfo(url))
  }

  private def loadInfo(url: URL): Either[ErrorMessage, CompanyInfo] = for {
    document <- loadPage(url)
    title <- extractTitle(document)
  } yield CompanyInfo(title)

  private def loadPage(url: URL): Either[ErrorMessage, Document] = {

    def buildRequest(url: URL): Request = new Request.Builder()
      .get()
      .url(url)
      .build()

    def filterBadStatuses(response: Response): Try[Response] =
      if (response.code >= 200 && response.code < 400) {
        Success(response)
      } else {
        Failure(new Exception(s"Bad response code: ${response.code}"))
      }

    def getBody(response: Response): Try[ResponseBody] = if (response.body == null) {
      Failure(new NullPointerException("response body is null"))
    } else {
      Success(response.body)
    }

    val request = buildRequest(url)
    val content = for {
      response <- Try(client.newCall(request).execute())
      goodResponse <- filterBadStatuses(response)
      body <- getBody(goodResponse)
      content <- Try(body.string())
    } yield content
    content match {
      case Success(string) => Right(Jsoup.parse(string))
      case Failure(ex) => Left(ex.getMessage)
    }
  }

  private def extractTitle(document: Document): Either[ErrorMessage, Title] = {
    val titleElements = document.select("title")
    if (titleElements.isEmpty) {
      Left("title tag is missing")
    } else {
      Right(titleElements.text)
    }
  }

}
