package pl.allegro.tech.kafka.offset.monitor.graphite

case class InfluxDbMetricWriterProperties(host: String, port: Int, username: String, password: String, database: String, influxdbReportPeriod : Int, metricsCacheExpireSeconds: Int )