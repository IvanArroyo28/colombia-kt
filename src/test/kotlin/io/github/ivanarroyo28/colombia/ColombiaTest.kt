package io.github.ivanarroyo28.colombia

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class ColombiaTest {

    @Test
    fun `colombia is always utc minus five`() {
        val january = Instant.parse("2026-01-15T12:00:00Z")
        val july = Instant.parse("2026-07-15T12:00:00Z")

        assertEquals(ZoneOffset.ofHours(-5), Colombia.zoneId.rules.getOffset(january))
        assertEquals(ZoneOffset.ofHours(-5), Colombia.zoneId.rules.getOffset(july))
    }

    @Test
    fun `today uses the colombian date even if the server is in utc`() {
        // 03:00 in UTC on Nov 18 is still 22:00 on Nov 17 in Colombia.
        val clock = Clock.fixed(Instant.parse("2026-11-18T03:00:00Z"), ZoneOffset.UTC)

        assertEquals(LocalDate.of(2026, 11, 17), Colombia.today(clock))
    }
}
