package dk.kalhauge.plan.dsl

class Teacher(val initials: String, val name: String, val phone: String?)

fun Lecture.teachers(vararg teachers: Teacher) {
  teachers.forEach { add(it) }
  }

