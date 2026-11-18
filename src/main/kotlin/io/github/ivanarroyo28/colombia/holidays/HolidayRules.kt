package io.github.ivanarroyo28.colombia.holidays

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * One holiday as the law defines it.
 *
 * @property movesToMonday true for the holidays listed in Ley 51 de 1983 (Ley Emiliani):
 * if they don't fall on a Monday, the day off moves to the next Monday.
 * @property firstYear the first year the holiday applies, so past years stay correct
 * when the law adds (or removes) a holiday.
 * @property celebration the celebration date for a year, given that year's Easter Sunday.
 */
internal class HolidayRule(
    val name: String,
    val movesToMonday: Boolean,
    val firstYear: Int = ColombianHolidays.FIRST_SUPPORTED_YEAR,
    val celebration: (year: Int, easter: LocalDate) -> LocalDate,
)

internal object HolidayRules {

    private fun fixed(month: Int, day: Int): (Int, LocalDate) -> LocalDate =
        { year, _ -> LocalDate.of(year, month, day) }

    private fun daysAfterEaster(days: Long): (Int, LocalDate) -> LocalDate =
        { _, easter -> easter.plusDays(days) }

    /** Every holiday, in calendar order. Sources: Ley 51 de 1983 and Ley 2578 de 2026. */
    val all: List<HolidayRule> = listOf(
        HolidayRule("Año Nuevo", movesToMonday = false, celebration = fixed(1, 1)),
        HolidayRule("Día de los Reyes Magos", movesToMonday = true, celebration = fixed(1, 6)),
        HolidayRule("Día de San José", movesToMonday = true, celebration = fixed(3, 19)),
        HolidayRule("Jueves Santo", movesToMonday = false, celebration = daysAfterEaster(-3)),
        HolidayRule("Viernes Santo", movesToMonday = false, celebration = daysAfterEaster(-2)),
        HolidayRule("Día del Trabajo", movesToMonday = false, celebration = fixed(5, 1)),
        // Thursday 39 days after Easter → day off on the following Monday (Easter + 43).
        HolidayRule("Ascensión del Señor", movesToMonday = true, celebration = daysAfterEaster(39)),
        // Thursday 60 days after Easter → day off on Easter + 64.
        HolidayRule("Corpus Christi", movesToMonday = true, celebration = daysAfterEaster(60)),
        // Friday 68 days after Easter → day off on Easter + 71.
        HolidayRule("Sagrado Corazón de Jesús", movesToMonday = true, celebration = daysAfterEaster(68)),
        HolidayRule("San Pedro y San Pablo", movesToMonday = true, celebration = fixed(6, 29)),
        // Ley 2578 of June 1, 2026: new national holiday, moved to Monday by Ley Emiliani.
        HolidayRule(
            "Día de Nuestra Señora del Rosario de Chiquinquirá",
            movesToMonday = true,
            firstYear = 2026,
            celebration = fixed(7, 9),
        ),
        HolidayRule("Día de la Independencia", movesToMonday = false, celebration = fixed(7, 20)),
        HolidayRule("Batalla de Boyacá", movesToMonday = false, celebration = fixed(8, 7)),
        HolidayRule("Asunción de la Virgen", movesToMonday = true, celebration = fixed(8, 15)),
        HolidayRule("Día de la Raza", movesToMonday = true, celebration = fixed(10, 12)),
        HolidayRule("Día de Todos los Santos", movesToMonday = true, celebration = fixed(11, 1)),
        HolidayRule("Independencia de Cartagena", movesToMonday = true, celebration = fixed(11, 11)),
        HolidayRule("Día de la Inmaculada Concepción", movesToMonday = false, celebration = fixed(12, 8)),
        HolidayRule("Navidad", movesToMonday = false, celebration = fixed(12, 25)),
    )
}

/** The same date if it is a Monday; otherwise the next Monday (Ley Emiliani). */
internal fun LocalDate.nextMondayOrSame(): LocalDate =
    with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
