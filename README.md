[![official JetBrains project](https://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

# Scala Type Search Plugin for IntelliJ IDEA

An IntelliJ plugin that allows searching for functions from any library (including the Scala Standard Library) by their type signature using the Search Everywhere window.

## Installation

1. Download the latest release.
2. Open IntelliJ.
3. Click `File -> Settings` and choose the `Plugins` tab.
4. Click the gear icon and choose `Install plugin from disk...`
5. Select the downloaded file to be installed.
6. Restart the IDE when prompted.
7. Navigate to IntelliJ's installation directory.
8. Navigate to the `plugins/ScalaTypeSearch/lib` directory
9. Create the `typeSearchDBs` directory and navigate to it.
10. Insert any database json files to be parsed by the plugin into this directory (they can be generated for each library using scaladoc with the flag `-Ygenerate-inkuire`). The Scala 3 standard library database file is included in the resource folder of this repository.
11. Open a Scala project in IntelliJ.
12. If the Type Search tab is present when accessing the Search Everywhere window, the plugin is installed correctly.

Steps 7-10 have to be repeated on each plugin or IDE update.

## Usage

When inside a Scala project, to use the plugin's functionality you must:
1. Access the Search Everywhere window (for example by pressing the `Shift` key twice)
2. Click on the `Type Search` tab.
3. Enter the query corresponding to the signature of the function you are looking for.
4. Click on one of the functions from the results to navigate to the file where the function is defined.

The basic query syntax is as follows:

`ArgType1 => ArgType2 => ... => ArgTypeN => ReturnType`

Examples :
* `Int => String` will yield all functions which take in an integer and return a String.
* `List[A] => [A]` is a polymorphic query which will return all functions operating on lists and returning a value of the same type as that of this list's elements.

The syntax detailed above can be extended in the following way:

`ArgType1 => ArgType2 => ... => ArgTypeN => ReturnType @ Library1, Library2, ...`

which will limit the search to the libraries, whose names are specified after the `@` sign. For example:

`Int => String @ scala3`

will return functions operating on integers and returning Strings, which are included in the Scala 3 standard library.

## Inkuire

More details about generating database json files as well as the query syntax is available [here](https://docs.scala-lang.org/scala3/guides/scaladoc/search-engine.html).

## [IntelliJ Platform SDK documentation](https://plugins.jetbrains.com/docs/intellij/welcome.html)

## Browsing IntelliJ platform sources

When loading the plugin in sbt, the IntelliJ platform is downloaded to `~/.ScalaTypeSearchPluginIC/sdk/<sdk version>/`.
IntelliJ platform sources should automatically attach after project has been imported and indices have been built.

However, if this didn't happen, and if you're seeing decompiled code when opening a platform API class you can click
the option "attach sources" at the top of the editor, navigate to the sdk directory and select `sources.zip`,
then choose "All".
