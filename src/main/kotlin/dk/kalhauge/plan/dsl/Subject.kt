package dk.kalhauge.plan.dsl

import dk.kalhauge.calendar.dsl.labelized
import dk.kalhauge.document.dsl.anonymousSection

abstract class SubjectList() {
  val subjects = mutableListOf<Subject>()

  fun tool(title: String, build: Subject.() -> Unit = {}) =
    Subject(title, Subject.Category.TOOL).also {
      subjects.add(it)
      it.build()
      }

  fun concept(title: String, build: Subject.() -> Unit = {}) =
    Subject(title, Subject.Category.CONCEPT).also {
      subjects.add(it)
      it.build()
      }

  fun theory(title: String, build: Subject.() -> Unit = {}) =
    Subject(title, Subject.Category.THEORY).also {
      subjects.add(it)
      it.build()
      }

  fun practice(title: String, build: Subject.() -> Unit = {}) =
    Subject(title, Subject.Category.PRACTICE).also {
      subjects.add(it)
      it.build()
      }

  fun recap(title: String, build: Subject.() -> Unit = {}) =
    Subject(title, Subject.Category.RECAP).also {
      subjects.add(it)
      it.build()
      }

  }

object EmptySubjectList : SubjectList()

class Subject(val title: String, val category: Category) {
  enum class Category { TOOL, CONCEPT, THEORY, PRACTICE, RECAP }

  val label: String by lazy { title.labelized() }
  val description = anonymousSection()

  val prerequisites = mutableListOf<Prerequisite>()

  infix fun uses(subject: Subject) {
    prerequisites.add(Prerequisite(subject, Prerequisite.Category.USES))
    }

  infix fun uses(subjects: Iterable<Subject>) {
    prerequisites.addAll(subjects.map { Prerequisite(it, Prerequisite.Category.USES) })
    }

  infix fun dependsOn(subject: Subject) {
    prerequisites.add(Prerequisite(subject, Prerequisite.Category.DEPENDS_ON))
    }

  infix fun dependsOn(subjects: Iterable<Subject>) {
    prerequisites.addAll(subjects.map { Prerequisite(it, Prerequisite.Category.DEPENDS_ON) })
    }

  infix fun implements(subject: Subject) {
    prerequisites.add(Prerequisite(subject, Prerequisite.Category.IMPLEMENTS))
    }

  infix fun implements(subjects: Iterable<Subject>) {
    prerequisites.addAll(subjects.map { Prerequisite(it, Prerequisite.Category.IMPLEMENTS) })
    }

  class Prerequisite(val subject: Subject, val category: Category = Category.USES) {
    enum class Category { USES, DEPENDS_ON, IMPLEMENTS }
    }

  }

