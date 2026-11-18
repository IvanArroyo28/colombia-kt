package io.github.ivanarroyo28.colombia.documents

/**
 * Identity document types with the codes the DIAN uses in electronic invoicing
 * (Resolución 000042 de 2020, technical annex).
 *
 * ```
 * DocumentType.fromDianCode("13") // CITIZENSHIP_CARD
 * DocumentType.NIT.dianCode       // "31"
 * ```
 */
public enum class DocumentType(
    public val dianCode: String,
    public val spanishName: String,
) {
    BIRTH_REGISTRY("11", "Registro civil"),
    IDENTITY_CARD("12", "Tarjeta de identidad"),
    CITIZENSHIP_CARD("13", "Cédula de ciudadanía"),
    FOREIGNER_ID_CARD("21", "Tarjeta de extranjería"),
    FOREIGNER_ID("22", "Cédula de extranjería"),
    NIT("31", "NIT"),
    PASSPORT("41", "Pasaporte"),
    FOREIGN_ID_DOCUMENT("42", "Documento de identificación extranjero"),
    PEP("47", "PEP (Permiso Especial de Permanencia)"),
    FOREIGN_NIT("50", "NIT de otro país"),
    NUIP("91", "NUIP"),
    ;

    public companion object {
        /** The type for a DIAN code such as "13", or null if the code is unknown. */
        @JvmStatic
        public fun fromDianCode(code: String): DocumentType? = entries.firstOrNull { it.dianCode == code.trim() }
    }
}
