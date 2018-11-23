package ru._2gis.api.crawler.async

import java.net.URL
import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.routing._
import akka.util.Timeout
import ru._2gis.api.CustomExecutionContext
import ru._2gis.api.crawler.{CompanyInfoLoader, CompanyInfoLoadingResult, HttpClient, LoadCompanyInfo}

import scala.concurrent.duration._


private[async]
final case class Execute(urls: IndexedSeq[URL])

private[async]
final case class GetResult(id: UUID)


private[async]
final class AsyncExecutor extends Actor with HttpClient with CustomExecutionContext {

  private val executions = context.actorOf(Props[Executions], "executions")
  private val companyInfoLoaders = buildLoaders()

  private implicit val timeout: Timeout = Timeout(1.second)

  override def receive: Receive = {
    case Execute(urls) =>
      val id = UUID.randomUUID()
      urls foreach { url => companyInfoLoaders.tell(LoadCompanyInfo(id, url), self) }
      executions ! NewExecution(id, urls.length)
      sender() ! id
    case res@CompanyInfoLoadingResult(_, _, _) =>
      executions ! res
    case GetResult(id) =>
      executions ? GetExecutionStatus(id) pipeTo sender()
  }

  private def buildLoaders(): ActorRef = {
    context.actorOf(FromConfig.props(Props(classOf[CompanyInfoLoader], client)), "loaders")
  }

}
