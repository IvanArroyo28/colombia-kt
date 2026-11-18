package io.github.ivanarroyo28.colombia.divipola

/**
 * One of Colombia's 33 first-level divisions in DIVIPOLA: the 32 departments plus Bogotá, D.C.
 *
 * ```
 * val bolivar = Divipola.department("13")!!
 * bolivar.name                   // "Bolívar"
 * bolivar.municipalities.size    // 46
 * ```
 *
 * @property code Two-digit DANE code, with its leading zero: "05" is Antioquia.
 * @property name Name in normal Spanish capitalization, e.g. "Valle del Cauca".
 * @property officialName Name exactly as DANE publishes it (upper case), e.g. "VALLE DEL CAUCA".
 */
public data class Department(
    public val code: String,
    public val name: String,
    public val officialName: String,
) {
    /** Municipalities and areas of this department, ordered by code. */
    public val municipalities: List<Municipality>
        get() = Divipola.municipalitiesOf(code)

    override fun toString(): String = "$code $name"
}
