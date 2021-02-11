package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Text
import dk.kalhauge.document.dsl.anonymousSection
import dk.kalhauge.document.dsl.text
import java.util.*

class Flow(val course: Course, val title: String, val name: String? = null) {
  var active = true
  var overview = anonymousSection()
  var skills: Text? = null
//  private val weeks = mutableListOf<Week>()
  private val weeks = TreeMap<Int, Week>()
  val lectures get() = weeks.values.flatMap { it.lectures }
  var label: String? = null

  val code: String get() = label ?: course.label

  init {
    course.add(this)
    }

  fun skills(build: Text.() -> Unit = {}) {
    skills = text().also(build)
    }

  fun add(week: Week) {
    weeks[week.number] =  course.add(week)
    }

  }

fun Course.flow(title: String, name: String? = null, build: Flow.() -> Unit = {}) =
  Flow(this, title, name).also(build)
