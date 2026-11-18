package io.github.ivanarroyo28.colombia.divipola

/**
 * The DIVIPOLA tables, read once from the resource files that `./gradlew updateDivipola` generates.
 * Each line is tab-separated; lines starting with # are comments.
 */
internal class DivipolaData(
    val departments: List<Department>,
    val municipalities: List<Municipality>,
) {
    val departmentsByCode: Map<String, Department> = departments.associateBy { it.code }
    val municipalitiesByCode: Map<String, Municipality> = municipalities.associateBy { it.code }
    val municipalitiesByDepartment: Map<String, List<Municipality>> = municipalities.groupBy { it.departmentCode }

    /** [normalizeForSearch] of each municipality name, in the same order as [municipalities]. */
    val searchNames: List<String> = municipalities.map { normalizeForSearch(it.name) }

    companion object {
        private const val RESOURCE_DIR = "/io/github/ivanarroyo28/colombia/divipola/"

        private val TYPES = mapOf(
            "M" to MunicipalityType.MUNICIPALITY,
            "I" to MunicipalityType.ISLAND,
            "A" to MunicipalityType.NON_MUNICIPALIZED_AREA,
        )

        fun load(): DivipolaData {
            val departments = readTable("departamentos.tsv", columns = 2).map { (code, official) ->
                require(code.length == 2 && code.all { it in '0'..'9' }) { "Bad department code: $code" }
                Department(code, spanishTitleCase(official), official)
            }
            val municipalities = readTable("municipios.tsv", columns = 5).map { (code, official, type, lat, lon) ->
                require(code.length == 5 && code.all { it in '0'..'9' }) { "Bad municipality code: $code" }
                Municipality(
                    code = code,
                    name = spanishTitleCase(official),
                    officialName = official,
                    departmentCode = code.substring(0, 2),
                    type = TYPES[type] ?: error("Unknown type '$type' for $code"),
                    // DANE writes coordinates with a decimal comma: "10,385126".
                    latitude = lat.replace(',', '.').toDouble(),
                    longitude = lon.replace(',', '.').toDouble(),
                )
            }
            return DivipolaData(departments, municipalities)
        }

        private fun readTable(fileName: String, columns: Int): List<List<String>> {
            val stream = DivipolaData::class.java.getResourceAsStream(RESOURCE_DIR + fileName)
                ?: error("Missing resource $RESOURCE_DIR$fileName. Run ./gradlew updateDivipola")
            return stream.bufferedReader(Charsets.UTF_8).useLines { lines ->
                lines.filter { it.isNotBlank() && !it.startsWith("#") }
                    .map { line ->
                        line.split('\t').also {
                            require(it.size == columns) { "Expected $columns columns in $fileName: $line" }
                        }
                    }
                    .toList()
            }
        }
    }
}
