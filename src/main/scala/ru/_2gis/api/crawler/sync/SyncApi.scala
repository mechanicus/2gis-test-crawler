package ru._2gis.api.crawler.sync

import java.net.URL
import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.Config
import okhttp3.OkHttpClient
import ru._2gis.api.crawler.{Complete, ExecutionStatus}

import scala.concurrent.Future
import scala.concurrent.duration._


/**
  * Класс-обертка над акторной системой, предоставляющий scala-api для
  * исполнения синхронных запросов
  */
final class SyncApi(implicit system: ActorSystem, client: OkHttpClient, config: Config) {

  private val id = UUID.fromString("00000000-0000-0000-0000-000000000000")
  private implicit val timeout: Timeout =
    Timeout((config.getLong("api.crawler.http-client.loadTimeoutInMillis") + 2).millis)

  def executeQuery(urls: IndexedSeq[URL]): Future[ExecutionStatus] = {
    val executor = system.actorOf(Props(
      classOf[SyncExecutor],
      id,
      client,
      timeout
    ).withDispatcher("akka.actor.sync-executions-dispatcher"))
    (executor ? Execute(urls)).mapTo[Complete]
  }

}
