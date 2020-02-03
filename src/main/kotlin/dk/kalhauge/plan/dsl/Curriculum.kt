package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.paragraph

class Curriculum(val course: Course, val ects: Int) {
  var content = paragraph()
  val objectives = mapOf<Taxonomy, MutableList<Objective>>(
    Taxonomy.KNOWLEDGE to mutableListOf(),
    Taxonomy.ABILITY to mutableListOf(),
    Taxonomy.SKILL to mutableListOf()
    )
  init {
    course.curriculum = this
    }
  fun add(objective: Objective) {
    objectives[objective.level]?.add(objective)
    }
  fun keyFor(level: Taxonomy) = when (level) {
    Taxonomy.KNOWLEDGE -> "K${(objectives[level]?.size ?: 0) + 1}"
    Taxonomy.ABILITY   -> "A${(objectives[level]?.size ?: 0) + 1}"
    Taxonomy.SKILL     -> "S${(objectives[level]?.size ?: 0) + 1}"
    }
  }

fun Course.curriculum(ects: Int, build: Curriculum.() -> Unit) =
  Curriculum(this, ects).also(build)