package io.github.ivanarroyo28.colombia.divipola

/** The kind of territorial entity, as DANE classifies it in DIVIPOLA. */
public enum class MunicipalityType(public val spanishName: String) {
    /** A regular municipality. Almost every entry is one. */
    MUNICIPALITY("Municipio"),

    /** San Andrés (88001), which DANE lists as "Isla". */
    ISLAND("Isla"),

    /** Areas of Amazonas, Guainía and Vaupés that are not part of any municipality. */
    NON_MUNICIPALIZED_AREA("Área no municipalizada"),
}
