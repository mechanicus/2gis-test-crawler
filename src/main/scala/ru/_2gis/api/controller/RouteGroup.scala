package ru._2gis.api.controller

import akka.http.scaladsl.server.Route

/** Интерфейс для модулей контроллера */
trait RouteGroup {
  def routes: Route
}
