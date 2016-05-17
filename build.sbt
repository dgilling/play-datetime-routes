name := "play-datetime-routes"
organization := "com.github.dgilling"
version := "1.0-SNAPSHOT"

publishMavenStyle := true
isSnapshot := true

scalaVersion := "2.11.8"

val playVersion = "2.5.3"

libraryDependencies += "joda-time" % "joda-time" % "2.9.3"
libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % playVersion,
  "com.typesafe.play" %% "play" % playVersion)