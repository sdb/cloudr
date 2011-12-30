
import sbt._
import Keys._
import AndroidKeys._
import com.typesafe.sbtscalariform.ScalariformPlugin._
import de.element34.sbteclipsify.Eclipsify._
import sbtfilter.Plugin._

object General {
  lazy val buildOrganization = "be.ellefant.cloudr"
  lazy val buildVersion      = "0.1"
  lazy val buildScalaVersion = "2.9.1"

  lazy val settings = Defaults.defaultSettings ++ formattingSettings ++ Seq (
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

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "cloudr" // TODO
    ) ++ Seq(
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
      libraryDependencies += Slf4jApi,
      unmanagedJars in Compile <<= (sdkPath in Android) map { (sp) =>
        Seq(sp / "platforms" / "android-10" / "android.jar").classpath
      }
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
  lazy val options = """-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature
-keep public class scala.reflect.ScalaSignature {
    public java.lang.String bytes();
}
-keep public class scala.Function0
-keep public class scala.ScalaObject

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

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

-keep class org.apache.http.entity.mime.MultipartEntity
-keep class com.cloudapp.*
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

object Idea {
  // quick 'n dirty way to add Android Facet to IDEA projects
  val command: Command = Command.command("gen-idea-android") { state =>
    val base = Project.extract (state).currentProject.base
    transform(base / ".." / ".idea_modules" / "cloudr.iml", "app")
    transform(base / ".." / ".idea_modules" / "tests.iml", "tests")
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

  case class AddFacet(module: String) extends RewriteRule {
    def path(p: String) = "/../" + module + "/" + p
    override def transform(n: Node): Seq[Node] = n match {
      case e @ Elem(prefix, "component", attribs, scope, children @ _*) if (e \ "@name").text == "FacetManager" =>
        <component name="FacetManager">
          { children }
          <facet type="android" name="Android">
            <configuration>
              <option name="GEN_FOLDER_RELATIVE_PATH_APT" value={path("gen")} />
              <option name="GEN_FOLDER_RELATIVE_PATH_AIDL" value={path("gen")} />
              <option name="MANIFEST_FILE_RELATIVE_PATH" value={path("AndroidManifest.xml")} />
              <option name="RES_FOLDER_RELATIVE_PATH" value={path("res")} />
              <option name="ASSETS_FOLDER_RELATIVE_PATH" value={path("assets")} />
              <option name="LIBS_FOLDER_RELATIVE_PATH" value={path("libs")} />
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

  case class AddFacetTransformer(module: String) extends RuleTransformer(AddFacet(module))

  def transform(f: java.io.File, module: String) = {
    val x = XML.loadFile(f)
    val t = AddFacetTransformer(module)(x)
    XML.save(f.getAbsolutePath, t)
  }
}
