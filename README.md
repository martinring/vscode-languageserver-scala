# VSCode Language Server - Scala

Implementation of the VSCode [language server protocol](https://github.com/Microsoft/language-server-protocol) in Scala. Based on [scala-json-rpc](https://github.com/flatmap/scala-json-rpc).

## Usage

Add the following to your `build.sbt`:

```scala
resolvers += Resolver.bintrayRepo("flatmap", "maven")
libraryDependencies += "net.flatmap" %% "vscode-languageserver" % "0.4.12"
```

## Example Implementation

For an example implementation, see [vscode-languageserver-scala-example](https://github.com/martinring/vscode-languageserver-scala-example)

## License

[MIT](https://opensource.org/licenses/MIT)