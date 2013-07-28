import sbt._
import sbt.Keys._

object ConfigBuild extends Build {
        val root = Project(
                id = "sisioh-config",
                base = file("."),
                settings = Project.defaultSettings ++ Seq()
        )
}
