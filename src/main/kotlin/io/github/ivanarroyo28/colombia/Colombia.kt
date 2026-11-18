package io.github.ivanarroyo28.colombia

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

/**
 * Basic facts about Colombia shared by the rest of the library.
 */
public object Colombia {

    /** Colombia has a single time zone, UTC−5, all year long (no daylight saving time). */
    public const val TIME_ZONE_ID: String = "America/Bogota"

    /** [TIME_ZONE_ID] as a [ZoneId]. */
    public val zoneId: ZoneId = ZoneId.of(TIME_ZONE_ID)

    /**
     * Today's date in Colombia, whatever the time zone of the machine running the code.
     *
     * A server in UTC at 2026-11-18 03:00 would say "November 18",
     * but in Colombia it is still 22:00 on November 17.
     *
     * @param clock pass a fixed clock in tests; the system clock is used by default.
     */
    public fun today(clock: Clock = Clock.systemUTC()): LocalDate =
        LocalDate.now(clock.withZone(zoneId))
}
