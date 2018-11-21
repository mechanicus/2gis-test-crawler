package ru._2gis.api.controller

import java.net.URL

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru._2gis.api.CustomExecutionContext
import ru._2gis.api.crawler.async._
import ru._2gis.api.marshalling._
import ru._2gis.api.view._

import scala.concurrent.Await
import scala.concurrent.duration._


final class AsyncRoutes(system: ActorSystem)
  extends RouteGroup
     with CustomExecutionContext
{

  import JsonCodecs._
  import Marshallers._
  import Unmarshallers._

  private val asyncApi = new AsyncApi(system)

  override def routes: Route = {
    pathPrefix("async") {
      path("queries") {
        post {
          formFields('urls.as[IndexedSeq[URL]]) { urls =>
            val response = for {
              id <- asyncApi.executeQuery(urls)
            } yield Responses.ok("query_id", id)
            complete(response)
          }
        }
      } ~
      path("query" / JavaUUID) { id =>
        get {
          Await.result(asyncApi.getQueryResult(id), Duration.Inf) match {
            case Some(status) =>
              complete(Responses.ok(status))
            case None =>
              complete(Responses.notFound(s"there is no execution associated with queryId = $id"))
          }
        }
      }
    }
  }

}
