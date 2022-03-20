import org.jetbrains.sbtidea.Keys._

ThisBuild / intellijPluginName := "ScalaTypeSearch"
ThisBuild / intellijBuild      := "221"
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
      intellijPlugins += "org.intellij.scala:2022.1.2".toPlugin,
      libraryDependencies += "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.5" withSources(),
      unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
      unmanagedResourceDirectories in Test    += baseDirectory.value / "testResources",
    )
