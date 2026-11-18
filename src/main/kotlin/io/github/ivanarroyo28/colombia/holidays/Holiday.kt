package io.github.ivanarroyo28.colombia.holidays

import java.time.LocalDate

/**
 * A Colombian public holiday.
 *
 * @property date the day off. This is the date that matters for work, banks and deadlines.
 * @property name the official name in Spanish, e.g. "Día de San José".
 * @property celebrationDate the date of the celebration itself. It differs from [date] when
 * Ley Emiliani moves the day off to the next Monday (e.g. San José 2025: celebrated on
 * Wednesday, March 19; day off on Monday, March 24).
 */
public data class Holiday(
    val date: LocalDate,
    val name: String,
    val celebrationDate: LocalDate,
) {
    /** True when the day off was moved to a Monday by Ley Emiliani. */
    public val isMoved: Boolean
        get() = date != celebrationDate
}
