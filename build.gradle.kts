import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.13.2"
    // Gradle GrammarKit Plugin - https://github.com/JetBrains/gradle-grammar-kit-plugin
    id("org.jetbrains.grammarkit") version "2021.2.1"
    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "2.0.0"
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "0.1.13"
    // Gradle Kover Plugin
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Configure project's dependencies
repositories {
    mavenCentral()
}

// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin {
    jvmToolchain(17)
}

sourceSets {
    main {
        java.srcDirs("src/main/gen")
    }
    test {
        java.srcDirs("src/main/gen")
    }
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) })
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl.set(properties("pluginRepositoryUrl"))
}

grammarKit {
    // Version of IntelliJ patched JFlex (see the link below), Default is 1.7.0-1
    jflexRelease.set(properties("jflexRelease"))

    // Release version, tag, or short commit hash of Grammar-Kit to use (see link below). Default is 2021.1.2
    grammarKitRelease.set(properties("grammarKitRelease"))

    // Optionally provide an IntelliJ version to build the classpath for GenerateParser/GenerateLexer tasks
    intellijRelease.set(properties("grammarKitIntelliJRelease"))
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
    cachePath.set(provider { file(".qodana").canonicalPath })
    reportPath.set(provider { file("build/reports/inspections").canonicalPath })
    saveReport.set(true)
    showReport.set(environment("QODANA_SHOW_REPORT").map { it.toBoolean() }.getOrElse(false))
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover.xmlReport {
    onCheck.set(true)
}

val generateLatteParser = task("generateLatteParser", GenerateParserTask::class) {
    source.set("src/main/java/com/jantvrdik/intellij/latte/parser/LatteParser.bnf")
    targetRoot.set("src/main/gen")
    pathToParser.set("/com/jantvrdik/intellij/latte/parser/LatteParser.java")
    pathToPsiRoot.set("/com/jantvrdik/intellij/latte/psi")
    purgeOldFiles.set(true)
}

val generateLatteMacroContentLexer = task<GenerateLexerTask>("generateLatteMacroContentLexer") {
    source.set("src/main/java/com/jantvrdik/intellij/latte/lexer/grammars/LatteMacroContentLexer.flex")
    targetDir.set("src/main/gen/com/jantvrdik/intellij/latte/lexer/")
    targetClass.set("LatteMacroContentLexer")
    purgeOldFiles.set(true)
}

val generateLatteMacroLexer = task<GenerateLexerTask>("generateLatteMacroLexer") {
    source.set("src/main/java/com/jantvrdik/intellij/latte/lexer/grammars/LatteMacroLexer.flex")
    targetDir.set("src/main/gen/com/jantvrdik/intellij/latte/lexer/")
    targetClass.set("LatteMacroLexer")
    purgeOldFiles.set(true)
}

val generateLatteTopLexer = task<GenerateLexerTask>("generateLatteTopLexer") {
    source.set("src/main/java/com/jantvrdik/intellij/latte/lexer/grammars/LatteTopLexer.flex")
    targetDir.set("src/main/gen/com/jantvrdik/intellij/latte/lexer/")
    targetClass.set("LatteTopLexer")
    purgeOldFiles.set(true)
}

tasks {
    // Set the JVM compatibility versions
    /*properties("javaVersion").get().let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }

    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=17")
    }*/

    withType<KotlinCompile> {
        dependsOn(generateLatteMacroContentLexer, generateLatteMacroLexer, generateLatteTopLexer, generateLatteParser)
    }

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        })

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes.set(properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        })
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

    signPlugin {
        certificateChain.set(environment("CERTIFICATE_CHAIN"))
        privateKey.set(environment("PRIVATE_KEY"))
        password.set(environment("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(environment("PUBLISH_TOKEN"))
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(properties("pluginVersion").map { listOf(it.split('-').getOrElse(1) { "default" }.split('.').first()) })
    }
}