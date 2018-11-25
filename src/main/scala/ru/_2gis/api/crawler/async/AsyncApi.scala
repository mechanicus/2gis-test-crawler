package ru._2gis.api.crawler.async

import java.net.URL
import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import ru._2gis.api.GlobalExecutionContext
import ru._2gis.api.crawler.ExecutionStatus

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Класс-обертка над акторной системой, предоставляющий scala-api для
  * исполнения асинхронных запросов
  */
final class AsyncApi(system: ActorSystem) extends GlobalExecutionContext {

  private val asyncExecutor = system.actorOf(Props[AsyncExecutor], "async-executor")
  private implicit val askTimeout: Timeout = Timeout(1.second)

  def executeQuery(urls: IndexedSeq[URL]): Future[UUID] = {
    (asyncExecutor ? Execute(urls)).mapTo[UUID]
  }

  def getQueryResult(id: UUID): Future[Option[ExecutionStatus]] = {
    (asyncExecutor ? GetResult(id)).mapTo[MaybeExecutionStatus].map(_.status)
  }

}
