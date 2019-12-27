import sbt.Keys._

name := """Task"""

version := "0.1.0"

val zioVersion = "1.0.0-RC17"

scalaVersion := "2.13.1"

resolvers ++= Seq(
  "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  ("Bintray sbt plugin releases" at "http://dl.bintray.com/sbt/sbt-plugin-releases/").withAllowInsecureProtocol(true),
  Resolver.mavenLocal
)

// Dependencies
libraryDependencies ++= Seq(
"dev.zio" %% "zio" % zioVersion,
"dev.zio" %% "zio-test"     % zioVersion % "test",
"dev.zio" %% "zio-test-sbt" % zioVersion % "test",
"org.typelevel" %% "cats-core" % "2.0.0",
"org.scalatest" %% "scalatest" % "3.1.0" % "test"
)

testFrameworks ++= Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

assemblyJarName in assembly := "task.jar"

mainClass in assembly := Some("test.Entry")