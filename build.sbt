name := "vscode-languageserver"
version := "0.4.15"
organization := "net.flatmap"
scalaVersion := "2.11.8"
scalaVersion in ThisBuild := "2.11.8"

bintrayOrganization := Some("flatmap")
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

resolvers += Resolver.bintrayRepo("flatmap", "maven")
libraryDependencies += "net.flatmap" %% "jsonrpc" % "0.4.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"