import sbt._
import Keys._
import AndroidKeys._

object General {
  val buildOrganization = "be.ellefant"
  val buildVersion      = "0.0.1-SNAPSHOT"
  val buildScalaVersion = "2.9.1"

  val settings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    shellPrompt  := ShellPrompt.buildShellPrompt(buildVersion),
    platformName in Android := "android-10",
    resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me" // TODO
    )
}

object Dependencies {
  lazy val Slf4jVer = "1.6.3"

  lazy val Slf4jApi = "org.slf4j" % "slf4j-api" % Slf4jVer
  lazy val Slf4jSimple = "org.slf4j" % "slf4j-simple" % Slf4jVer
  lazy val CloudApp = "com.cloudapp" % "com.cloudapp.rest" % "0.1-SNAPSHOT"
  lazy val ScalaTest = "org.scalatest" %% "scalatest" % "1.6.1"
}

object AndroidBuild extends Build {
  import Dependencies._

  lazy val mainDeps = Seq(
    libraryDependencies ++= Seq(
      CloudApp intransitive(),
      Slf4jApi,
      Slf4jSimple,
      ScalaTest % "test"
    )
  )
  lazy val main = Project(
    "cloudapp",
    file("."),
    settings = General.fullAndroidSettings ++ mainDeps ++ Seq(
      name := "android-cloudapp"
    )
  )

  lazy val tests = Project(
    "tests",
    file("tests"),
    settings = General.settings ++ AndroidTest.androidSettings ++ Seq(
      name := "android-cloudapp-tests"
    )
  ) dependsOn main
}

// Shell prompt which shows the current project,
// git branch and build version
object ShellPrompt {
  object devnull extends ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) { }
    def buffer[T] (f: => T): T = f
  }
  def currBranch = (
    ("git branch" lines_! devnull filter (_ startsWith "*") headOption)
      getOrElse "-" stripPrefix "* "
  )

  def buildShellPrompt(buildVersion: String) = {
    (state: State) => {
      val currProject = Project.extract (state).currentProject.id
      "%s:%s:%s> ".format (
        currProject, currBranch, buildVersion
      )
    }
  }
}
