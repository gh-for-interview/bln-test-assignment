import sbtassembly.MergeStrategy

name := "TestAssignmentBLN"

version := "0.1"

scalaVersion := "2.13.6"

val akkaHttpVersion = "10.1.13"
val scalikejdbcVersion = "3.5.0"

coverageEnabled.in(ThisBuild, Test, test) := true

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-testkit" % "2.5.31" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "org.apache.kafka" % "kafka-clients" % "2.4.1",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.5",
  "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
  "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion,
  "org.postgresql" % "postgresql" % "42.2.20",
  "org.scalatest" %% "scalatest" % "3.1.2" % Test
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("version.conf") => MergeStrategy.concat
  case _ => MergeStrategy.first
}