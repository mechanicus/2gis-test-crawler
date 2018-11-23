package ru._2gis.api.crawler

import java.net.URL


final case class CompanyInfo(title: String)

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
