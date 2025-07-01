ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.19"
// There seems to be some kind of race condition in the code which makes it so that the tests fail catastrophically
// when run in parallel.
// Maybe it's somehow related to the ArithExpr library, since the issue appeared only when I started using the library.
ThisBuild / parallelExecution := false
ThisBuild / logBuffered := false
ThisBuild / autoAPIMappings := true
ThisBuild / scalacOptions += "-deprecation"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
libraryDependencies += "org.scala-lang" %% "toolkit" % "0.1.7"
Compile / unmanagedSourceDirectories += baseDirectory.value / "lib/arithexpr/src/main/"

lazy val root = (project in file("."))
  .settings(name := "min_hw_ir")
