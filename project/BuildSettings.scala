import sbt._
import sbt.Keys._

object BuildSettings {

  val projectName = "play-spark-module"

  val buildVersion = "0.0.1-SNAPSHOT"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    name := projectName,
    organization := "play.module.io.joaovasques",
    version := buildVersion,
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-unchecked", "-deprecation",
      "-Xlint", "-Ywarn-dead-code",
      //"-language:_", "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    javaOptions ++= Seq("-Dscalac.patmat.analysisBudget=off"),
    //crossScalaVersions := Seq("2.11.6"),
    crossVersion := CrossVersion.binary
  )
  //TODO ++ Publish.settings ++ Format.settings ++ Travis.settings
}

