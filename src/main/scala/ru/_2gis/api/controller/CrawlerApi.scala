package ru._2gis.api.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import ru._2gis.api.configuration.CLIConfig


final class CrawlerApi(
  cliConfig: CLIConfig,
  fileConfig: Config
) extends Runnable
     with RouteGroup
     with CustomRejectionHandler
{

  private val logger = LoggerFactory.getLogger(getClass)
  private implicit val system: ActorSystem = ActorSystem()

  private val syncRoutes = new SyncRoutes(system)
  private val asyncRoutes = new AsyncRoutes(system)

  override def run(): Unit = {
    logConfiguration()
    implicit val am: ActorMaterializer = ActorMaterializer()
    Http().bindAndHandle(routes, cliConfig.host, cliConfig.port)
  }

  override def routes: Route = pathPrefix("crawler") {
    syncRoutes.routes ~ asyncRoutes.routes
  }

  private def logConfiguration(): Unit = {
    val cli = s"host = ${cliConfig.host}\nport = ${cliConfig.port}"
    import ru._2gis.api.configuration.FileConfig.PrettyConfig
    val configurationString = s"""API configuration:
      |CLI:
      |$cli
      |FILE:
      |${fileConfig.getConfig("api").pretty}
    """.stripMargin('|')
    logger.info(configurationString)
  }

}
