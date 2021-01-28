package dk.kalhauge.plan.dsl

import dk.kalhauge.document.dsl.Address
import dk.kalhauge.util.of
import java.time.LocalDateTime
import java.time.ZoneId
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
  val weekFields = WeekFields.of(Locale.GERMANY)
  return LocalDateTime.now()
    .withYear(year)
    .with(weekFields.weekOfYear(), week.toLong())
    .with(weekFields.dayOfWeek(), (weekDay.ordinal%7 + 1).toLong())
    .withHour(time.hour)
    .withMinute(time.minute)
    .withSecond(0)
    .withNano(0)
  }

class Grid<R, C, V>(vararg keys: C) {
  var allowDublicates = false
  val rows: SortedMap<R, MutableMap<C, MutableList<V>>> = TreeMap<R, MutableMap<C, MutableList<V>>>()
  val columnKeys = mutableListOf<C>(*keys)
  operator fun set(rowKey: R, columnKey: C, value: V) {
    val columns = rows[rowKey] ?: mutableMapOf<C, MutableList<V>>().also {
      rows[rowKey] = it
      }
    val values = columns[columnKey] ?: mutableListOf<V>().also {
      if (!columnKeys.contains(columnKey)) columnKeys.add(columnKey)
      columns[columnKey] = it
      }
    if (allowDublicates || !values.contains(value)) values.add(value)
    }

  operator fun get(rowKey: R, columnKey: C): List<V> {
    val columns = rows[rowKey] ?: return emptyList()
    return columns[columnKey] ?: emptyList()
    }
  }

/*
fun main() {
  println("${emptyList<String>()}")
  val table = Table<Int, String, Double>("MA", "TI", "ON", "TO", "FR")
  table[37, "MA"] = 7.0
  table[37, "TI"] = 7.1
  table[37, "TI"] = 8.0
  table[38, "MA"] = 2.0
  table[39, "LØ"] = 10.0
  table.rows.toSortedMap().forEach { rowKey, colums ->
    print("$rowKey => ")
    table.columnKeys.forEach { print("$it:${table[rowKey, it]} ") }
    println()
    }
  }
*/