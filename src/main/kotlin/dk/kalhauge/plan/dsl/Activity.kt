package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.text

enum class ActivityType { READ, WRITE, WORK }

open class Activity(
    val lecture: Lecture,
    title: String,
    val type: ActivityType,
    val load: Double
    ) {
  val title = text(title)
  init {
    lecture.add(this)
    }
  }

fun Lecture.read(title: String, load: Double, build: Activity.() -> Unit = {}) =
    Activity(this, title, ActivityType.READ, load).also(build)

fun Lecture.work(title: String, load: Double, build: Activity.() -> Unit = {}) =
    Activity(this, title, ActivityType.WORK, load).also(build)

fun Lecture.write(title: String, load: Double, build: Activity.() -> Unit = {}) =
    Activity(this, title, ActivityType.WRITE, load).also(build)
