package io.github.ivanarroyo28.colombia.pesos

import io.github.ivanarroyo28.colombia.internal.groupThousands
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Colombian pesos (COP): format, parse and write amounts in words.
 *
 * ```
 * Pesos.format(1234567)                // "$ 1.234.567"
 * Pesos.format(BigDecimal("1500.5"), decimals = 2) // "$ 1.500,50"
 * Pesos.parse("$ 1.234.567,50")        // 1234567.50
 * Pesos.toWords(1200000)               // "un millón doscientos mil pesos"
 * ```
 */
public object Pesos {

    /** Largest amount [toWords] can write: 999.999 billones (Spanish "billón" = 10¹²). */
    @JvmField
    public val MAX_WORDS_AMOUNT: BigDecimal = BigDecimal("999999999999999999.99")

    // ---------------------------------------------------------------- format

    /**
     * Formats [amount] the Colombian way: dots for thousands, comma for decimals.
     * Rounds half up ("redondeo al peso").
     *
     * @param decimals 0 (default: cents are rarely used in Colombia), 1 or 2.
     * @param symbol whether to start with "$ ".
     */
    @JvmStatic
    @JvmOverloads
    public fun format(amount: BigDecimal, decimals: Int = 0, symbol: Boolean = true): String {
        require(decimals in 0..2) { "decimals must be 0, 1 or 2, got $decimals" }
        val rounded = amount.setScale(decimals, RoundingMode.HALF_UP)
        val plain = rounded.abs().toPlainString()
        val integerPart = plain.substringBefore('.')
        val decimalPart = plain.substringAfter('.', missingDelimiterValue = "")

        return buildString {
            if (rounded.signum() < 0) append('-')
            if (symbol) append("$ ")
            append(groupThousands(integerPart))
            if (decimals > 0) append(',').append(decimalPart)
        }
    }

    /** [format] for whole pesos. */
    @JvmStatic
    @JvmOverloads
    public fun format(amount: Long, decimals: Int = 0, symbol: Boolean = true): String =
        format(BigDecimal.valueOf(amount), decimals, symbol)

    // ---------------------------------------------------------------- parse

    // 1.234.567 (dots every three digits) or 1234567, optionally with ,decimals.
    private val AMOUNT = Regex("""^(\d{1,3}(?:\.\d{3})+|\d+)(?:,(\d+))?$""")

    /**
     * Reads an amount written the Colombian way: `"$ 1.234.567"`, `"1234567"`, `"$1.500,50"`,
     * `"-$ 20.000"`, `"20.000 COP"`.
     *
     * Dots must separate groups of exactly three digits, so ambiguous text such as
     * `"1.5"` or the US style `"1,234.50"` returns null instead of a wrong number.
     *
     * @return the amount, or null if the text is not a valid Colombian amount.
     */
    @JvmStatic
    public fun parse(text: String): BigDecimal? {
        var s = text.trim()
        var negative = false
        if (s.startsWith("-")) {
            negative = true
            s = s.drop(1).trim()
        }
        s = s.removePrefix("COP").removeSuffix("COP").trim()
        s = s.removePrefix("$").trim()
        if (!negative && s.startsWith("-")) {
            negative = true
            s = s.drop(1).trim()
        }

        val match = AMOUNT.matchEntire(s) ?: return null
        val integerPart = match.groupValues[1].replace(".", "")
        val decimalPart = match.groupValues[2]
        val value = BigDecimal(if (decimalPart.isEmpty()) integerPart else "$integerPart.$decimalPart")
        return if (negative) value.negate() else value
    }

    // ---------------------------------------------------------------- words

    /**
     * Writes [amount] in Spanish words, as on cheques, invoices and contracts.
     * Cents, if any (rounded half up to two decimals), are added as "con … centavos".
     *
     * ```
     * Pesos.toWords(BigDecimal("1000000"))           // "un millón de pesos"
     * Pesos.toWords(BigDecimal("21500.50"))          // "veintiún mil quinientos pesos con cincuenta centavos"
     * Pesos.toWords(BigDecimal("1000000"), mcte = true).uppercase() // "UN MILLÓN DE PESOS M/CTE"
     * ```
     *
     * @param mcte adds "M/CTE" ("moneda corriente"), common on invoices and legal documents.
     * @throws IllegalArgumentException if [amount] is negative or above [MAX_WORDS_AMOUNT].
     */
    @JvmStatic
    @JvmOverloads
    public fun toWords(amount: BigDecimal, mcte: Boolean = false): String {
        require(amount.signum() >= 0) { "Amount must not be negative, got $amount" }
        require(amount <= MAX_WORDS_AMOUNT) { "Amount is too large to write in words: $amount" }

        val rounded = amount.setScale(2, RoundingMode.HALF_UP)
        val pesos = rounded.toBigInteger().toLong()
        val cents = rounded.remainder(BigDecimal.ONE).movePointRight(2).toInt()

        return buildString {
            append(SpanishNumbers.pesosInWords(pesos))
            if (cents > 0) append(" con ").append(SpanishNumbers.centsInWords(cents))
            if (mcte) append(" M/CTE")
        }
    }

    /** [toWords] for whole pesos. */
    @JvmStatic
    @JvmOverloads
    public fun toWords(amount: Long, mcte: Boolean = false): String = toWords(BigDecimal.valueOf(amount), mcte)
}
