package io.github.ivanarroyo28.colombia.divipola

import kotlin.test.Test
import kotlin.test.assertEquals

class NamesTest {

    @Test
    fun `title case keeps connectors in lower case`() {
        assertEquals("San José del Guaviare", spanishTitleCase("SAN JOSÉ DEL GUAVIARE"))
        assertEquals("Belén de los Andaquíes", spanishTitleCase("BELÉN DE LOS ANDAQUÍES"))
        assertEquals("Archipiélago de San Andrés, Providencia y Santa Catalina",
            spanishTitleCase("ARCHIPIÉLAGO DE SAN ANDRÉS, PROVIDENCIA Y SANTA CATALINA"))
    }

    @Test
    fun `title case capitalizes the first word even if it is a connector`() {
        assertEquals("La Unión", spanishTitleCase("LA UNIÓN"))
        assertEquals("Los Patios", spanishTitleCase("LOS PATIOS"))
    }

    @Test
    fun `title case keeps abbreviations and hyphens`() {
        assertEquals("Bogotá, D.C.", spanishTitleCase("BOGOTÁ, D.C."))
        assertEquals("Mirití - Paraná", spanishTitleCase("MIRITÍ - PARANÁ"))
        assertEquals("Itagüí", spanishTitleCase("ITAGÜÍ"))
    }

    @Test
    fun `normalization removes accents, case and punctuation`() {
        assertEquals("bogota d c", normalizeForSearch("Bogotá, D.C."))
        assertEquals("miriti parana", normalizeForSearch("MIRITÍ - PARANÁ"))
        assertEquals("itagui", normalizeForSearch("Itagüí"))
        assertEquals("la pena", normalizeForSearch("  La   Peña "))
    }
}
