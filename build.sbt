import sbt._
import sbt.Keys._
import Dependencies._
import BuildSettings._

lazy val playsparkmodule = Project(
  BuildSettings.projectName,
  file("."),
  settings = BuildSettings.buildSettings ++ Seq(
    libraryDependencies ++= Dependencies.get,
    dependencyOverrides ++= Set(
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
    )
  )
)

