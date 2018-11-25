package ru._2gis.api

import ru._2gis.api.configuration.{CLIConfig, FileConfig}
import ru._2gis.api.controller.CrawlerApi


object Main {

  def main(args: Array[String]): Unit = {
    CLIConfig(args) foreach { cliConfig =>
      new CrawlerApi(cliConfig, FileConfig.config).run()
    }
  }

}
