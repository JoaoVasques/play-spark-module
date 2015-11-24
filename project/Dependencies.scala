import sbt._

object Dependencies {

  private final val akkaVersion = "2.3.9"
  private final val sparkVersion = "1.5.2"
  private final val playVersion = "2.4.2"

  lazy val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion

  lazy val akkaLog = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

  lazy val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

  lazy val guice = "com.google.inject" % "guice" % "4.0-beta5"

  lazy val scalaGuice = "net.codingwell" %% "scala-guice" % "4.0.0-beta5"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.2.1" % "test"

  lazy val spark =  "org.apache.spark" %% "spark-core" % sparkVersion

  lazy val mongo = "org.mongodb" %% "casbah" % "3.0.0"

  lazy val play = "com.typesafe.play" %% "play" % playVersion % "provided" cross CrossVersion.binary

  lazy val playTest = "com.typesafe.play" %% "play-test" % playVersion % "test" cross CrossVersion.binary

  lazy val specs = "org.specs2" % "specs2" % "2.3.12" % "test" cross CrossVersion.binary

  def get() = {
    Seq(akka, akkaLog, akkaTestKit, guice, scalaGuice, scalaTest, spark, mongo, play, playTest, specs)
  }
}

