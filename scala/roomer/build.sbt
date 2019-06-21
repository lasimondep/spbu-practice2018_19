name                := "Roomer"

version             := "0.0.1"

scalaVersion        := "2.13.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.1.1"

test in assembly := {}

assemblyJarName in assembly := "Roomer.jar"
