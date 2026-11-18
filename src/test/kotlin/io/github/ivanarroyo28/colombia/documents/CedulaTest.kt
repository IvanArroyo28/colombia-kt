package io.github.ivanarroyo28.colombia.documents

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CedulaTest {

    @Test
    fun `valid cedulas`() {
        assertTrue(Cedula.isValid("1234567890"))
        assertTrue(Cedula.isValid("1.234.567.890"))
        assertTrue(Cedula.isValid("73 123 456"))
        assertTrue(Cedula.isValid("123"))
    }

    @Test
    fun `invalid cedulas`() {
        assertFalse(Cedula.isValid(""))
        assertFalse(Cedula.isValid("12"))           // too short
        assertFalse(Cedula.isValid("12345678901"))  // too long
        assertFalse(Cedula.isValid("0123456789"))   // leading zero
        assertFalse(Cedula.isValid("12345A"))
        assertFalse(Cedula.isValid("1234-567"))
    }

    @Test
    fun `format groups the digits`() {
        assertEquals("1.234.567.890", Cedula.format("1234567890"))
        assertEquals("73.123.456", Cedula.format("73 123 456"))
    }

    @Test
    fun `format rejects invalid input`() {
        assertFailsWith<IllegalArgumentException> { Cedula.format("0123") }
    }
}
