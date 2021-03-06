package ru._2gis.api.crawler.sync

import java.net.URL
import java.util.UUID

import akka.actor.{Actor, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import okhttp3.OkHttpClient
import ru._2gis.api.GlobalExecutionContext
import ru._2gis.api.crawler._

import scala.concurrent.Future


private[sync]
final case class Execute(urls: IndexedSeq[URL])


/** Актор, занимающийся исполнением синхронных запросов */
final class SyncExecutor (
  defaultId: UUID,
  client: OkHttpClient,
  private implicit val timeout: Timeout
) extends Actor
  with GlobalExecutionContext
{

  override def receive: Receive = {
    // когда приходит запрос на загрузку информации по урлам,
    // создаем по `воркеру` на каждый урл, отправляем им каждому
    // по одному урлу и ждем ответных сообщений с загруженной
    // информацией. Когда все урлы обработаны, собираем результат
    // и возвращаем клиенту
    case Execute(urls) =>
      val futures = urls map { url =>
        val loader = context.actorOf(Props(
          classOf[CompanyInfoLoader],
          client
        ).withDispatcher("akka.actor.sync-executions-dispatcher"))
        (loader ? LoadCompanyInfo(defaultId, url))
          .mapTo[CompanyInfoLoadingResult]
          .map(loadingResult => Record(url, loadingResult.result))
      }
      val execution = Future.sequence(futures).map(records => Complete(records))
      execution pipeTo sender()
  }

}
