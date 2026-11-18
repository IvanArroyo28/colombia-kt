package io.github.ivanarroyo28.colombia.pesos

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PesosWordsTest {

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource(
        delimiter = '|',
        value = [
            "0 | cero pesos",
            "1 | un peso",
            "2 | dos pesos",
            "16 | dieciséis pesos",
            "21 | veintiún pesos",
            "22 | veintidós pesos",
            "30 | treinta pesos",
            "31 | treinta y un pesos",
            "99 | noventa y nueve pesos",
            "100 | cien pesos",
            "101 | ciento un pesos",
            "115 | ciento quince pesos",
            "500 | quinientos pesos",
            "999 | novecientos noventa y nueve pesos",
            "1000 | mil pesos",
            "1001 | mil un pesos",
            "21000 | veintiún mil pesos",
            "31500 | treinta y un mil quinientos pesos",
            "100000 | cien mil pesos",
            "101000 | ciento un mil pesos",
            "1000000 | un millón de pesos",
            "1001000 | un millón mil pesos",
            "1200000 | un millón doscientos mil pesos",
            "2000000 | dos millones de pesos",
            "1234567 | un millón doscientos treinta y cuatro mil quinientos sesenta y siete pesos",
            "21000000 | veintiún millones de pesos",
            "1000000000 | mil millones de pesos",
            "1001000000 | mil un millones de pesos",
            "2500000000 | dos mil quinientos millones de pesos",
            "1000000000000 | un billón de pesos",
        ],
    )
    fun `whole pesos in words`(amount: Long, expected: String) {
        assertEquals(expected, Pesos.toWords(amount))
    }

    @Test
    fun `cents are added with con`() {
        assertEquals("mil quinientos pesos con cincuenta centavos", Pesos.toWords(BigDecimal("1500.50")))
        assertEquals("cero pesos con un centavo", Pesos.toWords(BigDecimal("0.01")))
        assertEquals("veintiún mil quinientos pesos con veintiún centavos", Pesos.toWords(BigDecimal("21500.21")))
    }

    @Test
    fun `cents are rounded half up`() {
        assertEquals("un peso", Pesos.toWords(BigDecimal("0.995")))
    }

    @Test
    fun `mcte suffix for invoices`() {
        assertEquals("UN MILLÓN DE PESOS M/CTE", Pesos.toWords(1_000_000, mcte = true).uppercase())
    }

    @Test
    fun `largest supported amount`() {
        val words = Pesos.toWords(Pesos.MAX_WORDS_AMOUNT)
        assertEquals(true, words.startsWith("novecientos noventa y nueve mil novecientos noventa y nueve billones"))
        assertEquals(true, words.endsWith("con noventa y nueve centavos"))
    }

    @Test
    fun `negative or too large amounts are rejected`() {
        assertFailsWith<IllegalArgumentException> { Pesos.toWords(-1) }
        assertFailsWith<IllegalArgumentException> { Pesos.toWords(BigDecimal("1000000000000000000")) }
    }
}
