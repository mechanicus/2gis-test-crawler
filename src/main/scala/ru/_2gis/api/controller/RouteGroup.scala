package ru._2gis.api.controller

import akka.http.scaladsl.server.Route


trait RouteGroup {
  def routes: Route
}
