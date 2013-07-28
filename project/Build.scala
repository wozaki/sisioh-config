import sbt._
import sbt.Keys._

object ConfigBuild extends Build {
  val root = Project(
    id = "sisioh-config",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      organization := "org.sisioh",
      version := "0.0.12",
      scalaVersion := "2.10.2",
      scalacOptions ++= Seq("-encoding", "UTF-8", "-feature", "-deprecation", "-unchecked"),
      javacOptions ++= Seq("-encoding", "UTF-8", "-deprecation"),
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % "2.10.2",
        "junit" % "junit" % "4.8.1" % "test",
        "org.hamcrest" % "hamcrest-all" % "1.3" % "test",
        "org.mockito" % "mockito-core" % "1.9.5" % "test",
        "org.specs2" %% "specs2" % "1.14" % "test",
        "com.typesafe" % "config" % "1.0.2"
      )
    )
  )
}
