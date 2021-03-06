package ru._2gis.api.configuration

import scopt.OptionParser


final case class CLIConfig(
  host: String,
  port: Int
)

object CLIConfig {

  def apply(args: Array[String]): Option[CLIConfig] = new OptionParser[CLIConfig]("crawler-api.jar") {

    opt[String]('h', "host") required() action {
      case (hostString, config) => config.copy(host = hostString)
    } valueName "<string>" text "host name or ip address to which the http endpoint will be bound"

    opt[Int]('p', "port") required() action {
      case (portValue, config) => config.copy(port = portValue)
    } validate { portValue =>
      if (portValue > 0 && portValue <= 0xffff) {
        success
      } else {
        failure(s"port value ($portValue) is out of range [1,65535]")
      }
    } valueName "<int>" text "port number to which the http endpoint will be bound"

  }.parse(args, CLIConfig("localhost", 8080))

}
