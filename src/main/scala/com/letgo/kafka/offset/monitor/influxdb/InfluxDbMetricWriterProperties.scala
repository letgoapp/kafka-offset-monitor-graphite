package com.letgo.kafka.offset.monitor.influxdb

case class InfluxDbMetricWriterProperties(host: String, port: Int, username: String, password: String, database: String, influxdbReportPeriod : Int, metricsCacheExpireSeconds: Int )