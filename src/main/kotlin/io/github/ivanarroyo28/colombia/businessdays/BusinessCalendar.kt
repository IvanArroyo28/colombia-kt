package io.github.ivanarroyo28.colombia.businessdays

import io.github.ivanarroyo28.colombia.holidays.ColombianHolidays
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

/**
 * Business days ("días hábiles") in Colombia: working weekdays that are not public holidays.
 *
 * What counts as a working weekday depends on the context, so it is configurable:
 * most offices and legal deadlines use Monday to Friday ([MONDAY_TO_FRIDAY]),
 * while many shops and some companies also work on Saturday ([MONDAY_TO_SATURDAY]).
 *
 * ```
 * val calendar = BusinessCalendar.MONDAY_TO_FRIDAY
 * calendar.addBusinessDays(LocalDate.of(2026, 12, 18), 10) // 2027-01-05: skips Christmas and New Year
 * ```
 *
 * @param workingDays the weekdays that are normally worked.
 * @param extraDaysOff dates off that are not public holidays, e.g. a company closing on December 24.
 */
public class BusinessCalendar @JvmOverloads constructor(
    workingDays: Set<DayOfWeek> = WEEKDAYS,
    extraDaysOff: Set<LocalDate> = emptySet(),
) {
    // Defensive copies: changing the caller's sets later must not change this calendar.
    private val workingDays: Set<DayOfWeek> = workingDays.toSet()
    private val extraDaysOff: Set<LocalDate> = extraDaysOff.toSet()

    init {
        require(this.workingDays.isNotEmpty()) { "A calendar needs at least one working day" }
    }

    /** True if [date] is a working weekday, not a public holiday and not one of the extra days off. */
    public fun isBusinessDay(date: LocalDate): Boolean =
        date.dayOfWeek in workingDays &&
            date !in extraDaysOff &&
            !ColombianHolidays.isHoliday(date)

    /**
     * The date that is [days] business days after [date] (or before it, if [days] is negative).
     * The starting date itself is never counted, and `days = 0` returns [date] unchanged,
     * the same convention as Excel's WORKDAY.
     */
    public fun addBusinessDays(date: LocalDate, days: Int): LocalDate {
        val step = if (days >= 0) 1L else -1L
        var remaining = kotlin.math.abs(days)
        var current = date
        while (remaining > 0) {
            current = current.plusDays(step)
            if (isBusinessDay(current)) remaining--
        }
        return current
    }

    /** The first business day strictly after [date]. */
    public fun nextBusinessDay(date: LocalDate): LocalDate = addBusinessDays(date, 1)

    /** The last business day strictly before [date]. */
    public fun previousBusinessDay(date: LocalDate): LocalDate = addBusinessDays(date, -1)

    /** [date] if it is a business day; otherwise the next one. Handy for payment or delivery dates. */
    public fun nextOrSameBusinessDay(date: LocalDate): LocalDate =
        if (isBusinessDay(date)) date else nextBusinessDay(date)

    /** [date] if it is a business day; otherwise the previous one. */
    public fun previousOrSameBusinessDay(date: LocalDate): LocalDate =
        if (isBusinessDay(date)) date else previousBusinessDay(date)

    /**
     * How many business days there are from [start] (included) to [endExclusive] (excluded).
     * Negative if [endExclusive] is before [start], like `ChronoUnit.DAYS.between`.
     */
    public fun businessDaysBetween(start: LocalDate, endExclusive: LocalDate): Int {
        if (endExclusive.isBefore(start)) return -businessDaysBetween(endExclusive, start)
        var count = 0
        var current = start
        while (current.isBefore(endExclusive)) {
            if (isBusinessDay(current)) count++
            current = current.plusDays(1)
        }
        return count
    }

    /** Every business day of [month], in order. */
    public fun businessDaysIn(month: YearMonth): List<LocalDate> =
        (1..month.lengthOfMonth())
            .map { month.atDay(it) }
            .filter { isBusinessDay(it) }

    public companion object {
        private val WEEKDAYS: Set<DayOfWeek> = setOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
        )

        /** Monday to Friday, minus public holidays. The usual meaning of "días hábiles". */
        @JvmField
        public val MONDAY_TO_FRIDAY: BusinessCalendar = BusinessCalendar()

        /** Monday to Saturday, minus public holidays. Common in retail and some companies. */
        @JvmField
        public val MONDAY_TO_SATURDAY: BusinessCalendar = BusinessCalendar(WEEKDAYS + DayOfWeek.SATURDAY)
    }
}

/** Kotlin shortcut using [BusinessCalendar.MONDAY_TO_FRIDAY]. */
public fun LocalDate.isColombianBusinessDay(): Boolean = BusinessCalendar.MONDAY_TO_FRIDAY.isBusinessDay(this)

/** Kotlin shortcut using [BusinessCalendar.MONDAY_TO_FRIDAY]: `LocalDate.now().plusBusinessDays(10)`. */
public fun LocalDate.plusBusinessDays(days: Int): LocalDate =
    BusinessCalendar.MONDAY_TO_FRIDAY.addBusinessDays(this, days)
