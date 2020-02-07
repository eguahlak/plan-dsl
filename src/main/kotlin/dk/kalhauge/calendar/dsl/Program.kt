package dk.kalhauge.calendar.dsl

import dk.kalhauge.document.dsl.Configuration
import dk.kalhauge.document.handler.FileHost
import java.time.LocalDateTime
import java.time.Month

fun main() {
  val calendar = calendar {
    event(
        "TST/W7/L1",
        LocalDateTime.of(2020, Month.FEBRUARY, 10, 8, 30),
        LocalDateTime.of(2020, Month.FEBRUARY, 10, 14, 0),
        "Test"
        ) {
      description = "Introduction and the V-model"
      }
    event(
        "ALG/W7/L2",
        LocalDateTime.of(2020, Month.FEBRUARY, 10, 8, 30),
        LocalDateTime.of(2020, Month.FEBRUARY, 10, 14, 0),
        "Algoritmer"
        ) {
      description = "Elementary Sorting"
      }
    }
  val conf = Configuration()
  val host = FileHost(conf)
  host.open("docs/calendars/test.ical")
  calendar.printTo(host)
  host.close()
  }