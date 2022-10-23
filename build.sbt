name := "play-datetime-routes"
organization := "com.github.dgilling"
version := "1.1.0"

publishMavenStyle := true
isSnapshot := true

scalaVersion := "2.11.8"

val playVersion = "2.6.25"
val playJsonVersion = "2.6.14"

libraryDependencies += "joda-time" % "joda-time" % "2.12.0"
libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % playJsonVersion,
  "com.typesafe.play" %% "play" % playVersion)