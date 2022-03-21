import org.jetbrains.sbtidea.Keys._

ThisBuild / intellijPluginName := "ScalaTypeSearch"
ThisBuild / intellijBuild      := "221.4906.8"
ThisBuild / intellijPlatform   := IntelliJPlatform.IdeaCommunity

lazy val scalaTypeSearch =
  project.in(file("."))
    .enablePlugins(SbtIdeaPlugin)
    .settings(
      version := "0.0.1-SNAPSHOT",
      scalaVersion := "2.13.7",
      Global    / intellijAttachSources := true,
      Compile / javacOptions ++= "--release" :: "11" :: Nil,
      intellijPlugins += "com.intellij.properties".toPlugin,
      intellijPlugins += "org.intellij.scala:2022.1.4".toPlugin,
      libraryDependencies += "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.5" withSources(),
      // https://mvnrepository.com/artifact/org.virtuslab/inkuire-engine
      libraryDependencies += "org.virtuslab" %% "inkuire-engine" % "1.0.0-M5",
      libraryDependencies += "org.typelevel" %% "cats-kernel" % "2.3.0",
      unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
      unmanagedResourceDirectories in Test    += baseDirectory.value / "testResources",
    )
