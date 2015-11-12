import sbt._
import sbt.Keys._
import Dependencies._
import BuildSettings._

lazy val playsparkmodule = Project(
  BuildSettings.projectName,
  file("."),
  settings = BuildSettings.buildSettings ++ Seq(
    libraryDependencies ++= Dependencies.get
  )
)

