ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.19"
ThisBuild / autoAPIMappings := true
ThisBuild / scalacOptions += "-deprecation"
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-Wconf:cat=other-match-analysis:error"
)
ThisBuild / showSuccess := false
Global / excludeLintKeys += showSuccess

// Some test suites (e.g., AetherlingBenchmarkTests) unfortunately do not
// support parallel testing
Test / parallelExecution := false
Test / logBuffered := false

// Testing
libraryDependencies ++= Seq(
  // Testing
  "org.scalatest" %% "scalatest" % "3.2.19" % "test",
  // Files, processes, etc.
  "org.scala-lang" %% "toolkit" % "0.1.7",
  // Logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.4.14"
)
Compile / unmanagedSourceDirectories += baseDirectory.value / "lib/arithexpr/src/main/"

lazy val root = (project in file("."))
  .settings(name := "min_hw_ir")
