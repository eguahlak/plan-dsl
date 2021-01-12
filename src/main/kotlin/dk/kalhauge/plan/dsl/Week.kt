package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.anonymousSection
import dk.kalhauge.plan.dsl.engine.joinEnglish

class Week(val flow: Flow, val number: Int, title: String) {
  companion object {
    var documentName = "README"
    }
  var active: Boolean = true
      get() = field && flow.active
      set(value) { field = value }
  var overview = anonymousSection()
  val lectures = mutableListOf<Lecture>()
  val course get() = flow.course
  val code get() = if (number < 10) "0$number" else "$number"
  val index: Int
  val previous get() = course.getWeek(index - 1)
  val next get() = course.getWeek(index + 1)
  var title: String = title
    get() = "Week $number - "+when {
      field.isNotBlank() -> field
      lectures.size > 0 -> lectures.map { it.title }.joinEnglish()
      else -> ""
      }

  init {
    index = flow.add(this)
    }

  fun add(lecture: Lecture) { lectures += lecture }

  }

fun Flow.week(number: Int, title: String = "", build: Week.() -> Unit = {}) =
    Week(this, number, title).also(build)