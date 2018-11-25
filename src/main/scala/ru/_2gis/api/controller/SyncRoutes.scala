package ru._2gis.api.controller
import java.net.URL

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.config.Config
import okhttp3.OkHttpClient
import ru._2gis.api.GlobalExecutionContext
import ru._2gis.api.crawler.sync.SyncApi
import ru._2gis.api.marshalling._
import ru._2gis.api.view._


/**
  * Модуль контроллера, обрабатывающий синхронные запросы к API
  */
final class SyncRoutes(implicit system: ActorSystem, client: OkHttpClient, config: Config)
  extends RouteGroup
     with GlobalExecutionContext
{

  import JsonCodecs._
  import Marshallers._
  import Unmarshallers._

  private val syncApi = new SyncApi

  override def routes: Route = {
    pathPrefix("sync") {
      path("query") { post { formFields('urls.as[IndexedSeq[URL]]) { urls =>
        val response = for {
          id <- syncApi.executeQuery(urls)
        } yield Responses.ok(id)
        complete(response)
      }}}
    }
  }

}
