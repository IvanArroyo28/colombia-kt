package io.github.ivanarroyo28.colombia.divipola

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Expected values checked against DANE's DIVIPOLA on datos.gov.co (cut-off 30 Dec 2024).
// If DANE creates or merges municipalities, update the counts after ./gradlew updateDivipola.
class DivipolaTest {

    @Test
    fun `totals match DANE`() {
        assertEquals(33, Divipola.departments.size)
        assertEquals(1122, Divipola.municipalities.size)
        assertEquals(1103, Divipola.municipalities.count { it.type == MunicipalityType.MUNICIPALITY })
        assertEquals(18, Divipola.municipalities.count { it.type == MunicipalityType.NON_MUNICIPALIZED_AREA })
        assertEquals(listOf("88001"), Divipola.municipalities.filter { it.type == MunicipalityType.ISLAND }.map { it.code })
    }

    @Test
    fun `codes are unique and sorted`() {
        val codes = Divipola.municipalities.map { it.code }
        assertEquals(codes.toSet().size, codes.size)
        assertEquals(codes.sorted(), codes)
        assertEquals(Divipola.departments.map { it.code }.sorted(), Divipola.departments.map { it.code })
    }

    @Test
    fun `every municipality belongs to a known department`() {
        Divipola.municipalities.forEach { assertNotNull(Divipola.department(it.departmentCode), it.code) }
        assertEquals(Divipola.municipalities.size, Divipola.departments.sumOf { it.municipalities.size })
    }

    @Test
    fun `department lookup`() {
        assertEquals("Antioquia", Divipola.department("05")?.name)
        assertEquals("Bogotá, D.C.", Divipola.department("11")?.name)
        assertEquals("Norte de Santander", Divipola.department(" 54 ")?.name)
        assertEquals("VALLE DEL CAUCA", Divipola.department("76")?.officialName)
        assertNull(Divipola.department("5"))
        assertNull(Divipola.department("12"))
    }

    @Test
    fun `municipality lookup`() {
        val cartagena = assertNotNull(Divipola.municipality("13001"))
        assertEquals("Cartagena de Indias", cartagena.name)
        assertEquals("CARTAGENA DE INDIAS", cartagena.officialName)
        assertEquals("Bolívar", cartagena.department.name)
        assertEquals("Cartagena de Indias, Bolívar", cartagena.fullName)
        assertEquals(10.385126, cartagena.latitude, 1e-9)
        assertEquals(-75.496269, cartagena.longitude, 1e-9)

        assertEquals("Medellín", Divipola.municipality("05001")?.name)
        assertEquals("Santiago de Cali", Divipola.municipality("76001")?.name)
        assertNull(Divipola.municipality("5001"))
        assertNull(Divipola.municipality("99999"))
    }

    @Test
    fun `recent municipalities are included`() {
        assertEquals("Barrancominas", Divipola.municipality("94343")?.name)
        assertEquals("Nuevo Belén de Bajirá", Divipola.municipality("27493")?.name)
    }

    @Test
    fun `bogota full name is not repeated`() {
        assertEquals("Bogotá, D.C.", Divipola.municipality("11001")?.fullName)
    }

    @Test
    fun `municipalities of a department`() {
        assertEquals(46, Divipola.municipalitiesOf("13").size)
        assertEquals(125, Divipola.municipalitiesOf("05").size)
        assertEquals(1, Divipola.municipalitiesOf("11").size)
        val amazonas = Divipola.municipalitiesOf("91")
        assertEquals(11, amazonas.size)
        assertEquals(9, amazonas.count { it.type == MunicipalityType.NON_MUNICIPALIZED_AREA })
        assertTrue(Divipola.municipalitiesOf("00").isEmpty())
    }

    @Test
    fun `coordinates are inside Colombia`() {
        Divipola.municipalities.forEach {
            assertTrue(it.latitude in -4.3..13.5, "${it.code} latitude ${it.latitude}")
            assertTrue(it.longitude in -82.0..-66.8, "${it.code} longitude ${it.longitude}")
        }
    }

    @Test
    fun `search ignores accents and case`() {
        assertEquals(listOf("11001"), Divipola.search("bogota").map { it.code })
        assertEquals("05001", Divipola.search("MEDELLIN").first().code)
        assertEquals("05042", Divipola.search("santa fe de antioquia").first().code)
        assertEquals("91460", Divipola.search("miriti parana").first().code)
        assertEquals("25398", Divipola.search("pena").first().code)
    }

    @Test
    fun `search ranks exact and prefix matches first`() {
        assertEquals(listOf("13001", "18150"), Divipola.search("cartagena").map { it.code })
        assertEquals(
            listOf("68669", "88001", "05647", "23670", "52835"),
            Divipola.search("San Andrés").map { it.code },
        )
    }

    @Test
    fun `search returns repeated names ordered by code`() {
        val results = Divipola.search("la unión")
        assertEquals(listOf("05400", "52399", "70400", "76400"), results.map { it.code })
        assertEquals("La Unión, Nariño", results[1].fullName)
    }

    @Test
    fun `search limit and empty queries`() {
        assertEquals(3, Divipola.search("san", limit = 3).size)
        assertTrue(Divipola.search("   ").isEmpty())
        assertTrue(Divipola.search("san", limit = 0).isEmpty())
        assertTrue(Divipola.search("xyzxyz").isEmpty())
        assertFailsWith<IllegalArgumentException> { Divipola.search("san", limit = -1) }
    }
}
