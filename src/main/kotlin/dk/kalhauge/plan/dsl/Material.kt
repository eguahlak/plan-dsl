package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.dsl.Target

interface Targeting {
  val hasResource: Boolean
  val target: Target?
  }

class Material(
    val lecture: Lecture,
    override val target: Target,
    val category: Category,
    var toFront: Boolean,
    var active: Boolean
    ) : Targeting {
  enum class Category { PRESENTATION, EXERCISE, REPOSITORY, LOCAL, EXTERNAL }

  override val hasResource get() = target !is TargetProxy
  }

fun Lecture.presentation(
    address: Address,
    title: String? = null,
    label: String? = null,
    name: String? = null,
    build: Material.() -> Unit = {}
    ) =
  Material(
      this,
      cached(address, title, label, name),
      Material.Category.PRESENTATION,
      toFront = true,
      active = true
      )
    .also {
    it.build()
    add(it)
    }

fun Lecture.exercise(
    address: Address,
    title: String? = null,
    label: String? = null,
    name: String? = null,
    build: Material.() -> Unit = {}
  ) =
    Material(this,
      cached(address, title, label, name),
      Material.Category.EXERCISE,
      toFront = false,
      active = true
  ).also {
  it.build()
  add(it)
  }

fun Lecture.repository(
    address: Address.Web,
    title: String? = null,
    label: String? = null,
    build: Material.() -> Unit = {}
  ) =
    Material(this,
      website(address, title, label),
      Material.Category.REPOSITORY,
      toFront = true,
      active = true
  ).also {
  it.build()
  add(it)
  }

fun Lecture.repository(
    label: String,
    build: Material.() -> Unit = {}
  ) =
    Material(this,
      TargetProxy(null, label),
      Material.Category.REPOSITORY,
      toFront = true,
      active = true
  ).also {
  it.build()
  add(it)
  }

fun Lecture.material(
    address: Address,
    title: String? = null,
    label: String? = null,
    build: Material.() -> Unit = {}
  ) =
    Material(this,
      cached(address, title, label),
      Material.Category.LOCAL,
      toFront = false,
      active = true
  ).also {
  it.build()
  add(it)
  }

fun Lecture.externalLink(
    address: Address.Web,
    title: String? = null,
    label: String? = null,
    build: Material.() -> Unit = {}
  ) =
    Material(this,
      website(address, title, label),
      Material.Category.EXTERNAL,
      toFront = false,
      active = true
  ).also {
  it.build()
  add(it)
  }

fun Lecture.externalLink(
    label: String,
    build: Material.() -> Unit = {}
  ) =
    Material(this,
      TargetProxy(null, label),
      Material.Category.EXTERNAL,
      toFront = false,
      active = true
  ).also {
  it.build()
  add(it)
  }

