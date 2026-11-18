package io.github.ivanarroyo28.colombia.divipola

/**
 * A municipality (or island, or non-municipalized area) from DIVIPOLA.
 *
 * ```
 * val cartagena = Divipola.municipality("13001")!!
 * cartagena.name             // "Cartagena de Indias"
 * cartagena.department.name  // "Bolívar"
 * cartagena.fullName         // "Cartagena de Indias, Bolívar"
 * ```
 *
 * @property code Five-digit DANE code: the department code followed by three digits, e.g. "05001".
 * @property name Name in normal Spanish capitalization, e.g. "San José del Guaviare".
 * @property officialName Name exactly as DANE publishes it (upper case).
 * @property departmentCode Two-digit code of the department it belongs to.
 * @property type Municipality, island or non-municipalized area.
 * @property latitude Latitude of the urban center in decimal degrees (WGS 84), as published by DANE.
 * @property longitude Longitude of the urban center in decimal degrees (WGS 84), as published by DANE.
 */
public data class Municipality(
    public val code: String,
    public val name: String,
    public val officialName: String,
    public val departmentCode: String,
    public val type: MunicipalityType,
    public val latitude: Double,
    public val longitude: Double,
) {
    /** The department it belongs to. */
    public val department: Department
        get() = checkNotNull(Divipola.department(departmentCode)) { "Unknown department $departmentCode" }

    /**
     * Name plus department, the usual way to tell apart repeated names:
     * "La Unión, Nariño" vs "La Unión, Antioquia". Bogotá is just "Bogotá, D.C.".
     */
    public val fullName: String
        get() = department.name.let { if (it == name) name else "$name, $it" }

    override fun toString(): String = "$code $fullName"
}
