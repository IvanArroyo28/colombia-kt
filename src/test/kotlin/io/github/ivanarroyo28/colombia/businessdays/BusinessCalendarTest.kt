package io.github.ivanarroyo28.colombia.businessdays

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BusinessCalendarTest {

    private val calendar = BusinessCalendar.MONDAY_TO_FRIDAY
    private fun date(text: String) = LocalDate.parse(text)

    // ---------- isBusinessDay ----------

    @Test
    fun `a holiday monday is not a business day`() {
        assertFalse(calendar.isBusinessDay(date("2026-11-16"))) // Independencia de Cartagena
        assertTrue(calendar.isBusinessDay(date("2026-11-17")))
    }

    @Test
    fun `weekends depend on the calendar`() {
        assertFalse(calendar.isBusinessDay(date("2026-11-21")))                              // Saturday
        assertTrue(BusinessCalendar.MONDAY_TO_SATURDAY.isBusinessDay(date("2026-11-21")))
        assertFalse(BusinessCalendar.MONDAY_TO_SATURDAY.isBusinessDay(date("2026-11-22")))  // Sunday
    }

    // ---------- addBusinessDays ----------

    @Test
    fun `adding one business day skips the weekend and a holiday`() {
        // Friday Nov 13 → (Sat, Sun, holiday Monday) → Tuesday Nov 17
        assertEquals(date("2026-11-17"), calendar.addBusinessDays(date("2026-11-13"), 1))
    }

    @Test
    fun `a 10 business day deadline over christmas and new year`() {
        assertEquals(date("2027-01-05"), calendar.addBusinessDays(date("2026-12-18"), 10))
    }

    @Test
    fun `adding business days around christmas`() {
        assertEquals(date("2026-12-29"), calendar.addBusinessDays(date("2026-12-23"), 3))
    }

    @Test
    fun `adding zero returns the same date`() {
        assertEquals(date("2026-11-21"), calendar.addBusinessDays(date("2026-11-21"), 0))
    }

    @Test
    fun `negative days go backwards`() {
        assertEquals(date("2026-11-13"), calendar.addBusinessDays(date("2026-11-17"), -1))
        assertEquals(date("2026-12-31"), calendar.previousBusinessDay(date("2027-01-04")))
    }

    @Test
    fun `saturday calendar counts saturdays`() {
        assertEquals(date("2026-11-14"), BusinessCalendar.MONDAY_TO_SATURDAY.addBusinessDays(date("2026-11-13"), 1))
    }

    @Test
    fun `company days off are skipped too`() {
        val company = BusinessCalendar(extraDaysOff = setOf(date("2026-12-24"), date("2026-12-31")))
        // Dec 24 closed, Dec 25 holiday, weekend → Monday Dec 28
        assertEquals(date("2026-12-28"), company.addBusinessDays(date("2026-12-23"), 1))
    }

    @Test
    fun `changing the original set later does not change the calendar`() {
        val daysOff = mutableSetOf(date("2026-12-24"))
        val company = BusinessCalendar(extraDaysOff = daysOff)
        daysOff.clear()
        assertFalse(company.isBusinessDay(date("2026-12-24")))
    }

    // ---------- rolling to a business day ----------

    @Test
    fun `next or same business day`() {
        assertEquals(date("2026-11-17"), calendar.nextOrSameBusinessDay(date("2026-11-16")))
        assertEquals(date("2026-11-17"), calendar.nextOrSameBusinessDay(date("2026-11-17")))
        assertEquals(date("2026-11-13"), calendar.previousOrSameBusinessDay(date("2026-11-15")))
    }

    // ---------- counting ----------

    @Test
    fun `november 2026 has 19 business days`() {
        assertEquals(19, calendar.businessDaysBetween(date("2026-11-01"), date("2026-12-01")))
        assertEquals(19, calendar.businessDaysIn(YearMonth.of(2026, 11)).size)
    }

    @Test
    fun `december 2026 has 21 business days`() {
        assertEquals(21, calendar.businessDaysIn(YearMonth.of(2026, 12)).size)
    }

    @Test
    fun `counting backwards gives a negative number`() {
        assertEquals(-19, calendar.businessDaysBetween(date("2026-12-01"), date("2026-11-01")))
        assertEquals(0, calendar.businessDaysBetween(date("2026-11-17"), date("2026-11-17")))
    }

    @Test
    fun `add and count agree`() {
        val start = date("2026-12-18")
        val end = calendar.addBusinessDays(start, 10)
        // Count from the day after start up to and including end.
        assertEquals(10, calendar.businessDaysBetween(start.plusDays(1), end.plusDays(1)))
    }

    // ---------- extensions and validation ----------

    @Test
    fun `kotlin extensions`() {
        assertFalse(date("2026-12-25").isColombianBusinessDay())
        assertEquals(date("2026-11-17"), date("2026-11-13").plusBusinessDays(1))
    }

    @Test
    fun `a calendar without working days is rejected`() {
        assertFailsWith<IllegalArgumentException> { BusinessCalendar(workingDays = emptySet<DayOfWeek>()) }
    }
}
