package docs

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.handler.FileHost
import dk.kalhauge.document.handler.GfmHandler
import dk.kalhauge.plan.dsl.engine.docs

fun main() {
  val VERSION = "2.1.20"
  val docs = docs("plan-dsl", "Course description specific language") {
    document("README") {
      paragraph("""
        Plan-dsl is written in Kotlin.
        Plan-dsl is ment to deal with various documentation issues on
        Cphbusiness, and to create consistent sites for students using GitHub Pages.
        """.trimIndent())
      paragraph("You need the following in your `build.gradle` file of your project:")
      code("""
        repositories {
          // ...
          maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/eguahlak/plan-dsl")
            credentials {
              username = System.getenv("GPR_USER") ?: GITHUB_USER
              password = System.getenv("GPR_API_KEY") ?: GITHUB_PACKAGES
              }
            }
          }

        dependencies {
          implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
          implementation "dk.kalhauge:plan-dsl:$VERSION"
          // ...
          }

        """.trimIndent(), "groovy")
      paragraph("""
        and you have to modify or create a `~~//.gradle//gradle.properties` file
        with the following entries:
        """.trimIndent())
      code("""
        GITHUB_USER=eguahlak
        GITHUB_PACKAGES=a011e...
        """.trimIndent())
      paragraph("""
        Until GitHub removes authentication on packages from public repositories
        you need to get the `GITHUB__PACKAGES value from me.`""".trimIndent())

      section("Setup") {
        list {
          paragraph("Create a GitHub repository eg.: `soft2020spring` and clone it to your computer")
          paragraph("Create a `docs` folder for the GitHub pages")
          paragraph("Create a Kotlin VM project in a `dsl` folder for the Kotlin project")
          paragraph("Create a `.gitignore` file in the `dsl` folder")
          code("""
            .idea/
            out/
            build/
            """.trimIndent())
          paragraph("Add, commit, and push the content")
          paragraph("Enable GitHub Pages from `docs`")
          }
        }
      }

    }
  val conf = Configuration("/Users/AKA/Development/Kotlin/plan-dsl")
  GfmHandler(FileHost(conf), docs).handle(true)
  }