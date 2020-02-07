package dk.kalhauge.calendar.dsl

import dk.kalhauge.document.handler.Host
import dk.kalhauge.util.toMD5
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Host.printCalendarLine(line: String) {
  val cline = line.chuncked(70).joinToString(separator = "\r\n ", postfix = "\r\n") { it }
  print(cline)
  }

class Calendar(
    val productId: String,
    val version: String
    ) {
  val events = mutableListOf<Event>()
  fun add(event: Event) { events += event }
  fun printTo(host: Host) {
    host.printCalendarLine("BEGIN:VCALENDAR")
    host.printCalendarLine("PRODID:$productId")
    host.printCalendarLine("VERSION:$version")
    events.forEach { it.printTo(host) }
    host.printCalendarLine("END:VCALENDAR")
    }
  }

fun calendar(
    productId: String = "-//Cphbusiness//Plan.dsl Calendar//EN",
    version: String = "2.0",
    build: Calendar.() -> Unit
    ) = Calendar(productId, version).also(build)

class Event(
    val calendar: Calendar,
    val id: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    var summary: String
    ) {
  var description: String? = null
  val stamp = LocalDateTime.now()
  fun printTo(host: Host) {
    host.printCalendarLine("BEGIN:VEVENT")
    host.printCalendarLine("UID:${id.toMD5()}")
    host.printCalendarLine("DTSTAMP:${stamp.toCal()}")
    host.printCalendarLine("DTSTART:${start.toCal()}")
    host.printCalendarLine("DTEND:${end.toCal()}")
    host.printCalendarLine("SUMMARY:$summary")
    if (description != null)
      host.printCalendarLine("DESCRIPTION:$description")
    host.printCalendarLine("END:VEVENT")
    }
  }

fun Calendar.event(
    id: String,
    start: LocalDateTime,
    end: LocalDateTime,
    summary: String, build: Event.() -> Unit = {}
    ) = Event(this, id, start, end, summary).also {
  it.build()
  add(it)
  }

val FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

fun LocalDateTime.toCal() = format(FORMAT)


fun main() {
  val day = LocalDateTime.of(2020, 2, 13, 10, 33, 7, 0)
  println(day)
  println(day.toCal())
  }