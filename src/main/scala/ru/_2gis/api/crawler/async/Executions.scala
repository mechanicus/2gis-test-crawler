package ru._2gis.api.crawler.async

import java.util.UUID

import akka.actor.Actor
import com.typesafe.config.Config
import ru._2gis.api.crawler._

import scala.concurrent.duration._


private[async]
final case class NewExecution(
  id: UUID,
  urlsCount: Int
)

private[async]
final case class GetExecutionStatus(id: UUID)

private[async]
final case class MaybeExecutionStatus(status: Option[ExecutionStatus])


/** LRU-кэш исполняющихся и исполненных запросов */
private[async]
final class Executions(config: Config) extends Actor {

  private val conf = config.getConfig("api.crawler.async.executions")
  private val maxCapacity: Int = conf.getInt("cache.max-capacity")
  private val executionLifetime: Duration = Duration(conf.getString("cache.lifetime"))
  private val ticks = context.system.scheduler.schedule(1.second, 1.second, self, Tick)(context.dispatcher)

  override def receive: Receive = work(Map.empty)

  override def postStop(): Unit = {
    ticks.cancel()
  }

  private def work(cache: Map[UUID, CacheEntry]): Receive = {
    // когда приходит команда создать запись в кэше под новый запрос,
    // создаем такую запись и добавляем ее в кэш, попутно удалив самую
    // старую запись в кэше, если он переполнен
    case NewExecution(id, urlsCount) =>
      val newCacheEntry = CacheEntry(System.currentTimeMillis(), Incomplete(urlsCount, IndexedSeq.empty))
      if (cache.size >= maxCapacity) {
        val oldestExecutionId = findOldestExecutionId(cache)
        val cleanedCache = cache - oldestExecutionId
        context.become(work(cleanedCache + (id -> newCacheEntry)))
      } else {
        context.become(work(cache + (id -> newCacheEntry)))
      }

    // когда приходит загруженная информация от `воркера`, добавляем ее
    // в соответствующую запись кэша
    case CompanyInfoLoadingResult(id, url, result) =>
      if (cache.contains(id)) {
        val entry = cache(id)
        val newExecutionStatus = entry.executionStatus match {
          case Incomplete(remainingTasks, results) =>
            if (remainingTasks == 1) {
              Complete(results :+ Record(url, result))
            } else {
              Incomplete(remainingTasks - 1, results :+ Record(url, result))
            }
          case complete => complete
        }
        val newEntry = entry.copy(executionStatus = newExecutionStatus)
        val updatedCache = cache + (id -> newEntry)
        context.become(work(updatedCache))
      }

    // когда пришел запрос на получение информации о исполняющемся или исполненном
    // запросе, просто берем его из кэша
    case GetExecutionStatus(id) =>
      sender() ! MaybeExecutionStatus(cache.get(id).map(_.executionStatus))

    // периодически чистим кэш от старых записей
    case Tick =>
      val currentTimestamp = System.currentTimeMillis()
      val cleanedCache = (Map.empty[UUID, CacheEntry] /: cache) { case (nc, (id, entry)) =>
        if (currentTimestamp - entry.creationTimestamp > executionLifetime.toMillis) {
          nc
        } else {
          nc + (id -> entry)
        }
      }
      context.become(work(cleanedCache))
  }

  private def findOldestExecutionId(cache: Map[UUID, CacheEntry]): UUID = {
    val (result, _) = (cache.head /: cache) { case ((oldestId, oldestEntry), (id, entry)) =>
      if (entry.creationTimestamp < oldestEntry.creationTimestamp) {
        (id, entry)
      } else {
        (oldestId, oldestEntry)
      }
    }
    result
  }

  private case object Tick

  private case class CacheEntry (
    creationTimestamp: Long,
    executionStatus: ExecutionStatus
  )

}
