import sbt._

object PluginDef extends Build {
  override lazy val projects = Seq(root)
  lazy val root = Project("plugins", file(".")) dependsOn( androidPlugin, eclipsifyPlugin )
  lazy val androidPlugin = uri("git://github.com/jberkel/android-plugin#0.6.0")
  lazy val eclipsifyPlugin = uri("git://github.com/sdb/SbtEclipsify.git")
}