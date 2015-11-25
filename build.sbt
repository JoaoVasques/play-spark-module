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
    ),
    parallelExecution in Test := false,
    resolvers := Seq(
      "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "Sonatype" at "http://oss.sonatype.org/content/groups/public/",
      "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
      "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
    ),
    target in Compile in doc := baseDirectory.value / "doc"
  )
)

