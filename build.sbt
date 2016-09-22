name := "vscode-languageserver-scala"
version := "0.1"
organization := "net.flatmap"
scalaVersion := "2.11.8"
val circeVersion = "0.5.1"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.4.10"
