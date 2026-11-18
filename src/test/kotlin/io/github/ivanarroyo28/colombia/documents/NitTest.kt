package io.github.ivanarroyo28.colombia.documents

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NitTest {

    // Real, publicly listed NITs (the DIAN's own is 800.197.268-4) and edge cases.
    @ParameterizedTest(name = "DV of {0} is {1}")
    @CsvSource(
        "800197268, 4",
        "860069804, 2",
        "900373115, 3",
        "899999068, 1",
        "830000000, 1",
        "1, 8",
        "123456789012345, 2",
    )
    fun `verification digit`(number: String, expected: Int) {
        assertEquals(expected, Nit.verificationDigit(number))
    }

    @Test
    fun `separators are ignored when computing the digit`() {
        assertEquals(4, Nit.verificationDigit("800.197.268"))
        assertEquals(4, Nit.verificationDigit(" 800 197 268 "))
    }

    @ParameterizedTest(name = "\"{0}\" is valid")
    @ValueSource(strings = ["800.197.268-4", "800197268-4", "800 197 268 - 4", "8001972684", " 800.197.268-4 "])
    fun `valid formats`(nit: String) {
        assertTrue(Nit.isValid(nit))
    }

    @ParameterizedTest(name = "\"{0}\" is not valid")
    @ValueSource(strings = ["800.197.268-5", "8001972685", "", "-4", "4", "ABC-1", "800197268-", "800197268-12", "1234567890123456-7"])
    fun `invalid inputs`(nit: String) {
        assertFalse(Nit.isValid(nit))
    }

    @Test
    fun `parse returns the clean number and digit`() {
        val nit = Nit.parse("800.197.268-4")!!
        assertEquals("800197268", nit.number)
        assertEquals(4, nit.verificationDigit)
        assertEquals("800.197.268-4", nit.formatted())
    }

    @Test
    fun `parse returns null for a wrong digit`() {
        assertNull(Nit.parse("800.197.268-0"))
    }

    @Test
    fun `format adds dots and the verification digit`() {
        assertEquals("900.373.115-3", Nit.format("900373115"))
        assertEquals("1-8", Nit.format("1"))
    }

    @Test
    fun `format rejects non digits`() {
        assertFailsWith<IllegalArgumentException> { Nit.format("90037311A") }
        assertFailsWith<IllegalArgumentException> { Nit.verificationDigit("") }
    }

    @Test
    fun `non latin digits are rejected`() {
        assertFalse(Nit.isValid("٨٠٠١٩٧٢٦٨-4")) // Arabic-Indic digits
    }
}
