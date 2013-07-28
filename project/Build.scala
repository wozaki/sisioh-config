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
      libraryDependencies += "com.typesafe" % "config" % "1.0.2"
    )
  )
}
