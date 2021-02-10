package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.dsl.Target
import dk.kalhauge.document.dsl.structure.Tree
import dk.kalhauge.plan.dsl.engine.add
import java.util.*

class Course(val title: String, val semester: Semester, val label: String, val onlyInfo: Boolean = false) {
  companion object {
    var documentName = "README"
    var graphName = "TOPICS"
    }
  init {
    semester.courses.add(this)
    }
  val teachers = mutableSetOf<Teacher>()
  var location: Location = Somewhere
  val schedule = mutableListOf<TimeSlot>()
  var calendar: String? = null

  val overview = anonymousSection()
  val topics = mutableListOf<Topic>()

  var plan = section("Plan")
  val flows = mutableListOf<Flow>()

  val materials = mutableListOf<Material>()

  val books = mutableListOf<Book>()

  val targets = mutableMapOf<String, Target>()
  fun register(item: Any?) {
    if (item != null && item is Targeting) {
      if (item.hasResource) {
        item.target?.let {
          targets[it.key] = it
          }
        }
      }
    }
  var objective = paragraph().also { it { text("At the end of the course the student will") } }
  val objectives = mutableMapOf<String, Objective>()

  var creditable = paragraph()
  val creditables = mutableListOf<Creditable>()

  var nextLectureNumber = 0
  val weeks = TreeMap<Int, Week>()
  val lectures get() = weeks.values.flatMap { it.lectures }

  val exam = section("Exam")

  var curriculum: Curriculum? = null

  fun add(vararg topics: Topic) {
    this.topics.addAll(topics)
    }
  fun add(topicList: TopicList) {
    topics.addAll(topicList.topics)
    }

  fun add(timeSlot: TimeSlot) { schedule += timeSlot }
  fun add(flow: Flow) { flows += flow }

  fun add(week: Week): Week {
    val original = weeks[week.number]
    if (original != null) {
      original.expandWith(week)
      return original
      }
    else {
      weeks[week.number] = week
      return week
      }
    }

  fun getWeekBefore(week: Week) = weeks.lowerEntry(week.number)?.value

  fun getWeekAfter(week: Week) = weeks.higherEntry(week.number)?.value

  fun add(objective: Objective) {
    objective.key?.let { objectives[it] = objective }
    }
  fun getObjective(key: String) =  objectives[key] ?: OrphanedObjective(key)
  fun add(creditable: Creditable) {
    register(creditable)
    creditables += creditable
    }
  fun add(material: Material) {
    register(material)
    materials += material
    }
  fun add(book: Book) { books += book }
  fun add(teacher: Teacher) { teachers += teacher }

  }

fun course(
    title: String,
    semester: Semester,
    label: String = "",
    onlyInfo: Boolean = false,
    build: Course.() -> Unit = {}
    ) =
  Course(title, semester, label, onlyInfo).also(build)


fun Tree.Trunk.course(
    title: String,
    semester: Semester,
    label: String = "",
    onlyInfo: Boolean = false,
    build: Course.() -> Unit = {}
    ) =
  Course(title, semester, label, onlyInfo).also {
    it.build()
    add(it)
    }
