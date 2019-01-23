package ru._2gis.api

import scala.concurrent.ExecutionContext


/** Миксин с `ExecutionContext` для исполнения действий над `Future` */
trait GlobalExecutionContext {

  protected implicit val executionContext: ExecutionContext = ExecutionContext.global

}
