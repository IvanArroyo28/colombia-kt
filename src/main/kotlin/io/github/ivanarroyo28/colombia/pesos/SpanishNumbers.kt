package io.github.ivanarroyo28.colombia.pesos

/**
 * Spanish number words for amounts of money (masculine nouns: pesos, centavos).
 *
 * Because a noun always follows, "uno" is shortened: "un peso", "veintiún pesos",
 * "treinta y un mil", "un millón".
 */
internal object SpanishNumbers {

    private const val MILLION = 1_000_000L
    private const val BILLION = 1_000_000_000_000L // Spanish "billón" = 10^12, not the US billion

    private val UNITS = listOf(
        "", "un", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
        "diez", "once", "doce", "trece", "catorce", "quince",
        "dieciséis", "diecisiete", "dieciocho", "diecinueve",
        "veinte", "veintiún", "veintidós", "veintitrés", "veinticuatro",
        "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve",
    )
    private val TENS = listOf("", "", "", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa")
    private val HUNDREDS = listOf(
        "", "ciento", "doscientos", "trescientos", "cuatrocientos",
        "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos",
    )

    /** 1234567 → "un millón doscientos treinta y cuatro mil quinientos sesenta y siete pesos". */
    fun pesosInWords(amount: Long): String {
        require(amount >= 0)
        if (amount == 0L) return "cero pesos"

        val billions = amount / BILLION
        val millions = (amount % BILLION) / MILLION
        val rest = amount % MILLION

        val words = buildList {
            if (billions > 0) add(if (billions == 1L) "un billón" else "${belowMillion(billions)} billones")
            if (millions > 0) add(if (millions == 1L) "un millón" else "${belowMillion(millions)} millones")
            if (rest > 0) add(belowMillion(rest))
        }.joinToString(" ")

        val noun = when {
            amount == 1L -> "peso"
            // Exact millions take "de": "un millón de pesos", but "un millón doscientos mil pesos".
            amount % MILLION == 0L -> "de pesos"
            else -> "pesos"
        }
        return "$words $noun"
    }

    /** 50 → "cincuenta centavos", 1 → "un centavo". */
    fun centsInWords(cents: Int): String {
        require(cents in 1..99)
        return if (cents == 1) "un centavo" else "${belowThousand(cents.toLong())} centavos"
    }

    /** 1 to 999 999. */
    private fun belowMillion(n: Long): String {
        val thousands = n / 1000
        val rest = n % 1000
        return buildList {
            when {
                thousands == 1L -> add("mil") // "mil", never "un mil"
                thousands > 1 -> add("${belowThousand(thousands)} mil")
            }
            if (rest > 0) add(belowThousand(rest))
        }.joinToString(" ")
    }

    /** 1 to 999. */
    private fun belowThousand(n: Long): String {
        if (n == 100L) return "cien" // but 101 is "ciento un"
        val hundreds = (n / 100).toInt()
        val rest = (n % 100).toInt()
        return buildList {
            if (hundreds > 0) add(HUNDREDS[hundreds])
            when {
                rest == 0 -> Unit
                rest < 30 -> add(UNITS[rest])
                rest % 10 == 0 -> add(TENS[rest / 10])
                else -> add("${TENS[rest / 10]} y ${UNITS[rest % 10]}")
            }
        }.joinToString(" ")
    }
}
