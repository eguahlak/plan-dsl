package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.dsl.Target
import dk.kalhauge.plan.dsl.engine.context

class GhostSection : Block.Parent {
  override val children = mutableListOf<Block.Child>()
  override fun add(child: Block.Child?) {
    if (child != null) children += child
    }
  operator fun invoke(build: GhostSection.() -> Unit) { build() }
  // TODO align with Paragraph.plusAssign
  operator fun plusAssign(text: Text) { paragraph { add(text) } }
  operator fun plusAssign(content: String) { plusAssign(text(content)) }
  }

fun Block.Parent.add(ghostSection: GhostSection) {
  ghostSection.children.forEach { add(it) }
  }

class Course(val title: String, val semester: Semester, val label: String, val folder: Folder? = null) {
  val teachers = mutableSetOf<Teacher>()
  var location: Location = Somewhere
  val schedule = mutableListOf<TimeSlot>()

  val overview = GhostSection()

  var plan = Section("Plan")
  val flows = mutableListOf<Flow>()

  val materials = mutableListOf<Material>()

  val targets = mutableMapOf<String, Target>()
  fun register(item: Any?) {
    if (item != null && item is Targeting) {
      if (item.hasResource) {
        item.target?.let {
          targets[it.label] = it
          }
        }
      }
    }

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
  fun add(creditable: Creditable) {
    register(creditable)
    creditables += creditable
    }
  fun add(material: Material) {
    register(material)
    materials += material
    }
  fun add(teacher: Teacher) { teachers += teacher }

  }

fun course(
    title: String,
    semester: Semester,
    label: String = "",
    build: Course.() -> Unit = {}
    ) =
  Course(title, semester, label).also(build)

fun Folder.course(
    title: String,
    semester: Semester,
    label: String = "",
    build: Course.() -> Unit = {}
    ) =
  Course(title, semester, label, this).also {
    it.build()
    it.context
    }
