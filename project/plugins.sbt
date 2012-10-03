resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "sdb@github" at "http://sdb.github.com/maven",
  "mpeltonen@github" at "http://mpeltonen.github.com/maven/",
  "gseitz@github" at "http://gseitz.github.com/maven/"
)

// addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.4")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.3.1")

addSbtPlugin("com.github.sdb" % "xsbt-filter" % "0.2")

libraryDependencies += "commons-io" % "commons-io" % "2.0.1"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/groups/scala-tools/"