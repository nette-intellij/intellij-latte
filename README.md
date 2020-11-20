Latte Plugin for IntelliJ IDEA / PhpStorm
=========================================

Provides support for [Latte](https://github.com/nette/latte) – a template engine for PHP.

- Plugin page: [IntelliJ plugin Latte](https://plugins.jetbrains.com/plugin/7457-latte)
- Forum: [Nette forum](https://forum.nette.org/en/32907-upgrades-in-latte-plugin-for-phpstorm)


Installation
------------

Settings → Plugins → Browse repositories → Find "Latte" → Install Plugin → Restart IDE


Sponsors
------

<a href="https://www.futurerockstars.cz/"><img src="https://i.imgur.com/uqdF6OJ.png" alt="FutureRockstars" width="188" height="86"></a>

Does GitHub already have your 💳? Does Nette plugins save you development time? [Send a couple of 💸 a month my way too.](https://github.com/sponsors/mesour) Thank you!

One-time donations through [PayPal](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=GSDRZW9YGPE5G&source=url) are also accepted. To request an invoice, [contact me](mailto:matous.nemec@mesour.com) through e-mail.


Supported Features
------------------

* Syntax highlighting
* Code completion for PHP, classic HTML, attribute tags and more
* Registering custom tags, filters and custom functions


Testing EAP versions
--------------------

Latte plugin for IntelliJ is downloadable from the JetBrains plugin repository directly in the PHPStorm. Latte plugin for IntelliJ using channel `eap` which contains the latest release candidate.

- Here is, how to set up custom release channels in PHPStorm: [Custom release channels](https://plugins.jetbrains.com/docs/marketplace/custom-release-channels.html)
- You have to add this link: `https://plugins.jetbrains.com/plugins/eap/list` to your Custom plugin repositories
- After you add the link above, you can refresh your plugins Marketplace and you will see RC versions for Latte plugin


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

Known Limitations
-----------------

* Low-level macros `{syntax}` is not supported, `{contentType}` is supported partially.

![Screenshot](http://plugins.jetbrains.com/files/7457/screenshot_14518.png)
