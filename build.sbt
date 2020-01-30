name := "play-datetime-routes"
organization := "com.github.dgilling"
version := "1.0.3"

publishMavenStyle := true
isSnapshot := true

scalaVersion := "2.11.8"

val playVersion = "2.6.23"
val playJsonVersion = "2.6.13"

libraryDependencies += "joda-time" % "joda-time" % "2.10.3"
libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % playJsonVersion,
  "com.typesafe.play" %% "play" % playVersion)