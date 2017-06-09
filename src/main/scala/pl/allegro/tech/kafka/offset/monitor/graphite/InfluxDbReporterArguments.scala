package pl.allegro.tech.kafka.offset.monitor.graphite

import java.lang.Integer.parseInt

object InfluxDbReporterArguments {

  def parseArguments(args: String): InfluxDbMetricWriterProperties = {

    var influxdbHost: String = "localhost"
    var influxdbPort: Int = 8086
    var influxdbUser: String = ""
    var influxdbPassword: String = ""
    var influxdbDatabase: String = ""
    var influxdbReportPeriod: Int = 30
    var metricsCacheExpireSeconds: Int = 600


    val argsMap: Map[String, String] = args.split(",").map(_.split("=", 2)).filter(_.length > 1).map(arg => {
      arg(0) -> arg(1)
    }).toMap
    argsMap.get("influxdbHost").foreach(influxdbHost = _)
    argsMap.get("influxdbPort").foreach(str => {
      influxdbPort = parseInt(str)
    })
    argsMap.get("influxdbReportPeriod").foreach(str => {
      influxdbReportPeriod = parseInt(str)
    })
    argsMap.get("metricsCacheExpireSeconds").foreach(str => {
      metricsCacheExpireSeconds = parseInt(str)
    })

    argsMap.get("influxdbUser").foreach(influxdbUser = _)

    argsMap.get("influxdbPassword").foreach(influxdbPassword = _)

    argsMap.get("influxdbDatabase").foreach(influxdbDatabase = _)


    InfluxDbMetricWriterProperties(influxdbHost, influxdbPort, influxdbUser, influxdbPassword, influxdbDatabase, influxdbReportPeriod, metricsCacheExpireSeconds)
  }
}
