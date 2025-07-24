ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.19"
ThisBuild / autoAPIMappings := true
ThisBuild / scalacOptions += "-deprecation"
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
// TODO: Make this an error again
  "-Wconf:cat=other-match-analysis:warning"
)
ThisBuild / showSuccess := false
Global / excludeLintKeys += showSuccess

// Some test suites (e.g., AetherlingBenchmarkTests) unfortunately do not
// support parallel testing
Test / parallelExecution := false
Test / logBuffered := false

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
libraryDependencies += "org.scala-lang" %% "toolkit" % "0.1.7"
Compile / unmanagedSourceDirectories += baseDirectory.value / "lib/arithexpr/src/main/"

lazy val root = (project in file("."))
  .settings(name := "min_hw_ir")
