package ru._2gis.api.configuration

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}


/** Конфигурация API на основе файла */
object FileConfig {

  // настройки API можно указать в файле "api.conf" и положить
  // его в папку с jar-файлом. Если такого файла нет, или в нем
  // указаны не все настройки, то недостающие настройки будут взяты
  // из файла "resources/api.conf", зашитого в jar-файл
  implicit lazy val config: Config =
    ConfigFactory.parseFile(new File("api.conf"))
      .withFallback(ConfigFactory.load())


  // вспомогательный класс для красивой печати конфига в лог
  implicit final class PrettyConfig(val config: Config) extends AnyVal {

    def pretty: String =
      flat(config.root.unwrapped)
        .map({case (key, value) => s"$key = $value"})
        .mkString("\n")

    private def flat(map: java.util.Map[String, AnyRef], prefix: String = ""): IndexedSeq[(String, AnyRef)] = {
      import scala.collection.JavaConverters._
      map.asScala.toIndexedSeq flatMap { case (key, value) =>
        val k = if (prefix.isEmpty) key else s"$prefix.$key"
        value match {
          case m: java.util.Map[_, _] => flat(m.asInstanceOf[java.util.Map[String, AnyRef]], k)
          case v => IndexedSeq((k, v))
        }
      }
    }
  }

}
