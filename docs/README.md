Plan-dsl is written in Kotlin.
Plan-dsl is ment to deal with various documentation issues on
Cphbusiness, and to create consistent sites for students using GitHub Pages.  

   * [Setup](#setup)
     * [The Kotlin program](#the-kotlin-program)
       * [Gradle](#gradle)
       * [Code Skelleton](#code-skelleton)
       * [Flows in separate files](#flows-in-separate-files)
     * [Text Types](#text-types)

# Setup

 * Create a GitHub repository eg.: `soft2020spring` and clone it to your computer
 * Create a `docs` folder for the GitHub pages
 * Create a Kotlin VM project in a `dsl` folder for the Kotlin project
 * Create a `.gitignore` file in the `dsl` folder
```
.idea/
out/
build/
```

 * Add, commit, and push the content
 * Enable GitHub Pages from `docs`

## The Kotlin program

### Gradle

You need the following in your `build.gradle` file of your project:  

```groovy
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
  implementation "dk.kalhauge:plan-dsl:2.1.49"
  // ...
  }
```

and you have to modify or create a `~/.gradle/gradle.properties` file
with the following entries:  

```
GITHUB_USER=eguahlak
GITHUB_PACKAGES=a011e...
```

Until GitHub removes authentication on packages from public repositories
you need to get the `GITHUB__PACKAGES` value from me.  

### Code Skelleton

In a Kotlin file (normally in a package):  

```kotlin
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
        presentation("$filesPath/slides-01.pdf".file, "Optional Title", label = "SLIDES1")
        repository("https://github.com/eguahlak/plan-dsl.git".web)
        exercise("$filesPath/exercise-01.pdf"), name = "ex1.pdf", title = "Exercise 1")
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
```

### Flows in separate files

It might be convenient to put flow definitions in separate files,
otherwise the course file will grow very long.  

In the main file:  

```kotlin
fun Tree.Trunk.tstCourse() = course("Course Title", spring(2020), "CODE") {
  wednesday(morning, CL_103)
  overview += "Abstract for the course..."
  plan += "Short text about the plan, overall idea, etc."
  firstFlow()
  secondFlow()
  // ...
  }
```

And in a separate file per flow:  

```kotlin
fun Course.firstFlow() = flow("The first flow") {
  // ...
  }
```

## Text Types

Text can be input at different document levels,
depending on the attribute.
The hierarchy is as follows:  

 * Document
   * Section
     * Section
     * Paragraph
     * List
     * Code
   * Paragraph
     * Text
   * List
     * Paragraph
     * List
     * Code
   * Code

The `document` will already be created by the course or the week.
  

