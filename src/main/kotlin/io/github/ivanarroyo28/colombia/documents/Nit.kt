package io.github.ivanarroyo28.colombia.documents

/**
 * NIT ("Número de Identificación Tributaria") and its DIAN verification digit ("dígito de verificación").
 *
 * ```
 * Nit.verificationDigit("900373115") // 3
 * Nit.format("900373115")            // "900.373.115-3"
 * Nit.isValid("900.373.115-3")       // true
 * Nit.isValid("900.373.115-4")       // false: wrong verification digit
 * ```
 *
 * The verification digit only proves the number is well formed (it catches typos).
 * Whether the NIT exists and is active can only be checked in the DIAN's RUT service.
 */
public object Nit {

    /** The DIAN algorithm has weights for numbers of up to 15 digits. */
    public const val MAX_LENGTH: Int = 15

    // Official DIAN weights, applied from the rightmost digit to the left.
    private val WEIGHTS = intArrayOf(3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71)

    /**
     * The verification digit (0–9) of a NIT number written **without** its verification digit.
     * Dots, commas and spaces are ignored.
     *
     * @throws IllegalArgumentException if [number] is not 1 to [MAX_LENGTH] digits.
     */
    @JvmStatic
    public fun verificationDigit(number: String): Int {
        val digits = requireDigits(number)
        val sum = digits.reversed().withIndex().sumOf { (index, char) -> (char - '0') * WEIGHTS[index] }
        val remainder = sum % 11
        return if (remainder < 2) remainder else 11 - remainder
    }

    /**
     * Reads a NIT **with** its verification digit, in any usual format:
     * `"900.373.115-3"`, `"900373115-3"`, `"900 373 115 - 3"` or `"9003731153"`
     * (without a dash, the last digit is taken as the verification digit).
     *
     * @return the NIT, or null if the text is malformed or the verification digit is wrong.
     */
    @JvmStatic
    public fun parse(nit: String): NitNumber? {
        val text = nit.trim()
        val dash = text.lastIndexOf('-')
        val (numberPart, digitPart) = if (dash >= 0) {
            text.substring(0, dash) to text.substring(dash + 1).trim()
        } else {
            val digits = stripSeparators(text)
            if (digits.length < 2) return null
            digits.dropLast(1) to digits.takeLast(1)
        }

        val number = stripSeparators(numberPart)
        if (!isWellFormed(number)) return null
        if (digitPart.length != 1 || !digitPart[0].isAsciiDigit()) return null

        val digit = digitPart[0] - '0'
        return if (verificationDigit(number) == digit) NitNumber(number, digit) else null
    }

    /** True if [nit] (with its verification digit) is well formed and the digit is correct. */
    @JvmStatic
    public fun isValid(nit: String): Boolean = parse(nit) != null

    /**
     * Formats a NIT number written **without** its verification digit, adding the digit:
     * `"900373115"` → `"900.373.115-3"`.
     *
     * @throws IllegalArgumentException if [number] is not 1 to [MAX_LENGTH] digits.
     */
    @JvmStatic
    public fun format(number: String): String {
        val digits = requireDigits(number)
        return NitNumber(digits, verificationDigit(digits)).formatted()
    }

    private fun requireDigits(number: String): String {
        val digits = stripSeparators(number)
        require(isWellFormed(digits)) { "A NIT must have 1 to $MAX_LENGTH digits, got \"$number\"" }
        return digits
    }

    private fun isWellFormed(digits: String): Boolean =
        digits.length in 1..MAX_LENGTH && digits.all { it.isAsciiDigit() }
}

/**
 * A NIT whose verification digit has been checked.
 *
 * @property number the digits without the verification digit, e.g. "900373115".
 * @property verificationDigit the DIAN verification digit, e.g. 3.
 */
public data class NitNumber(
    val number: String,
    val verificationDigit: Int,
) {
    /** "900.373.115-3" */
    public fun formatted(): String = "${groupThousands(number)}-$verificationDigit"
}

/** Only 0–9: [Char.isDigit] would also accept digits from other scripts, like Arabic ones. */
internal fun Char.isAsciiDigit(): Boolean = this in '0'..'9'
