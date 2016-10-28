name := "vscode-languageserver"
version := "0.4.13"
organization := "net.flatmap"
scalaVersion := "2.11.8"

bintrayOrganization := Some("flatmap")
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

resolvers += Resolver.bintrayRepo("flatmap", "maven")
libraryDependencies += "net.flatmap" %% "jsonrpc" % "0.4.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"