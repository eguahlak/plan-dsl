package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Paragraph

class Lecture(val week: Week, val title: String) {
  val teachers = mutableListOf<Teacher>()
  var timeSlot: TimeSlot
  val course get() = week.course
  val number =  course.nextLectureNumber++
  val code get() = if (number < 10) "0$number" else "$number"
  var note = " "
  val workLoad: Double get() = activities.map { it.load }.sum() + timeSlot.load
  var overview: Paragraph? = null

  var objective: Paragraph? = null
  val objectives = mutableListOf<Objective>()

  var activity: Paragraph? = null
  val activities = mutableListOf<Activity>()

  val materials = mutableListOf<Material>()

  init {
    timeSlot = course.schedule.getOrElse(week.lectures.size, { TBD })
    week.add(this)
    }

  fun add(objective: Objective) { objectives += objective }
  fun add(activity: Activity) { activities += activity }
  fun add(material: Material) {
    materials += material
    if (material.toFront) course.add(material)
    }
  fun add(teacher: Teacher) {
    teachers += teacher
    course.add(teacher)
    }

  fun overview(build: Paragraph.() -> Unit = {}) {
    overview = Paragraph().also(build)
    }

  fun objective(build: Paragraph.() -> Unit = {}) {
    objective = Paragraph().also(build)
    }

  fun activity(build: Paragraph.() -> Unit = {}) {
    activity = Paragraph().also(build)
    }

  data class Load(
      var presence: Double = 0.0,
      var read: Double = 0.0,
      var write: Double = 0.0,
      var work: Double = 0.0
      ) {
    fun added(other: Load): Load {
      presence += other.presence
      read += other.read
      write += other.write
      work += other.work
      return this
      }
    }

  fun loads(): Load {
    val load = Load(presence = timeSlot.load);
    activities.forEach {
      when (it.type) {
        ActivityType.READ -> load.read += it.load
        ActivityType.WRITE -> load.write += it.load
        ActivityType.WORK -> load.work += it.load
        }
      }
    return load
    }
  }

fun Week.lecture(title: String, build: Lecture.() -> Unit = {}) =
    Lecture(this, title).also(build)