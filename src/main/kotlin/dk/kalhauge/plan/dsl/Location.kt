package dk.kalhauge.plan.dsl

interface Location {
  val name: String
  }

class Room(override val name: String) : Location {
  override fun toString() = name
  }

object Somewhere : Location {
  override val name = "Somewhere TBD"
  override fun toString() = name
  }

class Place(override val name: String, val address: String) : Location {
  override fun toString() = "$name at /$address/"
  }

