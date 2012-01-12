resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "sdb@github" at "http://sdb.github.com/maven",
  "gseitz@github" at "http://gseitz.github.com/maven/"
)

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.4")

addSbtPlugin("org.scala-tools.sbt" % "sbt-android-plugin" % "0.6.0")

// addSbtPlugin("de.element34" %% "sbt-eclipsify" % "0.11.0-SNAPSHOT")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.2.0")

addSbtPlugin("com.github.sdb" % "xsbt-filter" % "0.1")