name := "vscode-languageserver-scala"
version := "0.1"
organization := "net.flatmap"
scalaVersion := "2.11.8"

bintrayOrganization := Some("flatmap")
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

resolvers += Resolver.bintrayRepo("net/flatmap", "maven")
libraryDependencies += "net.flatmap" %% "scala-json-rpc" % "0.1.5"