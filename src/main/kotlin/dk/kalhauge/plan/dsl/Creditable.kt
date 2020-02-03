package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.dsl.Target

interface Creditable {
  val credits: Double
  val title: Text
  }

class Attendance(
    override val credits: Double,
    override val title: Text
    ) : Creditable

fun Course.attendance(credits: Double = 20.0, title: String? = null) =
  Attendance(credits, text(title ?: "Attendance")).also { add(it) }

//fun Lecture.attendance(credits: Double = 20.0, title: String? = null) =
//  Attendance(credits, text(title ?: "Attendance")).also { add(it) }

class Assignment(
    lecture: Lecture,
    title: String,
    load: Double,
    override val credits: Double,
    override var target: Target?
    ) : Activity(lecture, title, ActivityType.WORK, load), Creditable, Targeting {
  override val hasResource get() = target?.let { it !is TargetProxy } ?: false
  }

fun Lecture.assignment(
    title: String,
    load: Double,
    credits: Double = 0.0,
    target: Target? = null,
    build: Assignment.() -> Unit = {}) =
  Assignment(this, title, load, credits, target).also {
    it.build()
    course.add(it)
    }
/*
    val lecture: Lecture,
    val title: String,
    val type: ActivityType,
    val load: Double
*/