package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.*
import dk.kalhauge.document.dsl.Target

class Material(
    val lecture: Lecture,
    val target: Target,
    val category: Category,
    var toFront: Boolean,
    var active: Boolean
    ) {
  enum class Category { PRESENTATION, EXERCISE, REPOSITORY, LOCAL, EXTERNAL }
  init {
    lecture.add(this)
    }
  }

fun Lecture.presentation(
    path: String,
    title: String? = null,
    label: String? = null,
    name: String? = null,
    build: Material.() -> Unit = {}
    ) =
    Material(this,
      cached(path, title, label, name),
      Material.Category.PRESENTATION,
      toFront = true,
      active = true
      ).also(build)

fun Lecture.exercise(
    path: String,
    title: String? = null,
    label: String? = null,
    name: String? = null,
    build: Material.() -> Unit = {}
    ) =
    Material(this,
      cached(path, title, label, name),
      Material.Category.EXERCISE,
      toFront = false,
      active = true
      ).also(build)

fun Lecture.repository(
    address: Address,
    title: String? = null,
    label: String? = null,
    build: Material.() -> Unit = {}
    ) =
    Material(this,
      Resource(address, label, title),
      Material.Category.REPOSITORY,
      toFront = true,
      active = true
      ).also(build)

fun Lecture.repository(
    label: String,
    build: Material.() -> Unit = {}
    ) =
    Material(this,
      TargetProxy(label),
      Material.Category.REPOSITORY,
      toFront = true,
      active = true
      ).also(build)

fun Lecture.material(
    address: Address,
    title: String? = null,
    label: String? = null,
    build: Material.() -> Unit = {}
    ) =
    Material(this,
      Resource(address, label, title),
      Material.Category.LOCAL,
      toFront = false,
      active = true
      ).also(build)

fun Lecture.externalLink(
    address: Address,
    title: String? = null,
    label: String? = null,
    build: Material.() -> Unit = {}
    ) =
  Material(this,
    Resource(address, label, title),
    Material.Category.EXTERNAL,
    toFront = false,
    active = true
    ).also(build)

fun Lecture.externalLink(
    label: String,
    build: Material.() -> Unit = {}
    ) =
  Material(this,
    TargetProxy(label),
    Material.Category.EXTERNAL,
    toFront = false,
    active = true
    ).also(build)
