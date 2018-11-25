package ru._2gis.api.crawler

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config
import okhttp3.OkHttpClient

object HttpClients {

  def fromConfig(config: Config): OkHttpClient = {
    new OkHttpClient.Builder()
      .callTimeout(config.getLong("loadTimeoutInMillis"), TimeUnit.MILLISECONDS)
      .build()
  }

}
