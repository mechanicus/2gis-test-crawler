package ru._2gis.api.view

import java.net.HttpURLConnection._


sealed abstract class JsonResponse[+A] {
  def code: Int
  def status: String
}

private[view]
final case class Success[+A] (
  override val code: Int,
  override val status: String,
  result: A
) extends JsonResponse[A]

private[view]
final case class Error (
  override val code: Int,
  override val status: String,
  message: String
) extends JsonResponse[String]


object Responses {

  def ok[A](value: A): JsonResponse[A] = success(HTTP_OK, "OK", value)

  def ok[A](name: String, value: A): JsonResponse[Map[String, A]] = ok(Map(name -> value))

  def notFound(message: String): JsonResponse[String] = error(HTTP_NOT_FOUND, "NOT_FOUND", message)

  def success[A](code: Int, status: String, result: A): JsonResponse[A] = Success(code, status, result)

  def error(code: Int, status: String, message: String): JsonResponse[String] = Error(code, status, message)

}
