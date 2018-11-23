package ru._2gis.api

import java.util.concurrent.ForkJoinPool

import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext


trait CustomExecutionContext {

  implicit val executionContext: ExecutionContext = CustomExecutionContext.ec

}

object CustomExecutionContext {

  private lazy val config = ConfigFactory.load().getConfig("api.execution-context")

  private lazy val ec: ExecutionContext =
    ExecutionContext.fromExecutor(new ForkJoinPool(config.getInt("parallelism")))

}
