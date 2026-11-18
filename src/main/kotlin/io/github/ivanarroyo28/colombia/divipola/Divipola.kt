package io.github.ivanarroyo28.colombia.divipola

/**
 * DIVIPOLA, the official list of Colombia's departments and municipalities with their DANE codes.
 *
 * ```
 * Divipola.municipality("13001")?.name      // "Cartagena de Indias"
 * Divipola.department("05")?.name           // "Antioquia"
 * Divipola.search("medellin").first().code  // "05001"
 * Divipola.municipalitiesOf("91").size      // 11
 * ```
 *
 * Data: DANE, published on datos.gov.co (dataset gdxc-w37w), licensed CC BY-SA 4.0.
 * It is loaded the first time you use this object and then kept in memory.
 */
public object Divipola {

    /** Where the data comes from, for credits screens. */
    public const val DATA_SOURCE: String = "DANE - DIVIPOLA, datos.gov.co (gdxc-w37w)"

    /** License of the data (the library code itself is MIT). */
    public const val DATA_LICENSE: String = "CC BY-SA 4.0"

    private val data: DivipolaData by lazy { DivipolaData.load() }

    /** All 33 departments (including Bogotá, D.C.), ordered by code. */
    @JvmStatic
    public val departments: List<Department>
        get() = data.departments

    /** All municipalities, islands and non-municipalized areas, ordered by code. */
    @JvmStatic
    public val municipalities: List<Municipality>
        get() = data.municipalities

    /** The department with a two-digit code such as "05", or null if there is none. */
    @JvmStatic
    public fun department(code: String): Department? = data.departmentsByCode[code.trim()]

    /** The municipality with a five-digit code such as "05001", or null if there is none. */
    @JvmStatic
    public fun municipality(code: String): Municipality? = data.municipalitiesByCode[code.trim()]

    /** Municipalities and areas of a department, ordered by code. Empty if the code is unknown. */
    @JvmStatic
    public fun municipalitiesOf(departmentCode: String): List<Municipality> =
        data.municipalitiesByDepartment[departmentCode.trim()].orEmpty()

    /**
     * Finds municipalities by name, ignoring accents, case and punctuation: "bogota",
     * "CARTAGENA" and "santa fe de antioquia" all work.
     *
     * Best matches come first: the exact name, then names that start with the query,
     * then names with a word that starts with it, then names that contain it anywhere.
     * Repeated names ("La Unión" exists in 4 departments) come out ordered by code.
     *
     * @param limit maximum number of results; must not be negative.
     */
    @JvmStatic
    @JvmOverloads
    public fun search(query: String, limit: Int = 10): List<Municipality> {
        require(limit >= 0) { "limit must not be negative: $limit" }
        val wanted = normalizeForSearch(query)
        if (wanted.isEmpty() || limit == 0) return emptyList()

        return data.municipalities.indices
            .mapNotNull { index ->
                val name = data.searchNames[index]
                val rank = when {
                    name == wanted -> 0
                    name.startsWith(wanted) -> 1
                    " $wanted" in " $name" -> 2
                    wanted in name -> 3
                    else -> return@mapNotNull null
                }
                rank to data.municipalities[index]
            }
            .sortedWith(compareBy<Pair<Int, Municipality>>({ it.first }, { it.second.code }))
            .take(limit)
            .map { it.second }
    }
}
