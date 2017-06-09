kafka-offset-monitor-graphite
===========
Plugin to [KafkaOffsetMonitor](https://github.com/quantifind/KafkaOffsetMonitor) tool reporting offset data to Influxdb via [dropwizard metrics](https://github.com/davidB/metrics-influxdb).


Building It
===========
Currently KafkaOffsetMonitor is not available via public artifact repository, so before we build the plugin we need to build KafkaOffsetMonitor and publish it to maven local repo:

```
sbt publishM2
```

Now we can build the plugin:

```
sbt assembly
```

Running It
===========
Check how to run KafkaOffsetMonitor and modify the command by adding a plugin assembly jar file to the classpath, and put graphite configuration properties into a pluginsArgs argument.

See original KafkaOffsetMonitor example command modified with graphite reporter plugin usage:

```
java  -cp KafkaOffsetMonitor-assembly-0.4.1-SNAPSHOT.jar:kafka-offset-monitor-influxdb-assembly-1.0.jar com.quantifind.kafka.offsetapp.OffsetGetterWeb \
	--offsetStorage kafka \
	--kafkaBrokers localhost:9092 \
	--zk localhost \
	--port 8081 \
	--refresh 10.seconds \
	--retain 2.days \
	--dbName offsetapp_kafka \
	--pluginsArgs influxdbHost=localhost,influxdbPort=8086,influxdbUser=kafka,influxdbPassword=kafka,influxdbDatabase=kafka
```

The pluginArgs used by kafka-offset-monitor-graphite are:

- **influxdbHost** InfluxDB host (default localhost)
- **influxdbPort** InfluxDB reporting port (default 8086)
- **influxdbUser** InfluxDB user name.
- **influxdbPassword** InfluxDB password.
- **influxdbDatabase** InfluxDB metrics database.
- **influxdbReportPeriod** Reporting period in seconds (default 30)
- **metricsCacheExpireSeconds** Metrics cache TTL in mires (default 600). Offset metrics are stored in expiring cache and reported to Graphite periodically. If metrics are not updated they will be removed.


## InfluxDb
### Create database and user

```
CREATE DATABASE kafka;
CREATE USER kafka WITH PASSWORD 'kafka';
GRANT ALL ON kafka TO kafka;
```

### Use Docker with InfluxDB and Graphana


```
cd docker-influxdb-grafana
```

Get the stack (only once):

```
git clone https://github.com/nicolargo/docker-influxdb-grafana.git
cd docker-influxdb-grafana
docker pull grafana/grafana
docker pull influxdb
docker pull telegraf
```

Run your stack:

```
docker-compose up -d

```

Show me the logs:

```
docker-compose logs
```

Stop it:

```
docker-compose stop
docker-compose rm
```

Update it:

```
git pull
docker pull grafana/grafana
docker pull influxdb
```


Docker InfluxDB is started in 9086 port. So for create the user and the database:
```
curl -POST http://localhost:9086/query --data-urlencode "q=CREATE DATABASE kafka"
curl -POST http://localhost:9086/query --data-urlencode "q=CREATE USER kafka WITH PASSWORD 'kafka'"
curl -POST http://localhost:9086/query --data-urlencode "q=GRANT ALL ON kafka TO kafka"
```

And Graphana is started on port 9000:


http://localhost:9000