package mhir.main.shared

/** The current compiler version.
  */
object Version {

  /** Get the current version string.
    */
  def apply(): String = {
    val versionSource = scala.io.Source.fromResource("version.txt")
    try {
      val version = versionSource.mkString.strip
      if (version.contains("SNAPSHOT")) {
        version.replace("SNAPSHOT", currentCommit)
      } else {
        version
      }
    } finally {
      versionSource.close()
    }
  }

  private def currentCommit: String = {
    val commitSource = scala.io.Source.fromResource("commit.txt")
    try {
      commitSource.mkString.strip
    } finally {
      commitSource.close()
    }
  }
}
