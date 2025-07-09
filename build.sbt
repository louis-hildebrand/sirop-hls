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

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
libraryDependencies += "org.scala-lang" %% "toolkit" % "0.1.7"
Compile / unmanagedSourceDirectories += baseDirectory.value / "lib/arithexpr/src/main/"

lazy val root = (project in file("."))
  .settings(name := "min_hw_ir")
