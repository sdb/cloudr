
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
      DefaultMavenRepository,
      ScalaToolsReleases,
      "Local Maven" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
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
  // lazy val Slf4jSimple = "org.slf4j" % "slf4j-simple" % Slf4jVer
  lazy val CloudApp = "com.cloudapp" % "com.cloudapp.rest" % "0.1-SNAPSHOT"
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
  lazy val AndroidSupport13 = "android.support" % "compatibility-v13" % "r3"
}

object AndroidBuild extends Build {
  import Dependencies._

  lazy val slf4jAndroid = Project(
    "slf4j",
    file("slf4j"),
    settings = Defaults.defaultSettings ++ AndroidPath.settings ++ Seq(
      name := "slf4j-android",
      organization := "org.slf4j",
      version      := "1.6.3",
      libraryDependencies += Slf4jApi,
      unmanagedJars in Compile <<= (sdkPath in Android) map { (sp) =>
        Seq(sp / "platforms" / "android-10" / "android.jar").classpath
      }
    )
  )

  lazy val mainDeps = Seq(
    libraryDependencies ++= Seq(
      CloudApp intransitive(),
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

  lazy val main = Project(
    "cloudr",
    file("."),
    settings = General.fullAndroidSettings ++ mainDeps ++ Seq(
      name := "cloudr",
      parallelExecution in Test := false,
      testOptions in Test += Tests.Argument("junitxml", "console"),
      commands += Idea.command,
      libraryDependencies <+= (sdkPath in Android) apply { (sp) =>
        AndroidSupport13 from (sp / "extras" / "android" / "support" / "v13" / "android-support-v13.jar").toURI.toString
      },
      proguardOption in Android := Proguard.options,
      proguardOptimizations in Android := List("-dontobfuscate", "-dontoptimize"),
      internalDependencyClasspath in Test <<= (internalDependencyClasspath in Test) map { (cp) =>
        (cp filterNot (_.data.absolutePath.contains("slf4j"))) // exclude slf4j-android in test
      }
    )
  ) dependsOn (slf4jAndroid)

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

object Idea {
  // quick 'n dirty way to add Android Facet to IDEA projects
  val command: Command = Command.command("gen-idea-android") { state =>
    val base = Project.extract (state).currentProject.base
    transform(base / ".idea_modules" / "cloudr.iml")
    transform(base / ".idea_modules" / "tests.iml")
    state
  }

  import xml._
  import xml.transform._

  object ReplaceJdk extends RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case e @ Elem(prefix, "orderEntry", attribs, scope, children @ _*) if (e \ "@type").text == "inheritedJdk" =>
        <orderEntry type="jdk" jdkName="Android 3.2 Platform" jdkType="Android SDK" />
      case other => other
    }
  }

  object ReplaceJdkTransformer extends RuleTransformer(ReplaceJdk)

  object AddFacet extends RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case e @ Elem(prefix, "component", attribs, scope, children @ _*) if (e \ "@name").text == "FacetManager" =>
        <component name="FacetManager">
          { children }
          <facet type="android" name="Android">
            <configuration>
              <option name="GEN_FOLDER_RELATIVE_PATH_APT" value="/gen" />
              <option name="GEN_FOLDER_RELATIVE_PATH_AIDL" value="/gen" />
              <option name="MANIFEST_FILE_RELATIVE_PATH" value="/../src/main/AndroidManifest.xml" />
              <option name="RES_FOLDER_RELATIVE_PATH" value="/../src/main/res" />
              <option name="ASSETS_FOLDER_RELATIVE_PATH" value="/../src/main/assets" />
              <option name="LIBS_FOLDER_RELATIVE_PATH" value="/../src/main/libs" />
              <option name="REGENERATE_R_JAVA" value="true" />
              <option name="REGENERATE_JAVA_BY_AIDL" value="true" />
              <option name="USE_CUSTOM_APK_RESOURCE_FOLDER" value="false" />
              <option name="CUSTOM_APK_RESOURCE_FOLDER" value="" />
              <option name="USE_CUSTOM_COMPILER_MANIFEST" value="false" />
              <option name="CUSTOM_COMPILER_MANIFEST" value="" />
              <option name="APK_PATH" value="" />
              <option name="LIBRARY_PROJECT" value="false" />
              <option name="RUN_PROCESS_RESOURCES_MAVEN_TASK" value="true" />
              <option name="GENERATE_UNSIGNED_APK" value="false" />
            </configuration>
          </facet>
        </component>
      case e @ Elem(prefix, "component", attribs, scope, children @ _*) if (e \ "@name").text == "NewModuleRootManager" =>
        ReplaceJdkTransformer(e)
      case other => other
    }
  }

  object AddFacetTransformer extends RuleTransformer(AddFacet)

  def transform(f: java.io.File) = {
    val x = XML.loadFile(f)
    val t = AddFacetTransformer(x)
    XML.save(f.getAbsolutePath, t)
  }
}
