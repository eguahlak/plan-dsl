package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.anonymousSection
import dk.kalhauge.document.dsl.paragraph
import java.time.LocalDateTime

class Lecture(val week: Week, val title: String) : Scheduleable {
  val teachers = mutableListOf<Teacher>()
  fun teachers(vararg teachers: Teacher) {
    teachers.forEach { add(it) }
    }
  val course get() = week.course

  var isAsScheduled: Boolean = true
    private set

  var timeSlot: TimeSlot = course.schedule.getOrElse(week.lectures.size) { TBD }
    set(value) {
      isAsScheduled = false
      field = value
      }

  val number =  course.nextLectureNumber++
  val code get() = if (number < 10) "0$number" else "$number"
  var note = " "
  val workLoad: Double get() = activities.map { it.load }.sum() + timeSlot.load
  var overview = anonymousSection()
  val subjects = mutableListOf<Topic>()

  var objective = paragraph()
  val objectives = mutableListOf<Objective>()

  var activity = paragraph()
  val activities = mutableListOf<Activity>()

  val content = anonymousSection()

  val materials = mutableListOf<Material>()

  val start: LocalDateTime
    get() = localDateTime(course.semester.year, week.number, timeSlot.weekDay, timeSlot.start)

  val end: LocalDateTime
    get() = localDateTime(course.semester.year, week.number, timeSlot.weekDay, timeSlot.end)

  init {
    week.add(this)
    }

  fun add(vararg topics: Topic) {
    this.subjects.addAll(topics)
    }
  fun add(objective: Objective) { objectives += objective }
  fun add(activity: Activity) {
    course.register(activity)
    activities += activity
    }
  fun add(material: Material) {
    materials += material
    course.register(material)
    if (material.toFront /* && material.hasResource */) course.add(material)
    }
  fun add(teacher: Teacher) {
    teachers += teacher
    course.add(teacher)
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
    val load = Load(presence = timeSlot.load)
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