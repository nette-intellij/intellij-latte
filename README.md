Latte Plugin for IntelliJ IDEA / PhpStorm
=========================================

Provides support for [Latte](https://github.com/nette/latte) – a template engine for PHP.


Installation
------------

Settings → Plugins → Browse repositories → Find "Latte" → Install Plugin → Restart IDE


Building
------------

Plugin uses Gradle to build, but before build you need to install **Grammar-Kit** plugin to Intellij Idea, right click in file explorer to **LatteParser.bnf** located in **com.jantvrdik.intellij.latte**, click to **Generate Parser Code**, then select PSI root to **src/main/gen/com/jantvrdik/intellij/latte/psi** and generated parser as **src/main/gen/com/jantvrdik/intellij/latte/parser/LatteParser.class**

After generating parser files, you need to generate a .flex file from the same .bnf file, right click to file and choose option **Generate JFlex Lexer** and generate **_LatteLexer.flex** in the same folder.

Now you can build plugin using gradle, and it will automatically generate another classes from .flex files, if you want you can help us automate system, so next time no one must do the manual generation work.
```$xslt
$ gradlew build
```

To build `.jar` file to local install to IDE run gradle task `buildPlugin`. `.jar` file with plugin will be located in `build/libs`


Run IDE for testing
-------------------

Create file `local.properties` in project and insert next content with path to IDE directory:

For your current OS see `default IDE paths`: https://www.jetbrains.com/help/idea/tuning-the-ide.html#default-dirs

```
runIdeDirectory = /Users/<user id>/Library/Application Support/JetBrains/Toolbox/apps/PhpStorm/ch-0/201.7223.96/PhpStorm 2019.3 EAP.app/Contents
```

And run gradle task `:runIde` ideally by run configurations in IDEA (it supports debugging).


Supported Features
------------------

* Syntax highlighting
* Code completion for both classic and attribute macros
* Registering custom macros


Known Limitations
-----------------

* Low-level macros `{syntax}` is not supported, `{contentType}` is supported partially.

![Screenshot](http://plugins.jetbrains.com/files/7457/screenshot_14518.png)
