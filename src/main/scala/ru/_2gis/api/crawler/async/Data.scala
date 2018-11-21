package ru._2gis.api.crawler.async

import java.net.URL

import ru._2gis.api.crawler.CompanyInfo


final case class Record (
  url: URL,
  result: Either[String, CompanyInfo]
)

sealed abstract class ExecutionStatus

final case class Incomplete (
  remainingTasks: Int,
  results: IndexedSeq[Record]
) extends ExecutionStatus

final case class Complete (
  results: IndexedSeq[Record]
) extends ExecutionStatus
