package io.github.ivanarroyo28.colombia.documents

/** "900373115" → "900.373.115": groups of three digits from the right, the Colombian way. */
internal fun groupThousands(digits: String): String =
    digits.reversed().chunked(3).joinToString(".").reversed()

/** Removes the separators people usually type in ID numbers: dots, commas and spaces. */
internal fun stripSeparators(text: String): String =
    text.filterNot { it == '.' || it == ',' || it.isWhitespace() }
