package docs

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.handler.FileHost
import dk.kalhauge.document.handler.GfmHandler
import dk.kalhauge.plan.dsl.engine.docs
import dk.kalhauge.plan.dsl.web
import kotlin.text.Typography.dollar

fun main() {
  val VERSION = "2.1.48"
  val docs = docs("plan-dsl", "Course description specific language") {
    document("README") {
      paragraph("""
        Plan-dsl is written in Kotlin.
        Plan-dsl is ment to deal with various documentation issues on
        Cphbusiness, and to create consistent sites for students using GitHub Pages.
        """.trimIndent())
      toc(3)
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
        section("The Kotlin program") {
          section("Gradle") {
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
              you need to get the `GITHUB__PACKAGES` value from me.""".trimIndent())
            }
          section("Code Skelleton") {
            paragraph("""
              In a Kotlin file (normally in a package):
              """.trimIndent())
            kotlin("""
              val AKA = Teacher("AKA", "Anders Kalhauge", "21724411")
              val CL_103 = Room("CL-1.03")
              val filesPath = "/Some path on my machine"
              
              val conf = Configuration(hasNumbers = true, hasTitle = true)
              
              fun Tree.Trunk.tstCourse() = course("Course Title", spring(2020), "CODE") {
                wednesday(morning, CL_103)
                overview += "Abstract for the course..."
                plan += "Short text about the plan, overall idea, etc."
                flow("Title of first flow") {
                  overview += "Short description of the first flow"
                  skills += "Skills earned with business value"
                  week(10) {
                    overview += "Short description of the week, if any"
                    lecture("Lecture title") {
                      teachers(AKA)
                      overview += "Description of the lecture"

                      // Objectives section
                      knows("something the student will know (knowledge)")
                      have_the_skills_to("do something (skils")
                      is_able_to("do something (abilities") {
                        fulfills("A1") // An ability from the curriculum
                        fulfills("A2") // Another ability from the curriculum
                        }
                      objective("K2") // Copies an objective from the curriculum

                      // Activities section
                      read("Chapter 1 in {/TST-BOOK}", 1.0) // /TST-BOOK is defined later
                      write("Small assignment", 2.0)
                      
                      // Materials section
                      presentation("${dollar}filesPath/slides-01.pdf".file, "Optional Title", label = "SLIDES1")
                      repository("https://github.com/eguahlak/plan-dsl.git".web)
                      exercise("${dollar}filesPath/exercise-01.pdf"), name = "ex1.pdf", title = "Exercise 1")
                      externalLink("http://www.kalhauge.dk".web) { toFront = true }
                      externalLink("https://cphbusiness.mrooms.net".web)
                      }
                    lecture("If more than one lecture that week")
                    }
                  week(11) {
                    active = false // if the week should not be published, true is default
                    }
                  week(12) // More weeks in that flow
                  }
                flow("Title of second flow") {
                  active = false // if the flow and all its weeks should not be published
                  overview {
                    paragraph {
                      text("This is the full syntax for all anonymous sections:")
                      list {
                         paragraph("`overview` in course, flow, week, and lecture")
                         paragraph("`content` in lecture")
                         }
                      }
                    }
                  week(13) // ... more weeks
                  }
                attendance(15.0) // attendance gives 15 credits, default is 20
                exam += "Exam info goes here if short, otherwise use:"
                exam {
                  paragraph { /* ... */ }
                  }
                
                // List books if any
                book(
                    title = "Developer Testing",
                    label = "/TST-BOOK",
                    subtitle = "Building Quality into Software",
                    isbn = "978-0-13-429106-2",
                    edition = "1st",
                    editor = "Pearson",
                    url = "https://pearson.com/dev".web,
                    authors = "Jeff Langr"
                    )
  
                curriculum {
                  content += "Description from official document"
                  knowledge("knowledge from official document, repeated, get labels K1, K2, ...")
                  ability("ability from official document, repeated, get label A1, A2, ...")
                  skill("skill from official document, repeated, get label S1, S2, ...")
                  }
                  
              fun main() {
                val docs = docs("Title for jekyll", "Subtitle for jekyll") {
                  tstCourse()
                  }
                GfmHandler(FileHost(conf), docs).handle()
                }
              """.trimIndent())
            }
          section("Flows in separate files") {
            paragraph("""
              It might be convenient to put flow definitions in separate files,
              otherwise the course file will grow very long.""".trimIndent())
            paragraph("In the main file:")
            kotlin("""
              fun Tree.Trunk.tstCourse() = course("Course Title", spring(2020), "CODE") {
                wednesday(morning, CL_103)
                overview += "Abstract for the course..."
                plan += "Short text about the plan, overall idea, etc."
                firstFlow()
                secondFlow()
                // ...
                }
              """.trimIndent())
            paragraph("And in a separate file per flow:")
            kotlin("""
              fun Course.firstFlow() = flow("The first flow") {
                // ...
                }
              """.trimIndent())
            }
          }
        section("Text Types") {
          paragraph("""
            Text can be input at different document levels,
            depending on the attribute.
            The hierarchy is as follows:
            """.trimIndent())
          list {
            paragraph("Document")
            list {
              paragraph("Section")
              list {
                paragraph("Section")
                paragraph("Paragraph")
                paragraph("List")
                paragraph("Code")
                }
              paragraph("Paragraph")
              list {
                paragraph("Text")
                }
              paragraph("List")
              list {
                paragraph("Paragraph")
                paragraph("List")
                paragraph("Code")
                }
              paragraph("Code")
              }
            }
          }
        paragraph("""
          The `document` will already be created by the course or the week.
          
          """.trimIndent())
        }
      }
    }
  docs.root.document("README", "The `plan-dsl` repository") {
    paragraph { website("https://eguahlak.github.io/plan-dsl/".web, "Documentation") }
    }
  val conf = Configuration("/Users/AKA/Development/Kotlin/plan-dsl")
  GfmHandler(FileHost(conf), docs).handle(true)
  }