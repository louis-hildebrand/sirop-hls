ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"
Compile / unmanagedSourceDirectories += baseDirectory.value / "lib/arithexpr/src/main/"

scalacOptions += "-Wconf:cat=other-match-analysis:error"

lazy val root = (project in file("."))
  .settings(name := "min_hw_ir")
