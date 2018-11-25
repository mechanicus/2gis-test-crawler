package ru._2gis.api.crawler

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import ru._2gis.api.configuration.FileConfig

/** Миксин с HTTP-клиентом, сконфигурированным из конфига API */
trait HttpClient {
  protected val client: OkHttpClient = HttpClient.client
}

object HttpClient {

  private lazy val config = FileConfig.config.getConfig("api.crawler.http-client")
  private lazy val client = buildClient()

  private def buildClient(): OkHttpClient = {
    new OkHttpClient.Builder()
      .callTimeout(config.getLong("loadTimeoutInMillis"), TimeUnit.MILLISECONDS)
      .build()
  }

}
