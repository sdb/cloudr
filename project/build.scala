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
    resolvers ++= Seq(
      "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
    )
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "cloudr" // TODO
    )
}

object Dependencies {
  lazy val Slf4jVer = "1.6.3"

  lazy val Slf4jApi = "org.slf4j" % "slf4j-api" % Slf4jVer
  lazy val Slf4jSimple = "org.slf4j" % "slf4j-simple" % Slf4jVer
  lazy val CloudApp = "com.cloudapp" % "com.cloudapp.rest" % "0.1-SNAPSHOT"
  lazy val RoboGuice = "org.roboguice" % "roboguice" % "2.0b2"
  lazy val Guice = "com.google.inject" % "guice" % "3.0"
  // lazy val Robolectric = "com.pivotallabs" % "robolectric" % "1.0-RC4"
  lazy val Mockito = "org.mockito" % "mockito-core" % "1.9.0-rc1"
  lazy val Robospecs = "com.github.jbrechtel" %% "robospecs" % "0.2-SNAPSHOT"
  lazy val EasyMock = "org.easymock" % "easymock" % "3.0"
}

object AndroidBuild extends Build {
  import Dependencies._

  lazy val mainDeps = Seq(
    libraryDependencies ++= Seq(
      CloudApp intransitive(),
      Slf4jApi,
      Slf4jSimple,
      RoboGuice intransitive(),
      Guice classifier "no_aop",
      Mockito % "test",
      Robospecs % "test"
    )
  )

  lazy val main = Project(
    "cloudr",
    file("."),
    settings = General.fullAndroidSettings ++ mainDeps ++ Seq(
      name := "cloudr",
      proguardOption in Android := Proguard.options,
      proguardOptimizations in Android := List("-dontobfuscate", "-dontoptimize")
    )
  )

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
  ) dependsOn main
}

object Proguard {
  lazy val options = """-optimizations !code/simplification/arithmetic
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature
-keep public class scala.reflect.ScalaSignature {
    public java.lang.String bytes();
}
-keep public class scala.Function0
-keep public class scala.ScalaObject

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep class com.google.inject.Binder
-keepclassmembers class * {
    @com.google.inject.Inject <init>(...);
}
-keepclassmembers class * {
    void *(**On*Event);
}
-keepclassmembers class * {
    @com.google.inject.Inject <init>(...);
    @com.google.inject.Inject <fields>;
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepclasseswithmembers class * { native <methods>; }
-keepclasseswithmembers class * {
    public <init> (android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init> (android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements android.os.Parcelable { static android.os.Parcelable$Creator *; }
-keepclassmembers class **.R$* { public static <fields>; }
-keepclasseswithmembernames class * { native <methods>; }
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keepclassmembers class com.google.inject.util.Modules$OverriddenModuleBuilder { <methods>; }
-keepclassmembers public class com.google.inject.internal.util.$Finalizer { public static <methods>; }
-keepclassmembers public class com.google.inject.util.Modules { public static <methods>; }
-keep public class roboguice.**
-keep class com.google.inject.Binder
-keep class com.google.inject.Module
-keep class com.google.inject.Scope
-keep class com.google.inject.TypeLiteral
-keep class com.google.inject.Key
-keep class com.google.inject.matcher.Matcher
-keep class com.google.inject.spi.*
"""
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
