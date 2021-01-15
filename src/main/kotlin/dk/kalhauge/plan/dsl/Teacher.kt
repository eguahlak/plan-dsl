package dk.kalhauge.plan.dsl

class Teacher(val initials: String, val name: String, val phone: String?) {
  override fun toString() = "$name ($initials)"
  }


