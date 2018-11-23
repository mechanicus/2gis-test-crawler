package ru._2gis.api.crawler

import java.net.URL

import ru._2gis.api.{ErrorMessage, Title}


final case class CompanyInfo(title: Title)

final case class Record (
  url: URL,
  result: Either[ErrorMessage, CompanyInfo]
)

sealed abstract class ExecutionStatus

final case class Incomplete (
  remainingTasks: Int,
  results: IndexedSeq[Record]
) extends ExecutionStatus

final case class Complete (
  results: IndexedSeq[Record]
) extends ExecutionStatus
