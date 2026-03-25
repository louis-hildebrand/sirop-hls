ThisBuild / version := {
  val versionSource = scala.io.Source.fromFile("src/main/resources/version.txt")
  try {
    val version = versionSource.mkString.strip
    if (version.contains("SNAPSHOT")) {
      val commitSource =
        scala.io.Source.fromFile("src/main/resources/commit.txt")
      try {
        val commit = commitSource.mkString.strip
        version.replace("SNAPSHOT", commit)
      } finally {
        commitSource.close()
      }
    } else {
      version
    }
  } finally {
    versionSource.close()
  }
}
ThisBuild / scalaVersion := "2.12.19"
ThisBuild / autoAPIMappings := true
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
  .settings(
    name := "sirop",
    assembly / mainClass := Some("mhir.main.Compiler"),
    assembly / assemblyJarName := "sirop.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) =>
        xs.map(_.toLowerCase) match {
          case "services" :: xs =>
            MergeStrategy.filterDistinctLines
          case _ => MergeStrategy.discard
        }
      case PathList("reference.conf") => MergeStrategy.concat
      case x                          => MergeStrategy.first
    }
  )
