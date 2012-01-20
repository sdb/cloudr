import sbt._
import Keys._
import AndroidKeys._
import com.typesafe.sbtscalariform.ScalariformPlugin._
import de.element34.sbteclipsify.Eclipsify._
import sbtfilter.Plugin._
import org.apache.commons.io.FileUtils

object General {
  lazy val buildOrganization = "be.ellefant.cloudr"
  lazy val buildVersion      = "0.2.1-SNAPSHOT"
  lazy val buildScalaVersion = "2.9.1"

  lazy val settings = Defaults.defaultSettings ++ formattingSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    shellPrompt  := ShellPrompt.buildShellPrompt(buildVersion),
    platformName in Android := "android-10",
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    resolvers ++= Seq(
      DefaultMavenRepository,
      ScalaToolsReleases,
      "Local Maven" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
    ),
    projectNature := de.element34.sbteclipsify.ProjectType.Scala
  )

  lazy val formattingSettings = (inConfig(Compile)(baseScalariformSettings) ++ inConfig(Test)(baseScalariformSettings)) ++ Seq(
    ScalariformKeys.formatPreferences in Compile := formattingPreferences,
    ScalariformKeys.formatPreferences in Test    := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(IndentLocalDefs, true)
      .setPreference(RewriteArrowSymbols, true)
  }

  private def signReleaseTask: Project.Initialize[Task[File]] =
    (keystorePath, packageApkPath, streams) map { (ksPath, pPath,s ) =>
      val jarsigner = Seq(
        "jarsigner",
        "-verbose",
        "-keystore", ksPath.absolutePath,
        "-storepass", getPassword,
        pPath.absolutePath,
        "cloudr")
      s.log.debug("Signing "+jarsigner.mkString(" "))
      s.log.debug(jarsigner !!)
      s.log.info("Signed "+pPath)
      pPath
    }

  private def getPassword = System.getProperty("cloudr.keystore_password")

  lazy val fullAndroidSettings = AndroidMarketPublish.settings ++ inConfig(Android)(Seq(
      signRelease <<= signReleaseTask,
      signRelease <<= signRelease dependsOn packageRelease
    )) ++
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++ Seq(
      projectNature := de.element34.sbteclipsify.ProjectType.ScalaAndroid
    )
}

object Dependencies {
  lazy val Slf4jVer = "1.6.3"

  lazy val Slf4jApi = "org.slf4j" % "slf4j-api" % Slf4jVer
  // lazy val Slf4jSimple = "org.slf4j" % "slf4j-simple" % Slf4jVer
  // lazy val CloudApp = "com.cloudapp" % "com.cloudapp.rest" % "0.2-SNAPSHOT"
  lazy val RoboGuice = "org.roboguice" % "roboguice" % "2.0b2"
  lazy val Guice = "com.google.inject" % "guice" % "3.0"
  lazy val Robolectric = "com.pivotallabs" % "robolectric" % "1.1-SNAPSHOT"
  lazy val Mockito = "org.mockito" % "mockito-core" % "1.9.0-rc1"
  lazy val RoboSpecs = "com.github.jbrechtel" %% "robospecs" % "0.2-SNAPSHOT"
  lazy val Specs = "org.specs2" %% "specs2" % "1.6.1"
  lazy val EasyMock = "org.easymock" % "easymock" % "3.0"
  lazy val JUnit = "junit" % "junit" % "4.8.2"
  lazy val Slf4jAndroid = "org.slf4j" % "slf4j-android" % Slf4jVer
  lazy val Slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.7"
  lazy val Logback = "ch.qos.logback" % "logback-classic" % "0.9.30"
  lazy val AndroidSupport13 = "android.support" % "compatibility-v13" % "r4"
  lazy val ScalaAndroid = "com.github.sdb" %% "scala-android" % "0.1.0-SNAPSHOT"
  lazy val HttpClient = "org.apache.httpcomponents" % "httpclient" % "4.0.1"
  lazy val HttpMime = "org.apache.httpcomponents" % "httpmime" % "4.0.1"
  lazy val Json = "org.json" % "json" % "20090211"
}

object AndroidBuild extends Build {
  import Dependencies._

  lazy val slf4jAndroid = Project(
    "slf4j",
    file("slf4j"),
    settings = Defaults.defaultSettings ++ AndroidPath.settings ++ Seq( // TODO remove path
      name := "slf4j-android",
      organization := "org.slf4j",
      version      := "1.6.3",
      javaSource in Compile <<= baseDirectory(_ / "src"),
      javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
      libraryDependencies += Slf4jApi,
      unmanagedJars in Compile <<= (sdkPath in Android) map { (sp) =>
        Seq(sp / "platforms" / "android-10" / "android.jar").classpath
      },
      shellPrompt <<= version apply (ShellPrompt.buildShellPrompt(_))
    )
  )

