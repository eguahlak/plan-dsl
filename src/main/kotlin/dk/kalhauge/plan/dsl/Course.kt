package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.*
import dk.kalhauge.util.anchorize

/*
class ParagraphProperty {
  var paragraph: Paragraph? = null
  operator fun invoke(build: Paragraph.() -> Unit) {
    paragraph = Paragraph().also(build)
    }
  operator fun plusAssign(content: String) {
    plusAssign(text(content))
    }
  operator fun plusAssign(text: Text) {
    if (paragraph == null) paragraph = Paragraph()
    paragraph?.add(text)
    }
  }

class SectionProperty(val title: String) {
  val section = Section(title, title.anchorize())
  operator fun invoke(build: Section.() -> Unit) {
    section.build()
    }
  operator fun plusAssign(content: String) {
    plusAssign(text(content))
    }
  operator fun plusAssign(text: Text) {
    section.add(Paragraph().apply { add(text) })
    }
  }
*/

class Course(val title: String, val semester: Semester, val root: String) {
  val teachers = mutableSetOf<Teacher>()
  var location: Location = Somewhere
  val schedule = mutableListOf<TimeSlot>()

  val overview = Paragraph()

  var plan = Paragraph()
  val flows = mutableListOf<Flow>()

  val materials = mutableListOf<Material>()

  var objective = Paragraph().also { it { text("At the end of the course the student will") } }
  val objectives = mutableMapOf<String, Objective>()

  var creditable = Paragraph()
  val creditables = mutableListOf<Creditable>()

  var nextLectureNumber = 0
  val weeks = mutableListOf<Week>()
  val lectures get() = weeks.flatMap { it.lectures }

  val exam = Section("Exam")

  var curriculum: Curriculum? = null


  fun add(timeSlot: TimeSlot) { schedule += timeSlot }
  fun add(flow: Flow) { flows += flow }
  fun add(week: Week): Int {
    weeks += week
    return weeks.size - 1
    }
  fun getWeek(index: Int) = if (index < 0 || weeks.size <= index ) null else weeks[index]
  fun add(objective: Objective) {
    objective.key?.let { objectives[it] = objective }
    }
  fun getObjective(key: String) =  objectives[key] ?: OrphanedObjective(key)
  fun add(creditable: Creditable) { creditables += creditable }
  fun add(material: Material) { materials += material }
  fun add(teacher: Teacher) { teachers += teacher }

  }

fun course(
    title: String,
    semester: Semester,
    root: String = "",
    build: Course.() -> Unit = {}
    ) =
  Course(title, semester, root).also(build)

