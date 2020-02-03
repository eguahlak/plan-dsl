package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Text
import dk.kalhauge.document.dsl.text

enum class Taxonomy {
  KNOWLEDGE, ABILITY, SKILL
  }

interface Objective {
  val title: Text
  val level: Taxonomy
  val key: String?
  val fromCurriculum: Boolean
  val fulfillments: Set<String>
  }

class LectureObjective(
    val course: Course,
    override var title: Text,
    override val level: Taxonomy,
    override var key: String?
    ) : Objective {
  override val fromCurriculum = false
  override val fulfillments = mutableSetOf<String>()
  fun add(key: String) { fulfillments.add(key) }
  }

class OrphanedObjective(override val key: String) : Objective {
  override val title = text("Key '$key' is orphaned, check spelling!")
  override val level = Taxonomy.KNOWLEDGE
  override val fromCurriculum = false
  override val fulfillments = emptySet<String>()
  }

class ObjectiveReference(val course: Course, override val key: String) : Objective {
  override val title get() = course.getObjective(key).title
  override val level get() = course.getObjective(key).level
  override val fromCurriculum get() = course.getObjective(key).fromCurriculum
  override val fulfillments get() = course.getObjective(key).fulfillments
  }

class CourseObjective(
    val curriculum: Curriculum,
    override var title: Text,
    override val level: Taxonomy,
    override val key: String
    ) : Objective {
  override val fromCurriculum = true
  override val fulfillments = setOf(key)
  }

fun Lecture.objective(
    title: String,
    level: Taxonomy,
    key: String? = null,
    build: LectureObjective.() -> Unit = {}
    ) =
  LectureObjective(course, text(title), level, key).also {
    it.build()
    add(it)
    if (key != null) course.add(it)
    }

fun Lecture.knowledge(title: String, key: String? = null, build: LectureObjective.() -> Unit = {}) =
  objective(title, Taxonomy.KNOWLEDGE, key, build)
fun Lecture.ability(title: String, key: String? = null, build: LectureObjective.() -> Unit = {}) =
  objective(title, Taxonomy.ABILITY, key, build)
fun Lecture.skill(title: String, key: String? = null, build: LectureObjective.() -> Unit = {}) =
  objective(title, Taxonomy.SKILL, key, build)

fun Lecture.knows(title: String, key: String? = null, build: LectureObjective.() -> Unit = {}) =
  objective(title, Taxonomy.KNOWLEDGE, key, build)
fun Lecture.is_able_to(title: String, key: String? = null, build: LectureObjective.() -> Unit = {}) =
  objective(title, Taxonomy.ABILITY, key, build)
fun Lecture.have_the_skills_to(title: String, key: String? = null, build: LectureObjective.() -> Unit = {}) =
  objective(title, Taxonomy.SKILL, key, build)

fun Lecture.objective(key: String) =
  ObjectiveReference(course, key).also { add(it) }

fun LectureObjective.fulfills(key: String) { add(key) }

fun Curriculum.objective(
    title: String,
    level: Taxonomy,
    key: String? = null,
    build: Objective.() -> Unit = {}
    ) =
  CourseObjective(this, text(title), level, key ?: keyFor(level)).also {
    it.build()
    add(it)
    course.add(it)
    }

fun Curriculum.knowledge(title: String) = objective(title, Taxonomy.KNOWLEDGE)
fun Curriculum.ability(title: String) = objective(title, Taxonomy.ABILITY)
fun Curriculum.skill(title: String) = objective(title, Taxonomy.SKILL)
