resolvers ++= Seq(
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.0-SNAPSHOT")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.9.0")

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "1.0.0")