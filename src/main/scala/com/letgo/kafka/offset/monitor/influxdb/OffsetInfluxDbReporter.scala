package com.letgo.kafka.offset.monitor.influxdb

import java.util.concurrent.TimeUnit

import com.codahale.metrics.{Gauge, MetricFilter, MetricRegistry}
import com.google.common.cache._
import com.quantifind.kafka.OffsetGetter.OffsetInfo
import metrics_influxdb.api.measurements.MetricMeasurementTransformer
import metrics_influxdb.{HttpInfluxdbProtocol, InfluxdbReporter};

class OffsetInfluxDbReporter(pluginsArgs: String) extends com.quantifind.kafka.offsetapp.OffsetInfoReporter {

  val influxDbMetricWriterProperties = InfluxDbReporterArguments.parseArguments(pluginsArgs)

  val metrics: MetricRegistry = new MetricRegistry()

  val protocol = new HttpInfluxdbProtocol("http", influxDbMetricWriterProperties.host,
    influxDbMetricWriterProperties.port, influxDbMetricWriterProperties.username,
    influxDbMetricWriterProperties.password, influxDbMetricWriterProperties.database)

  val influxdbReporter = InfluxdbReporter.forRegistry(metrics)
    .protocol(protocol)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .filter(MetricFilter.ALL)
    .skipIdleMetrics(false)
    .transformer(MetricMeasurementTransformer.NOOP).build();


  influxdbReporter.start(influxDbMetricWriterProperties.influxdbReportPeriod, TimeUnit.SECONDS)

  val removalListener: RemovalListener[String, GaugesValues] = new RemovalListener[String, GaugesValues] {
    override def onRemoval(removalNotification: RemovalNotification[String, GaugesValues]) = {
      metrics.remove(removalNotification.getKey() + ".offset")
      metrics.remove(removalNotification.getKey() + ".logSize")
      metrics.remove(removalNotification.getKey() + ".lag")
    }
  }

  val cacheLoader = new CacheLoader[String, GaugesValues]() {
    def load(key: String): GaugesValues = {
      val values: GaugesValues = new GaugesValues()

      val offsetGauge: Gauge[Long] = new Gauge[Long] {
        override def getValue: Long = {
          values.offset
        }
      }

      val lagGauge: Gauge[Long] = new Gauge[Long] {
        override def getValue: Long = {
          values.lag
        }
      }

      val logSizeGauge: Gauge[Long] = new Gauge[Long] {
        override def getValue: Long = {
          values.logSize
        }
      }

      metrics.register(key + ".offset", offsetGauge)
      metrics.register(key + ".logSize", logSizeGauge)
      metrics.register(key + ".lag", lagGauge)

      values
    }
  }


  val gauges: LoadingCache[String, GaugesValues] = CacheBuilder.newBuilder()
    .expireAfterAccess(influxDbMetricWriterProperties.metricsCacheExpireSeconds, TimeUnit.SECONDS).build(cacheLoader)


  def report(info: scala.IndexedSeq[OffsetInfo]) = {
    info.foreach(i => {
      val values: GaugesValues = gauges.get(getMetricName(i))
      values.logSize = i.logSize
      values.offset = i.offset
      values.lag = i.lag
    })
  }

  def getMetricName(offsetInfo: OffsetInfo): String = {
    offsetInfo.topic.replace(".", "_") + "." + offsetInfo.group.replace(".", "_") + "." + offsetInfo.partition
  }
}
