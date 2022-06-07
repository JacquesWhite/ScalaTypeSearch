[![official JetBrains project](https://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

# Scala Type Search Plugin for IntelliJ IDEA

Plugin that allows searching functions from Scala Standard Library by type in the "Seach Everywhere" window.


##[IntelliJ Platform SDK documentation](https://plugins.jetbrains.com/docs/intellij/welcome.html)

## Browsing IntelliJ platform sources

When loading the plugin in sbt, the IntelliJ platform is downloaded to `~/.ScalaTypeSearchPluginIC/sdk/<sdk version>/`.
IntelliJ platform sources should automatically attach after project has been imported and indices have been built.

However, if this didn't happen, and if you're seeing decompiled code when opening a platform API class you can click
the option "attach sources" at the top of the editor, navigate to the sdk directory and select `sources.zip`,
then choose "All".
