package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.anonymousSection
import dk.kalhauge.util.id

abstract class TopicList() {
  val topics = mutableListOf<Topic>()

  fun tool(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Type.TOOL).also {
      it.build()
      topics.add(it)
      }

  fun concept(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Type.CONCEPT).also {
      it.build()
      topics.add(it)
      }

  fun theory(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Type.THEORY).also {
      it.build()
      topics.add(it)
      }

  fun practice(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Type.PRACTICE).also {
      it.build()
      topics.add(it)
      }

  fun recap(title: String, build: Topic.() -> Unit = {}) =
    Topic(title, Topic.Type.RECAP).also {
      it.build()
      topics.add(it)
      }

  }

class Topic(val title: String, val type: Type) {
  enum class Type { TOOL, CONCEPT, THEORY, PRACTICE, RECAP }

  val label: String by lazy { title.id() }
  val description = anonymousSection()

  val associations = mutableListOf<Association>()

  infix fun uses(topic: Topic) {
    associations.add(Association(topic, Association.Type.USES))
    }

  infix fun uses(topics: Iterable<Topic>) {
    associations.addAll(topics.map { Association(it, Association.Type.USES) })
    }

  infix fun dependsOn(topic: Topic) {
    associations.add(Association(topic, Association.Type.DEPENDS_ON))
    }

  infix fun dependsOn(topics: Iterable<Topic>) {
    associations.addAll(topics.map { Association(it, Association.Type.DEPENDS_ON) })
    }

  infix fun implements(topic: Topic) {
    associations.add(Association(topic, Association.Type.IMPLEMENTS))
    }

  infix fun implements(topics: Iterable<Topic>) {
    associations.addAll(topics.map { Association(it, Association.Type.IMPLEMENTS) })
    }

  class Association(val topic: Topic, val type: Type = Type.USES) {
    enum class Type { USES, DEPENDS_ON, IMPLEMENTS }
    }

  }

