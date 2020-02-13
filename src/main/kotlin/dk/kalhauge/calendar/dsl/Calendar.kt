package dk.kalhauge.calendar.dsl

import dk.kalhauge.document.handler.Host
import dk.kalhauge.util.toMD5
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Host.printCalendarLine(line: String) {
  val cline = line.chuncked(70).joinToString(separator = "\r\n ", postfix = "\r\n") { it }
  print(cline)
  }

fun StringBuilder.appendCalendarLine(line: String) {
  val cline = line.chuncked(70).joinToString(separator = "\r\n ", postfix = "\r\n") { it }
  append(cline)
  }


class Calendar(
    val productId: String,
    val version: String
    ) {
  val events = mutableListOf<Event>()
  fun add(event: Event) { events += event }
  fun appendTo(builder: StringBuilder) {
    builder.appendCalendarLine("BEGIN:VCALENDAR")
    builder.appendCalendarLine("PRODID:$productId")
    builder.appendCalendarLine("VERSION:$version")
    events.forEach { it.appendTo(builder) }
    builder.appendCalendarLine("END:VCALENDAR")
    }

  override fun toString(): String {
    val builder = StringBuilder()
    appendTo(builder)
    return builder.toString()
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
  var location: String? = null
  var url: String? = null
  val stamp = LocalDateTime.now()
  fun appendTo(builder: StringBuilder) {
    builder.appendCalendarLine("BEGIN:VEVENT")
    builder.appendCalendarLine("UID:${id.toMD5()}")
    // builder.appendCalendarLine("DTSTAMP:${stamp.toCal()}")
    builder.appendCalendarLine("DTSTART:${start.toCal()}")
    builder.appendCalendarLine("DTEND:${end.toCal()}")
    builder.appendCalendarLine("SUMMARY:$summary")
    if (description != null) {
      builder.appendCalendarLine("DESCRIPTION:$description")
      if (url != null)
        builder.appendCalendarLine("""X-ALT-DESC;FMTTYPE=text/html:<html><body>Go to <a href="$url">$description</a></body></html>""")
      }
    if (location != null)
      builder.appendCalendarLine("LOCATION:$location")
    builder.appendCalendarLine("END:VEVENT")
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

val FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneId.of("Europe/Paris"))

fun LocalDateTime.toCal() = minusHours(1).format(FORMAT)

