import sbt.Keys._

name := """Task"""

version := "0.1.0"

scalaVersion := "2.13.1"

resolvers ++= Seq(
  "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  "Bintray sbt plugin releases" at "http://dl.bintray.com/sbt/sbt-plugin-releases/",
  Resolver.mavenLocal
)

// Dependencies
libraryDependencies ++= Seq(
"dev.zio" %% "zio" % "1.0.0-RC17",
"org.typelevel" %% "cats-core" % "2.0.0",
"org.scalatest" %% "scalatest" % "3.1.0" % "test"
)

assemblyJarName in assembly := "task.jar"

mainClass in assembly := Some("test.Entry")