package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Address
import dk.kalhauge.util.of
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.*

fun String.last(count: Int) = this.substring(this.length - count)

fun Int.zeroize(count: Int = 2): String {
  val t = this.toString()
  return "${count - t.length of "0"}$t"
  }

val String.web get() = Address.Web(this)
val String.file get() = Address.Disk(this)

fun localDateTime(year: Int, week: Int, weekDay: WeekDay, time: TimeOfDay): LocalDateTime {
  // TODO check default
  val weekFields = WeekFields.of(Locale.GERMAN)
  return LocalDateTime.now()
    .withYear(year)
    .with(weekFields.weekOfYear(), week.toLong())
    .with(weekFields.dayOfWeek(), (weekDay.ordinal%7 + 1).toLong())
    .withHour(time.hour)
    .withMinute(time.minute)
    .withSecond(0)
    .withNano(0)
  }