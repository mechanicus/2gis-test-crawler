package ru._2gis.api.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ru._2gis.api.configuration.Configuration


final class CrawlerApi(config: Configuration)
  extends Runnable
     with RouteGroup
     with CustomRejectionHandler
{

  private implicit val system: ActorSystem = ActorSystem()

  private val syncRoutes = new SyncRoutes(system)
  private val asyncRoutes = new AsyncRoutes(system)

  override def run(): Unit = {
    implicit val am: ActorMaterializer = ActorMaterializer()
    Http().bindAndHandle(routes, config.host, config.port)
  }

  override def routes: Route = pathPrefix("crawler") {
    syncRoutes.routes ~ asyncRoutes.routes
  }

}
