package ru._2gis.api.crawler.sync

import java.net.URL
import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import ru._2gis.api.CustomExecutionContext
import ru._2gis.api.crawler.{Complete, ExecutionStatus, HttpClient}

import scala.concurrent.Future
import scala.concurrent.duration._


final class SyncApi(system: ActorSystem) extends HttpClient with CustomExecutionContext {

  private val id = UUID.fromString("00000000-0000-0000-0000-000000000000")
  private val config = ConfigFactory.load().getConfig("api.crawler")
  private implicit val timeout: Timeout = Timeout((config.getLong("http-client.loadTimeoutInMillis") + 2).millis)

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