  lazy val sdroid = Project(
    "sdroid",
    file("sdroid"),
    settings = General.settings ++ AndroidPath.settings ++ Seq( // TODO
      name := "sdroid",
      libraryDependencies += ScalaAndroid,
      scalaSource in Compile <<= baseDirectory(_ / "src"),
      unmanagedJars in Compile <<= (sdkPath in Android) map { (sp) =>
        Seq(sp / "platforms" / "android-10" / "android.jar").classpath
      }
    )
  )

  lazy val cloudapp = Project(
    "cloudapp",
    file("cloudapp"),
    settings = General.settings ++ Seq(
      name := "cloudapp",
      javaSource in Compile <<= baseDirectory(_ / "src"),
      libraryDependencies ++= Seq(
        HttpClient % "provided",
        HttpMime,
        Json  % "provided",
        Slf4jApi
      )
    )
  )

  lazy val mainDeps = Seq(
    libraryDependencies ++= Seq(
      Slf4jApi,
      Logback % "test",
      Slf4s,
      RoboGuice intransitive(),
      Guice classifier "no_aop",
      Mockito % "test",
      RoboSpecs % "test" intransitive(),
      Specs % "test",
      Robolectric % "test",
      JUnit % "test"
    )
  )

  lazy val app = Project(
    "cloudr",
    file("app"),
    settings = General.fullAndroidSettings ++ mainDeps ++ filterSettings ++ inConfig(Android)(Seq(
      manifestPath <<= (baseDirectory, manifestName in Android) map { (base, name) => Seq(base / name) },
      mainAssetsPath <<= baseDirectory (_ / "assets"),
      mainResPath <<= baseDirectory (_ / "res"),
      resourceDirectory := new File("blabla") // resources directory doesn't need to be packaged, it's already contained in the minified JAR
    )) ++ Seq(
      name := "cloudr",
      parallelExecution in Test := false,
      testOptions in Test += Tests.Argument("junitxml", "console"),
      commands ++= Seq(Idea.command, Eclipse.command),
      libraryDependencies <+= (sdkPath in Android) apply { (sp) =>
        AndroidSupport13 from (sp / "extras" / "android" / "support" / "v13" / "android-support-v13.jar").toURI.toString
      },
      proguardOption in Android := Proguard.options,
      proguardOptimizations in Android := List("-dontobfuscate", "-dontoptimize"),
      proguardInJars in Android <<= (fullClasspath in Android, proguardExclude in Android) map {
        (cp, proguardExclude) =>
          (((cp filterNot (d => (d.data.getName startsWith "httpcore") || (d.data.getName startsWith "httpclient"))) map (_.data))  --- proguardExclude) get
      },
      internalDependencyClasspath in Test <<= (internalDependencyClasspath in Test) map { (cp) =>
        (cp filterNot (_.data.absolutePath.contains("slf4j"))) // HACK exclude slf4j-android in test
      },
      scalaSource in Compile <<= baseDirectory (_ / "src"),
      resourceDirectory in Compile <<= baseDirectory (_ /"resources"),
      scalaSource in Test <<= baseDirectory (_ /"test"),
      resourceDirectory in Test <<= baseDirectory (_ /"test-resources"),
      FilterKeys.projectProps ~= { _ map (p => ("project." + p._1, p._2)) },
      FilterKeys.extraProps ++= Seq(
        "app.name" -> "Cloudr",
        "cloudapp.host" -> "my.cl.ly",
        "cloudapp.port" -> "80",
        "cloudapp.auth" -> "digest")
    )
  ) dependsOn (slf4jAndroid, sdroid, cloudapp)

  lazy val testsDeps = Seq(
    libraryDependencies ++= Seq(
      EasyMock
    )
  )

  lazy val tests = Project(
    "tests",
    file("tests"),
    settings = General.settings ++ AndroidTest.androidSettings ++ testsDeps ++ Seq(
      name := "cloudr-tests",
      proguardInJars in Android <<= (fullClasspath in Android, proguardExclude in Android) map {
        (cp, proguardExclude) =>
          ((cp filter (_.data.getName startsWith "easymock")) map (_.data))  --- proguardExclude get
      },
      proguardOptimizations in Android := List("-dontobfuscate", "-dontoptimize"),
      proguardOption in Android := """-optimizations !code/simplification/arithmetic
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature
-keep class org.easymock.**
"""
    )
  ) dependsOn app
}

object Proguard {
  lazy val options = FileUtils.readFileToString(new File("project/proguard_options.txt"))
}

object Eclipse {
  val command: Command = Command.command("eclipse-android") { state =>
    val base = Project.extract (state).currentProject.base
    IO.write(base /"proguard.cfg", Proguard.options)
    val props = new java.util.Properties
    props.setProperty("target", "android-10")
    IO.write(props, null, base /"project.properties")
    state
  }
}