package io.github.ivanarroyo28.colombia.holidays

import io.github.ivanarroyo28.colombia.Colombia
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

/**
 * Colombian public holidays ("festivos"), following Ley 51 de 1983 (Ley Emiliani)
 * and Ley 2578 de 2026.
 *
 * ```
 * ColombianHolidays.isHoliday(LocalDate.of(2026, 7, 13)) // true (Chiquinquirá, moved to Monday)
 * ColombianHolidays.holidaysOf(2026).size                // 19
 * ColombianHolidays.nextHoliday()                        // next holiday from today in Colombia
 * ```
 */
public object ColombianHolidays {

    /** Ley Emiliani took effect in 1984. Earlier years followed different rules. */
    public const val FIRST_SUPPORTED_YEAR: Int = 1984

    // Each year is computed once and reused: business-day calculations ask about many dates.
    private val cache = ConcurrentHashMap<Int, List<Holiday>>()

    /**
     * All holidays of [year], sorted by day off.
     *
     * Two holidays can share the same day off (in 2025 both San Pedro y San Pablo and
     * Sagrado Corazón fell on June 30); both are listed. Use [daysOffIn] for unique dates.
     *
     * @throws IllegalArgumentException if [year] is before [FIRST_SUPPORTED_YEAR].
     */
    @JvmStatic
    public fun holidaysOf(year: Int): List<Holiday> {
        require(year >= FIRST_SUPPORTED_YEAR) {
            "Only years from $FIRST_SUPPORTED_YEAR (Ley Emiliani) are supported, got $year"
        }
        return cache.computeIfAbsent(year, ::computeHolidays)
    }

    /** The unique days off of [year], sorted. */
    @JvmStatic
    public fun daysOffIn(year: Int): List<LocalDate> = holidaysOf(year).map { it.date }.distinct()

    /** True if [date] is a day off for a holiday. A moved holiday counts on its Monday, not on its celebration date. */
    @JvmStatic
    public fun isHoliday(date: LocalDate): Boolean = holidaysOn(date).isNotEmpty()

    /** The holiday on [date], or null. If two holidays share the date, the first one by calendar order. */
    @JvmStatic
    public fun holidayOn(date: LocalDate): Holiday? = holidaysOn(date).firstOrNull()

    /** Every holiday on [date]: usually zero or one, rarely two. */
    @JvmStatic
    public fun holidaysOn(date: LocalDate): List<Holiday> = holidaysOf(date.year).filter { it.date == date }

    /** Holidays from [start] to [endInclusive], both included. */
    @JvmStatic
    public fun holidaysBetween(start: LocalDate, endInclusive: LocalDate): List<Holiday> {
        require(!endInclusive.isBefore(start)) { "endInclusive ($endInclusive) is before start ($start)" }
        return (start.year..endInclusive.year)
            .flatMap { holidaysOf(it) }
            .filter { it.date in start..endInclusive }
    }

    /** The first holiday on or after [from] (today in Colombia by default). */
    @JvmStatic
    @JvmOverloads
    public fun nextHoliday(from: LocalDate = Colombia.today()): Holiday =
        generateSequence(from.year) { it + 1 }
            .flatMap { holidaysOf(it).asSequence() }
            .first { !it.date.isBefore(from) }

    /** Easter Sunday of [year]. Jueves Santo, Viernes Santo, Ascensión, Corpus Christi and Sagrado Corazón depend on it. */
    @JvmStatic
    public fun easterSunday(year: Int): LocalDate = computeEasterSunday(year)

    private fun computeHolidays(year: Int): List<Holiday> {
        val easter = easterSunday(year)
        return HolidayRules.all
            .filter { year >= it.firstYear }
            .map { rule ->
                val celebration = rule.celebration(year, easter)
                Holiday(
                    date = if (rule.movesToMonday) celebration.nextMondayOrSame() else celebration,
                    name = rule.name,
                    celebrationDate = celebration,
                )
            }
            .sortedBy { it.date }
    }
}

/** Kotlin shortcut: `LocalDate.of(2026, 7, 13).isColombianHoliday()`. */
public fun LocalDate.isColombianHoliday(): Boolean = ColombianHolidays.isHoliday(this)
