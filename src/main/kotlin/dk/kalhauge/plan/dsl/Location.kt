package dk.kalhauge.plan.dsl

interface Location {
  val name: String
  }

class Room(override val name: String) : Location

object Somewhere : Location {
  override val name = "Somewhere TBD"
  }

class Place(override val name: String, val address: String) : Location

val CL_103 = Room("CL-1.03")
val CL_203 = Room("CL-2.03")