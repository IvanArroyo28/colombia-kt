package io.github.ivanarroyo28.colombia.divipola

import java.text.Normalizer
import java.util.Locale

// Text helpers for DIVIPOLA names. DANE publishes them in upper case ("SAN JOSÉ DEL GUAVIARE").

/** Words written in lower case inside a Spanish place name, unless they come first. */
private val CONNECTORS = setOf("de", "del", "la", "las", "los", "y")

private val SPACES = Regex("\\s+")

/**
 * "SAN JOSÉ DEL GUAVIARE" → "San José del Guaviare", "BOGOTÁ, D.C." → "Bogotá, D.C.".
 * Abbreviations with dots (D.C.) keep their capitals.
 */
internal fun spanishTitleCase(officialName: String): String =
    officialName.trim().split(SPACES).mapIndexed { index, word ->
        val lower = word.lowercase(Locale.ROOT)
        when {
            '.' in word -> word
            index > 0 && lower in CONNECTORS -> lower
            else -> lower.replaceFirstChar { it.titlecase(Locale.ROOT) }
        }
    }.joinToString(" ")

/** Accents and other marks that NFD splits off the letters: "á" → "a" + "´". */
private val COMBINING_MARKS = Regex("\\p{Mn}+")

private val NOT_LETTER_OR_DIGIT = Regex("[^a-z0-9]+")

/**
 * Makes a name comparable with what people type: no accents, no case, no punctuation.
 * "Bogotá, D.C." → "bogota d c", "MIRITÍ - PARANÁ" → "miriti parana".
 * The ñ becomes n too, because many keyboards don't have it: "pena" finds "La Peña".
 */
internal fun normalizeForSearch(text: String): String =
    Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .replace(COMBINING_MARKS, "")
        .replace(NOT_LETTER_OR_DIGIT, " ")
        .trim()
