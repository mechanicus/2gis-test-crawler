package ru._2gis.api.crawler.async

import java.net.URL
import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.routing._
import akka.util.Timeout
import ru._2gis.api.GlobalExecutionContext
import ru._2gis.api.crawler.{CompanyInfoLoader, CompanyInfoLoadingResult, HttpClient, LoadCompanyInfo}

import scala.concurrent.duration._


private[async]
final case class Execute(urls: IndexedSeq[URL])

private[async]
final case class GetResult(id: UUID)

/** Актор, занимающийся исполнением асинхронных запросов */
private[async]
final class AsyncExecutor extends Actor with HttpClient with GlobalExecutionContext {

  // кэш исполняющихся и исполненных запросов
  private val executions = context.actorOf(Props[Executions], "executions")
  // пул акторов, занимающихся загрузкой информации о компаниях
  private val companyInfoLoaders = buildLoaders()

  private implicit val timeout: Timeout = Timeout(1.second)

  override def receive: Receive = {
    // 1. когда поступает запрос на загрузку информации по урлам,
    // отправляем эти урлы в пул `воркеров` которые загрузят
    // нужную информацию каждый в своем потоке
    // 2. также информация о исполняющемся запросе отправляется в
    // кэш запросов
    // 3. клиенту возвращается id нового запроса
    case Execute(urls) =>
      val id = UUID.randomUUID()
      urls foreach { url => companyInfoLoaders.tell(LoadCompanyInfo(id, url), self) }
      executions ! NewExecution(id, urls.length)
      sender() ! id

    // когда от одного из `воркеров` поступает сообщение с загруженной
    // информацией, обновляем кэш запросов
    case res@CompanyInfoLoadingResult(_, _, _) =>
      executions ! res

    // когда от клиента поступает запрос на получение информации о
    // исполняющемся запросе, перенаправляем его в кэш запросов
    case GetResult(id) =>
      executions ? GetExecutionStatus(id) pipeTo sender()

  }

  private def buildLoaders(): ActorRef = {
    context.actorOf(FromConfig.props(Props(classOf[CompanyInfoLoader], client)), "loaders")
  }

}
