package io.github.ivanarroyo28.colombia.documents

import io.github.ivanarroyo28.colombia.internal.groupThousands
import io.github.ivanarroyo28.colombia.internal.isAsciiDigit
import io.github.ivanarroyo28.colombia.internal.stripSeparators

/**
 * Colombian citizenship card number ("cédula de ciudadanía").
 *
 * Cédulas have **no check digit**, so this only validates the shape of the number
 * (digits, length, no leading zero). Whether a cédula exists can only be checked with the Registraduría.
 *
 * ```
 * Cedula.isValid("1.234.567.890") // true
 * Cedula.format("1234567890")     // "1.234.567.890"
 * ```
 */
public object Cedula {

    /** Very old cédulas can be short. */
    public const val MIN_LENGTH: Int = 3

    /** Cédulas issued today have 10 digits. */
    public const val MAX_LENGTH: Int = 10

    /** True if [cedula] has 3 to 10 digits and does not start with 0. Dots and spaces are ignored. */
    @JvmStatic
    public fun isValid(cedula: String): Boolean {
        val digits = stripSeparators(cedula)
        return digits.length in MIN_LENGTH..MAX_LENGTH &&
            digits.all { it.isAsciiDigit() } &&
            digits[0] != '0'
    }

    /**
     * `"1234567890"` → `"1.234.567.890"`.
     *
     * @throws IllegalArgumentException if [cedula] is not valid.
     */
    @JvmStatic
    public fun format(cedula: String): String {
        require(isValid(cedula)) { "Not a valid cédula: \"$cedula\"" }
        return groupThousands(stripSeparators(cedula))
    }
}
