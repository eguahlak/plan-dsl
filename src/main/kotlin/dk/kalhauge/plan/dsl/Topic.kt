package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.anonymousSection
import dk.kalhauge.util.id

abstract class TopicList() {
  val topics = mutableListOf<Topic>()

  fun tool(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Category.TOOL).also {
      topics.add(it)
      it.build()
      }

  fun concept(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Category.CONCEPT).also {
      topics.add(it)
      it.build()
      }

  fun theory(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Category.THEORY).also {
      topics.add(it)
      it.build()
      }

  fun practice(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Category.PRACTICE).also {
      topics.add(it)
      it.build()
      }

  fun recap(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Category.RECAP).also {
      topics.add(it)
      it.build()
      }

  }

object EmptyTopicList : TopicList()

class Topic(val title: String, val category: Category) {
  enum class Category { TOOL, CONCEPT, THEORY, PRACTICE, RECAP }

  val label: String by lazy { title.id() }
  val description = anonymousSection()

  val prerequisites = mutableListOf<Prerequisite>()

  infix fun uses(topic: Topic) {
    prerequisites.add(Prerequisite(topic, Prerequisite.Category.USES))
    }

  infix fun uses(topics: Iterable<Topic>) {
    prerequisites.addAll(topics.map { Prerequisite(it, Prerequisite.Category.USES) })
    }

  infix fun dependsOn(topic: Topic) {
    prerequisites.add(Prerequisite(topic, Prerequisite.Category.DEPENDS_ON))
    }

  infix fun dependsOn(topics: Iterable<Topic>) {
    prerequisites.addAll(topics.map { Prerequisite(it, Prerequisite.Category.DEPENDS_ON) })
    }

  infix fun implements(topic: Topic) {
    prerequisites.add(Prerequisite(topic, Prerequisite.Category.IMPLEMENTS))
    }

  infix fun implements(topics: Iterable<Topic>) {
    prerequisites.addAll(topics.map { Prerequisite(it, Prerequisite.Category.IMPLEMENTS) })
    }

  class Prerequisite(val topic: Topic, val category: Category = Category.USES) {
    enum class Category { USES, DEPENDS_ON, IMPLEMENTS }
    }

  }

