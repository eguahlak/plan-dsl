package dk.kalhauge.plan.dsl

enum class Term { SPRING, FALL }
enum class WeekDay {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, TBD
  }


class Semester(val year: Int, val term: Term) {
  companion object {
    val springSemesters = mutableMapOf<Int, Semester>()
    val fallSemesters = mutableMapOf<Int, Semester>()
    }
  val courses = mutableListOf<Course>()
  val holidays = mutableListOf<Holiday>()

  init {
    when (term) {
      Term.SPRING -> springSemesters[year] = this
      Term.FALL -> fallSemesters[year] = this
      }
    }
  override fun toString() = when (term) {
    Term.FALL -> "${year} fall"
    Term.SPRING -> "${year} spring"
    }
  }

fun spring(year: Int, build: Semester.() -> Unit = {}) =
    (Semester.springSemesters[year] ?: Semester(year, Term.SPRING)).apply(build)

fun fall(year: Int, build: Semester.() -> Unit = {}) =
    (Semester.fallSemesters[year] ?: Semester(year, Term.FALL)).apply(build)

interface Scheduleable {
  //fun equals(other: Scheduleable): Boolean
  }

class Holiday(val title: String, val name: String, val weekNumber: Int, val weekDay: WeekDay) : Scheduleable {
  override fun equals(other: Any?) =
      if (other !is Holiday) false
      else this.name == other.name && this.weekNumber == other.weekNumber && this.weekDay == other.weekDay
  }

fun Semester.holiday(title: String, name: String, weekNumber: Int, weekDay: WeekDay) {
  holidays += Holiday(title, name, weekNumber, weekDay)
  }

class TimeOfDay(val minutes: Int) : Comparable<TimeOfDay> {
  override fun toString() = "${(minutes/60).zeroize()}:${(minutes%60).zeroize()}"
  fun toHours(): Double = minutes/60 + (minutes%60)/60.0
  override fun compareTo(other: TimeOfDay) = this.minutes.compareTo(other.minutes)
  override fun equals(other: Any?) =
    other != null && other is TimeOfDay && this.minutes == other.minutes
  val hour get() = minutes/60
  val minute get() = minutes%60
  }

fun String.toTimeOfDay(): TimeOfDay {
  val parts = this.split(":").map { it.toInt() }
  return TimeOfDay(parts[0]*60 + parts[1])
  }

fun Double.toTimeOfDay() = TimeOfDay((60*this).toInt())

class TimeSlot(
    val weekDay: WeekDay,
    val start: TimeOfDay,
    val end: TimeOfDay,
    val location: Location,
    load: Double? = null
    ) : Comparable<TimeSlot> {
  constructor(weekDay: WeekDay, interval: Pair<String, String>, location: Location, load: Double? = null) :
      this(weekDay, interval.first.toTimeOfDay(), interval.second.toTimeOfDay(), location, load)
  companion object {
    val T0830 = TimeOfDay(8*60 + 30) //"08:30".toTimeOfDay()
    val T1200 = TimeOfDay(12*60) //"12:00".toTimeOfDay()
    val T1230 = TimeOfDay(12*60 + 30) //"12:30".toTimeOfDay()
    val T1400 = TimeOfDay(14*60) //"14:00".toTimeOfDay()
    val T1600 = TimeOfDay(16*60) //"16:00".toTimeOfDay()
    }
  val startText get() = start.toString()
  val endText get() = end.toString()
  val timeText get() = if (weekDay == WeekDay.TBD) "/To be decided/" else "$startText-$endText"
  val load = load ?: end.toHours() - start.toHours() - if (start < T1200 && T1230 < end) 0.5 else 0.0
  val dayText get() = when (weekDay) {
    WeekDay.MONDAY ->    "MON"
    WeekDay.TUESDAY ->   "TUE"
    WeekDay.WEDNESDAY -> "WED"
    WeekDay.THURSDAY ->  "THU"
    WeekDay.FRIDAY ->    "FRI"
    WeekDay.SATURDAY ->  "/SAT/"
    WeekDay.SUNDAY ->    "/SUN/"
    WeekDay.TBD ->       "/TBD/"
    }

  override fun toString() = "*Time*: $dayText $timeText *Location*: $location"
  override fun compareTo(other: TimeSlot) =
    when (this.weekDay.compareTo(other.weekDay)) {
      -1 -> -1
      0 -> this.start.compareTo(other.start)
      1 -> 1
      else -> 0
      }
  }

val TBD = TimeSlot(WeekDay.TBD, "00:00" to "00:00", Somewhere, 3.5)

val morning = "08:30" to "12:00"
val afternoon = "12:30" to "16:00"
val workshop = "08:30" to "14:00"

fun Course.monday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.MONDAY, interval, location)
fun Course.tuesday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.TUESDAY, interval, location)
fun Course.wednesday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.WEDNESDAY, interval, location)
fun Course.thursday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.THURSDAY, interval, location)
fun Course.friday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.FRIDAY, interval, location)

fun Course.time(weekDay: WeekDay, interval: Pair<String,String>, location: Location? = null) =
  TimeSlot(weekDay, interval, location ?: this.location).also { add(it) }

fun Lecture.monday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.MONDAY, interval, location)
fun Lecture.tuesday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.TUESDAY, interval, location)
fun Lecture.wednesday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.WEDNESDAY, interval, location)
fun Lecture.thursday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.THURSDAY, interval, location)
fun Lecture.friday(interval: Pair<String,String>, location: Location? = null) =
    time(WeekDay.FRIDAY, interval, location)

fun Lecture.time(weekDay: WeekDay, interval: Pair<String,String>, location: Location? = null) =
  TimeSlot(weekDay, interval, location ?: course.location).also { timeSlot = it }



