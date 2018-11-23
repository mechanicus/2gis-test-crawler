package ru._2gis.api

import ru._2gis.api.configuration.Configuration
import ru._2gis.api.controller.CrawlerApi


object Main {

  def main(args: Array[String]): Unit = {
    Configuration(args) foreach { config =>
      new CrawlerApi(config).run()
    }
  }

}
