name := "kafka-offset-monitor-influxdb"
version := "1.0"
scalaVersion := "2.11.11"


libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "18.0",
  "com.quantifind" %% "kafkaoffsetmonitor" % "0.4.1-SNAPSHOT",
  "com.github.davidb" % "metrics-influxdb" % "0.9.3",
  "io.dropwizard.metrics" % "metrics-core" % "3.2.2",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.jayway.awaitility" % "awaitility" % "1.6.1" % "test"
)

resolvers ++= Seq(
  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  "java m2" at "http://download.java.net/maven/2",
  "twitter repo" at "http://maven.twttr.com"
)


assemblyMergeStrategy in assembly := {
  case "about.html" => MergeStrategy.discard
  case x =>
    val oldStrategy = (mergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName == "KafkaOffsetMonitor-assembly-0.4.1-SNAPSHOT.jar"}
}