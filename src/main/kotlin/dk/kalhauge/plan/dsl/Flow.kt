package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Text
import dk.kalhauge.document.dsl.text

class Flow(val course: Course, val title: String) {
  var active = true
  var overview = anonymousSection()
  var skills: Text? = null
  val weeks = mutableListOf<Week>()
  val lectures get() = weeks.flatMap { it.lectures }

  init {
    course.add(this)
    }

  fun skills(build: Text.() -> Unit = {}) {
    skills = text().also(build)
    }

  fun add(week: Week): Int {
    weeks += week
    return course.add(week)
    }

  }

fun Course.flow(title: String, build: Flow.() -> Unit = {}) =
  Flow(this, title).also(build)
