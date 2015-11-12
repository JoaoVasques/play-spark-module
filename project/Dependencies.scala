import sbt._

object Dependencies {

  private final val akkaVersion = "2.3.9"

  lazy val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion

  lazy val akkaLog = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

  lazy val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

  lazy val guice = "com.google.inject" % "guice" % "4.0-beta5"

  lazy val scalaGuice = "net.codingwell" %% "scala-guice" % "4.0.0-beta5"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.2.1" % "test"

  def get() = {
    Seq(akka, akkaLog, akkaTestKit, guice, scalaGuice, scalaTest)
  }
}

