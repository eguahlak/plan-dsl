package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Text

class Flow(val course: Course, val title: String) {
  var overview = GhostSection()
  var skills: Text? = null
  val weeks = mutableListOf<Week>()
  val lectures get() = weeks.flatMap { it.lectures }

  init {
    course.add(this)
    }

  fun skills(build: Text.() -> Unit = {}) {
    skills = Text().also(build)
    }

  fun add(week: Week): Int {
    weeks += week
    return course.add(week)
    }

  }

fun Course.flow(title: String, build: Flow.() -> Unit = {}) =
  Flow(this, title).also(build)
