package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.dsl.Target
import dk.kalhauge.document.dsl.structure.Block
import dk.kalhauge.document.dsl.structure.Context
import dk.kalhauge.document.dsl.structure.FreeContext
import dk.kalhauge.document.dsl.structure.Tree
import dk.kalhauge.plan.dsl.engine.add

class GhostSection(context: Context?) : Block.BaseParent() {
  val context = context ?: FreeContext

  override val children = mutableListOf<Block.Child>()

  override val filePath get() = context.filePath
  override val keyPath get() = context.keyPath
  override fun register(target: Target) = context.register(target)
  override fun find(key: String) = context.find(key)

  operator fun invoke(build: GhostSection.() -> Unit) { build() }
  // TODO align with Paragraph.plusAssign
  operator fun plusAssign(text: Text) { paragraph { add(text) } }
  operator fun plusAssign(content: String) { plusAssign(text(content)) }
  }

fun Block.BaseParent.ghostSection() = GhostSection(this)

fun ghostSection() = GhostSection(null)

fun Block.Parent.add(ghostSection: GhostSection) {
  ghostSection.children.forEach { add(it) }
  }

class Course(val title: String, val semester: Semester, val label: String) {
  var documentName = "README"
  val teachers = mutableSetOf<Teacher>()
  var location: Location = Somewhere
  val schedule = mutableListOf<TimeSlot>()

  val overview = ghostSection()

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
  val weeks = mutableListOf<Week>()
  val lectures get() = weeks.flatMap { it.lectures }

  val exam = section("Exam")

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
  fun add(book: Book) { books += book }
  fun add(teacher: Teacher) { teachers += teacher }

  }

fun course(
    title: String,
    semester: Semester,
    label: String = "",
    build: Course.() -> Unit = {}
    ) =
  Course(title, semester, label).also(build)


fun Tree.Trunk.course(
    title: String,
    semester: Semester,
    label: String = "",
    build: Course.() -> Unit = {}
    ) =
  Course(title, semester, label).also {
    it.build()
    add(it)
    }
