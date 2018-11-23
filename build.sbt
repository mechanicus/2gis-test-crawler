name := "crawler-api"

version := "0.1"

scalaVersion := "2.12.7"

val akkaVersion = "2.5.18"
val akkaHttpVersion = "10.1.5"

libraryDependencies ++= Seq (
  "com.github.scopt" %% "scopt" % "3.7.0",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.squareup.okhttp3" % "okhttp" % "3.12.0",
  "io.argonaut" %% "argonaut" % "6.2.2",
  "org.jsoup" % "jsoup" % "1.11.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

javacOptions ++= Seq (
  "target", "1.8"
)

scalacOptions ++= Seq (
  "-encoding", "utf-8",
  "-target:jvm-1.8",
  "-feature",
  "-unchecked",
  "-explaintypes",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Xlint:_"
)
