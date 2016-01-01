import sbt._
import sbt.Keys._

object BuildSettings {

  val projectName = "play-spark-module"

  val buildVersion = "0.1"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    name := projectName,
    organization := "play.module.io.joaovasques",
    version := buildVersion,
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-unchecked", "-deprecation",
      "-Xlint", "-Ywarn-dead-code",
      "-language:_",
      //"-language:_", "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    javaOptions ++= Seq("-Dscalac.patmat.analysisBudget=off"),
    //crossScalaVersions := Seq("2.11.6"),
    crossVersion := CrossVersion.binary,
    shellPrompt := ShellPrompt.buildShellPrompt
  ) ++ Publish.settings ++ Travis.settings
}



// Shell prompt which show the current project,
// git branch and build version
object ShellPrompt {

  object devnull extends ProcessLogger {
    def info(s: => String) {}

    def error(s: => String) {}

    def buffer[T](f: => T): T = f
  }

  def currBranch = (
    ("git status -sb" lines_! devnull headOption)
      getOrElse "-" stripPrefix "## "
    )

  val buildShellPrompt = {
    (state: State) => {
      val currProject = Project.extract(state).currentProject.id
      "%s:%s:%s> ".format(
        currProject, currBranch, BuildSettings.buildVersion
      )
    }
  }
}

object Publish {
  @inline def env(n: String): String = sys.env.get(n).getOrElse(n)

  private val repoName = env("PUBLISH_REPO_NAME")
  private val repoUrl = env("PUBLISH_REPO_URL")

  lazy val settings = Seq(
    publishMavenStyle := false,
    licenses := Seq("Apache 2.0" ->
      url("http://www.apache.org/licenses/LICENSE-2.0"))
  )
}

object Travis {
  val travisSnapshotBranches =
    SettingKey[Seq[String]]("branches that can be published on sonatype")

  val travisCommand = Command.command("publishSnapshotsFromTravis") { state =>
    val extracted = Project extract state
    import extracted._
    import scala.util.Properties.isJavaAtLeast

    val thisRef = extracted.get(thisProjectRef)

    val isSnapshot = getOpt(version).exists(_.endsWith("SNAPSHOT"))
    val isTravisEnabled = sys.env.get("TRAVIS").exists(_ == "true")
    val isNotPR = sys.env.get("TRAVIS_PULL_REQUEST").exists(_ == "false")
    val isBranchAcceptable = sys.env.get("TRAVIS_BRANCH").exists(branch => getOpt(travisSnapshotBranches).exists(_.contains(branch)))
    val isJavaVersion = !isJavaAtLeast("1.7")

    if (isSnapshot && isTravisEnabled && isNotPR && isBranchAcceptable) {
      println(s"publishing $thisRef from travis...")

      val newState = append(
        Seq(
          publishTo := Some("Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"),
          credentials := Seq(Credentials(
            "Sonatype Nexus Repository Manager",
            "oss.sonatype.org",
            sys.env.get("SONATYPE_USER").getOrElse(throw new RuntimeException("no SONATYPE_USER defined")),
            sys.env.get("SONATYPE_PASSWORD").getOrElse(throw new RuntimeException("no SONATYPE_PASSWORD defined"))
          ))),
        state
      )

      runTask(publish in thisRef, newState)

      println(s"published $thisRef from travis")
    } else {
      println(s"not publishing $thisRef to Sonatype: isSnapshot=$isSnapshot, isTravisEnabled=$isTravisEnabled, isNotPR=$isNotPR, isBranchAcceptable=$isBranchAcceptable, javaVersionLessThen_1_7=$isJavaVersion")
    }

    state
  }

  val settings = Seq(
    Travis.travisSnapshotBranches := Seq("master"),
    commands += Travis.travisCommand)
}

