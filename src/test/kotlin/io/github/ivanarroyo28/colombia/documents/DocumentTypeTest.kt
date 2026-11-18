package io.github.ivanarroyo28.colombia.documents

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DocumentTypeTest {

    @Test
    fun `lookup by dian code`() {
        assertEquals(DocumentType.CITIZENSHIP_CARD, DocumentType.fromDianCode("13"))
        assertEquals(DocumentType.NIT, DocumentType.fromDianCode(" 31 "))
        assertNull(DocumentType.fromDianCode("99"))
    }

    @Test
    fun `codes are unique`() {
        val codes = DocumentType.entries.map { it.dianCode }
        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun `spanish names`() {
        assertEquals("Cédula de ciudadanía", DocumentType.CITIZENSHIP_CARD.spanishName)
    }
}
