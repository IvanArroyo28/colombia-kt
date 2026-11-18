package io.github.ivanarroyo28.colombia.holidays

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ColombianHolidaysTest {

    private fun dates(year: Int, vararg monthDays: String): List<LocalDate> =
        monthDays.map { LocalDate.parse("$year-$it") }

    // ---------- Full calendars, checked against the official ones ----------

    @Test
    fun `2026 has 19 holidays including the new one of Chiquinquira`() {
        val expected = dates(
            2026,
            "01-01", "01-12", "03-23", "04-02", "04-03", "05-01", "05-18", "06-08", "06-15", "06-29",
            "07-13", "07-20", "08-07", "08-17", "10-12", "11-02", "11-16", "12-08", "12-25",
        )
        assertEquals(expected, ColombianHolidays.holidaysOf(2026).map { it.date })
    }

    @Test
    fun `2025 has 18 holidays but only 17 days off`() {
        val holidays = ColombianHolidays.holidaysOf(2025)

        assertEquals(18, holidays.size)
        assertEquals(17, ColombianHolidays.daysOffIn(2025).size)
        // San Pedro y San Pablo and Sagrado Corazón both fell on Monday, June 30.
        assertEquals(
            setOf("San Pedro y San Pablo", "Sagrado Corazón de Jesús"),
            ColombianHolidays.holidaysOn(LocalDate.of(2025, 6, 30)).map { it.name }.toSet(),
        )
    }

    @Test
    fun `2024 calendar`() {
        val expected = dates(
            2024,
            "01-01", "01-08", "03-25", "03-28", "03-29", "05-01", "05-13", "06-03", "06-10", "07-01",
            "07-20", "08-07", "08-19", "10-14", "11-04", "11-11", "12-08", "12-25",
        )
        assertEquals(expected, ColombianHolidays.holidaysOf(2024).map { it.date })
    }

    @Test
    fun `2027 calendar`() {
        val expected = dates(
            2027,
            "01-01", "01-11", "03-22", "03-25", "03-26", "05-01", "05-10", "05-31", "06-07", "07-05",
            "07-12", "07-20", "08-07", "08-16", "10-18", "11-01", "11-15", "12-08", "12-25",
        )
        assertEquals(expected, ColombianHolidays.holidaysOf(2027).map { it.date })
    }

    // ---------- Rules ----------

    @Test
    fun `the 9 July holiday only exists from 2026`() {
        val name = "Día de Nuestra Señora del Rosario de Chiquinquirá"
        assertFalse(ColombianHolidays.holidaysOf(2025).any { it.name == name })
        assertTrue(ColombianHolidays.holidaysOf(2026).any { it.name == name })
    }

    @Test
    fun `ley emiliani moves the day off but keeps the celebration date`() {
        val sanJose = ColombianHolidays.holidaysOf(2025).first { it.name == "Día de San José" }

        assertEquals(LocalDate.of(2025, 3, 24), sanJose.date)            // Monday: day off
        assertEquals(LocalDate.of(2025, 3, 19), sanJose.celebrationDate) // Wednesday: celebration
        assertTrue(sanJose.isMoved)
    }

    @Test
    fun `a movable holiday that already falls on monday stays there`() {
        val cartagena = ColombianHolidays.holidaysOf(2024).first { it.name == "Independencia de Cartagena" }

        assertEquals(LocalDate.of(2024, 11, 11), cartagena.date)
        assertFalse(cartagena.isMoved)
    }

    @Test
    fun `fixed holidays are not moved even on weekends`() {
        // July 20, 2024 was a Saturday: no extra Monday off.
        assertTrue(ColombianHolidays.isHoliday(LocalDate.of(2024, 7, 20)))
        assertFalse(ColombianHolidays.isHoliday(LocalDate.of(2024, 7, 22)))
    }

    @Test
    fun `the celebration date of a moved holiday is not a day off`() {
        assertFalse(ColombianHolidays.isHoliday(LocalDate.of(2026, 7, 9)))  // Thursday: celebration
        assertTrue(ColombianHolidays.isHoliday(LocalDate.of(2026, 7, 13)))  // Monday: day off
    }

    // ---------- API ----------

    @Test
    fun `holiday on a date returns its name`() {
        assertEquals("Navidad", ColombianHolidays.holidayOn(LocalDate.of(2026, 12, 25))?.name)
        assertNull(ColombianHolidays.holidayOn(LocalDate.of(2026, 12, 24)))
    }

    @Test
    fun `next holiday can cross into the next year`() {
        assertEquals(LocalDate.of(2026, 12, 8), ColombianHolidays.nextHoliday(LocalDate.of(2026, 11, 18)).date)

        val newYear = ColombianHolidays.nextHoliday(LocalDate.of(2026, 12, 26))
        assertEquals(LocalDate.of(2027, 1, 1), newYear.date)
        assertEquals("Año Nuevo", newYear.name)
    }

    @Test
    fun `next holiday includes the starting date`() {
        assertEquals(LocalDate.of(2026, 12, 25), ColombianHolidays.nextHoliday(LocalDate.of(2026, 12, 25)).date)
    }

    @Test
    fun `holidays between two dates`() {
        val june = ColombianHolidays.holidaysBetween(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30))
        assertEquals(dates(2026, "06-08", "06-15", "06-29"), june.map { it.date })

        val acrossYears = ColombianHolidays.holidaysBetween(LocalDate.of(2026, 12, 20), LocalDate.of(2027, 1, 15))
        assertEquals(listOf("Navidad", "Año Nuevo", "Día de los Reyes Magos"), acrossYears.map { it.name })
    }

    @Test
    fun `kotlin extension`() {
        assertTrue(LocalDate.of(2026, 1, 1).isColombianHoliday())
    }

    @Test
    fun `years before ley emiliani are rejected`() {
        assertFailsWith<IllegalArgumentException> { ColombianHolidays.holidaysOf(1983) }
    }

    @Test
    fun `invalid range is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            ColombianHolidays.holidaysBetween(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1))
        }
    }

    @Test
    fun `every year from 1984 to 2100 has 18 or 19 holidays`() {
        for (year in ColombianHolidays.FIRST_SUPPORTED_YEAR..2100) {
            val expected = if (year >= 2026) 19 else 18
            val holidays = ColombianHolidays.holidaysOf(year)
            assertEquals(expected, holidays.size, "year $year")
            assertNotNull(holidays.firstOrNull { it.name == "Navidad" })
            // Every moved holiday lands on a Monday.
            holidays.filter { it.isMoved }.forEach {
                assertEquals(java.time.DayOfWeek.MONDAY, it.date.dayOfWeek, "${it.name} $year")
            }
        }
    }
}
