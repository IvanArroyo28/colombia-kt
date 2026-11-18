package io.github.ivanarroyo28.colombia.holidays

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import kotlin.test.assertEquals

class EasterTest {

    @ParameterizedTest(name = "Easter {0} is {1}")
    @CsvSource(
        "1984, 1984-04-22",
        "2000, 2000-04-23",
        "2019, 2019-04-21",
        "2024, 2024-03-31",
        "2025, 2025-04-20",
        "2026, 2026-04-05",
        "2027, 2027-03-28",
        "2038, 2038-04-25",
    )
    fun `easter sunday matches known dates`(year: Int, expected: String) {
        assertEquals(LocalDate.parse(expected), ColombianHolidays.easterSunday(year))
    }
}
