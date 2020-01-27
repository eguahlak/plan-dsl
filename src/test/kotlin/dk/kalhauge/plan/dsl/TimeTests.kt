package dk.kalhauge.plan.dsl

import org.junit.Assert.*
import org.junit.Test

class TimeTests {
  val delta = 0.0001

  @Test
  fun testTimeOfDayToDouble() {
    val tod = TimeOfDay(620)
    assertEquals(10.3333333, tod.toHours(), delta)
    }

  @Test
  fun testDoubleToTimeOfDay() {
    val h = 10.0 + 1.0/3
    assertEquals("10:20", h.toTimeOfDay().toString())
    }

  @Test
  fun testTimeConstants() {
    assertEquals("08:30", TimeSlot.T0830.toString())
    assertEquals("12:00", TimeSlot.T1200.toString())
    assertEquals("12:30", TimeSlot.T1230.toString())
    assertEquals("14:00", TimeSlot.T1400.toString())
    assertEquals("16:00", TimeSlot.T1600.toString())
    }

  @Test
  fun testLoadOfTimeSlots() {
    assertEquals(3.5, TimeSlot(WeekDay.MONDAY, morning).load, delta)
    assertEquals(3.5, TimeSlot(WeekDay.TUESDAY, afternoon).load, delta)
    assertEquals("Lunch time is subtracted", 5.0, TimeSlot(WeekDay.WEDNESDAY, workshop).load, delta)
    assertEquals("Load is overridden", 4.0, TimeSlot(WeekDay.THURSDAY, workshop, 4.0).load, delta)
    }

  }