package testarea

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.handler.FileHost
import dk.kalhauge.document.handler.GfmHandler
import dk.kalhauge.plan.dsl.*
import dk.kalhauge.plan.dsl.engine.context

val AKA = Teacher("AKA", "Anders Kalhauge", "21724411")
val TDI = Teacher("TDI", "Todorka Stoyanova Dimitrova", null)

val conf = Configuration(hasNumbers = true, hasTitle = true)
val resourcesRoot = conf["resources.root"]
val CL_203 = Room("CL-2.03")

fun Folder.tstCourse() = course("Algorithms and Datastructures", spring(2020), "ALG") {
    // https://www.cphbusiness.dk/media/78341/pba_soft_cba_studieordning_2017.pdf
    location = CL_203
    wednesday(morning)
    overview { +"Dette er oversigten" }
    overview += "Hello there"

    plan { +"The course is divided into four flows. The ..." }

    flow("Sorting") {
      overview { +"Sorting can ..." }
      skills { +"Better programmer and bug detective" }
      week(6) {
        overview { +"This is the introduction week..." }
        lecture("Basic data types") {
          teachers(AKA)
          this.overview { +"Consider..." }

          knows("basic abstract data types")
          have_the_skills_to("implement simple data types")
          is_able_to("choose an algorithm based on complexity") {
            fulfills("A1")
            fulfills("A2")
          }
          objective("K2")

          activity { +"The following activities..." }
          read("Chapter 1", 1.0)
          write("Small assignment", 2.0)
          repository(Address("https://github.com/eguahlak/plan-dsl.git"))
          presentation("$resourcesRoot/sas5.pdf", "Something", label = "SAS")
          exercise("$resourcesRoot/sas5.pdf", name = "ex1.pdf", title = "Exercise 1")
        }
        lecture("Big oh") {
          teachers(AKA, TDI)
          friday("08:30" to "15:00")
          skill("the time complexity of an algorithm") {
            fulfills("S2")
          }
          note = "for /test/"
          assignment("Mini project 1", 20.0, 15.0) {
            target = cached("/Users/AKA/tmp/resources/assignment-1.pdf")
          }
          presentation("/Users/AKA/DatSoftLyngby/soft2019fall/docs/DM/week-38/04-regular-languages.pdf", "Regular Languages")
          exercise("$resourcesRoot/sas5.pdf", "Exercise 2", "exercise2")
        }
        lecture("Graphs")
      }
      week(7) {
        lecture("Insertion sort") {
          teachers(AKA)
          knowledge("simple sorting algorithms") {
            fulfills("A2")
          }
        }
      }
      week(8) {
        lecture("Merge sort") { }
      }
    }

    flow("Searching") {}

    flow("Graphs") {}
    attendance()

    curriculum(10) {
      content {
        +"""
       Formålet med fagelementet er at kvalificere den studerende til
       planlægning og gennemførelse af test.
       Den studerende skal have forståelse for placering og betydning af test
       i metodikker for systemudvikling. 
       Den studerende skal kunne designe og gennemføre systematisk test af større systemer,
       herunder etablering af automatiseret test.
       Endvidere skal den studerende beherske begreber og teknikker
       til design og konstruktion af testbare systemer.
       """.trimIndent()
      }
      knowledge("væsentlige teststrategier og -modeller samt deres rolle i systemudviklingen")
      knowledge("test som en integreret del af et udviklingsprojekt")
      knowledge("forskellige testtyper og deres anvendelse")

      ability("sikre sporbarhed mellem systemkrav og test på alle niveauer")
      ability("anvende såvel blackbox- som whitebox testteknikker")
      ability("anvende forskellige kriterier for testdækningsgrad")
      ability("anvende teknikker til såvel verifikation som validering")
      ability("anvende teknikker og værktøjer til automatisering af test")
      ability("opbygge systemer til styring af test og fejlretningsprocessen i udviklingsprojekter")

      skill("definere, planlægge og gennemføre test i et udviklingsprojekt, der passer til projektets kvalitetskrav")
      skill("planlægge og styre gennemførelse af såvel intern som ekstern test af softwaresystemer")
      skill("designe testbare systemer")
    }

    exam {
      +"""
     30 minutes oral exam, no preparation but questions known in advance.
     A student shall have a minimum of 80 credits to attend the exam.
     """.trimIndent()
    }
    exam += "Hello World!, Killroy was here"
  }


fun main() {
  val docs = folder("docs") {
    tstCourse()
    }
  GfmHandler(FileHost(conf), docs).handle(printRelations = true, printTargets = true)
  }