package io.github.ivanarroyo28.colombia.internal

// Small helpers shared by several parts of the library. "internal" = not part of the public API.

/** "900373115" → "900.373.115": groups of three digits from the right, the Colombian way. */
internal fun groupThousands(digits: String): String =
    digits.reversed().chunked(3).joinToString(".").reversed()

/** Removes the separators people usually type in ID numbers: dots, commas and spaces. */
internal fun stripSeparators(text: String): String =
    text.filterNot { it == '.' || it == ',' || it.isWhitespace() }

/** Only 0–9: [Char.isDigit] would also accept digits from other scripts, like Arabic ones. */
internal fun Char.isAsciiDigit(): Boolean = this in '0'..'9'
