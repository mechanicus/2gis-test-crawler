package ru._2gis.api.controller

import java.net.URL

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.config.Config
import okhttp3.OkHttpClient
import ru._2gis.api.GlobalExecutionContext
import ru._2gis.api.crawler.async._
import ru._2gis.api.marshalling._
import ru._2gis.api.view._


/**
  * Модуль контроллера, обрабатывающий асинхронные запросы к API
  */
final class AsyncRoutes(implicit system: ActorSystem, client: OkHttpClient, config: Config)
  extends RouteGroup
     with GlobalExecutionContext
{

  import JsonCodecs._
  import Marshallers._
  import Unmarshallers._

  private val asyncApi = new AsyncApi

  override def routes: Route = {
    pathPrefix("async") {
      path("query") { post { formFields('urls.as[IndexedSeq[URL]]) { urls =>
        val response = for {
          id <- asyncApi.executeQuery(urls)
        } yield Responses.ok("query_id", id)
        complete(response)
      }}} ~
      path("query" / JavaUUID) { id => get {
        onSuccess(asyncApi.getQueryResult(id)) {
          case Some(status) =>
            complete(Responses.ok(status))
          case None =>
            complete(Responses.notFound(s"there is no execution associated with queryId = $id"))
        }
      }}
    }
  }

}
