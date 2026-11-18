package io.github.ivanarroyo28.colombia.pesos

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PesosFormatTest {

    @Test
    fun `format whole pesos`() {
        assertEquals("$ 1.234.567", Pesos.format(1234567))
        assertEquals("$ 0", Pesos.format(0))
        assertEquals("$ 999", Pesos.format(999))
        assertEquals("$ 1.000", Pesos.format(1000))
    }

    @Test
    fun `format with decimals`() {
        assertEquals("$ 1.500,50", Pesos.format(BigDecimal("1500.5"), decimals = 2))
        assertEquals("$ 3.293,3", Pesos.format(BigDecimal("3293.28"), decimals = 1))
    }

    @Test
    fun `format rounds half up to the peso`() {
        assertEquals("$ 1.501", Pesos.format(BigDecimal("1500.5")))
        assertEquals("$ 1.500", Pesos.format(BigDecimal("1500.49")))
    }

    @Test
    fun `format negative amounts and without symbol`() {
        assertEquals("-$ 20.000", Pesos.format(-20000))
        assertEquals("1.234.567", Pesos.format(1234567, symbol = false))
    }

    @Test
    fun `format rejects too many decimals`() {
        assertFailsWith<IllegalArgumentException> { Pesos.format(BigDecimal.ONE, decimals = 3) }
    }

    @Test
    fun `parse colombian amounts`() {
        assertEquals(BigDecimal("1234567"), Pesos.parse("$ 1.234.567"))
        assertEquals(BigDecimal("1234567"), Pesos.parse("1234567"))
        assertEquals(BigDecimal("1500.50"), Pesos.parse("$1.500,50"))
        assertEquals(BigDecimal("-20000"), Pesos.parse("-$ 20.000"))
        assertEquals(BigDecimal("-20000"), Pesos.parse("$ -20.000"))
        assertEquals(BigDecimal("20000"), Pesos.parse("20.000 COP"))
        assertEquals(BigDecimal("20000"), Pesos.parse("COP 20.000"))
        assertEquals(BigDecimal("1.234"), Pesos.parse("1,234")) // Colombian: comma is the decimal separator
    }

    @Test
    fun `parse rejects ambiguous or foreign formats`() {
        assertNull(Pesos.parse("1.5"))        // dots must separate groups of three
        assertNull(Pesos.parse("1,234.50"))   // US style
        assertNull(Pesos.parse("12.34.567"))
        assertNull(Pesos.parse(""))
        assertNull(Pesos.parse("$"))
        assertNull(Pesos.parse("mil pesos"))
    }

    @Test
    fun `format and parse round trip`() {
        val amount = BigDecimal("98765432.10")
        assertEquals(amount, Pesos.parse(Pesos.format(amount, decimals = 2)))
    }
}
