package ru._2gis.api.crawler

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory
import okhttp3.OkHttpClient

trait HttpClient {
  protected val client: OkHttpClient = HttpClient.client
}

object HttpClient {

  private lazy val config = ConfigFactory.load().getConfig("api.crawler.http-client")
  private lazy val client = buildClient()

  private def buildClient(): OkHttpClient = {
    new OkHttpClient.Builder()
      .callTimeout(config.getLong("loadTimeoutInMillis"), TimeUnit.MILLISECONDS)
      .build()
  }

}
