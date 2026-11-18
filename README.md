# colombia-kt 🇨🇴

[![CI](https://github.com/IvanArroyo28/colombia-kt/actions/workflows/ci.yml/badge.svg)](https://github.com/IvanArroyo28/colombia-kt/actions/workflows/ci.yml)
![Kotlin](https://img.shields.io/badge/Kotlin-2.3-7F52FF?logo=kotlin&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)

**Kotlin utilities for Colombia**: public holidays, business days, NIT, pesos and DIVIPOLA.
Pure Kotlin with no dependencies, so it works on **Android** (minSdk 26+) and on the **server** (Ktor, Spring…).

> 🚧 **Under construction.** The first version (v0.1.0) is on its way. *[Leer en español ↓](#español)*

## Roadmap

- [x] **Public holidays**: Ley 51 de 1983 (Ley Emiliani), Easter-based holidays and the new 9 July holiday from Ley 2578 de 2026. Every rule knows the year it took effect.
- [x] **Business days**: is it a business day, add N business days, count business days between two dates.
- [x] **NIT and ID documents**: DIAN verification digit, validation and formatting (`900.373.115-3`).
- [x] **Pesos**: format and parse COP amounts, and write amounts in words ("un millón doscientos mil pesos").
- [x] **DIVIPOLA**: the 33 departments and 1,122 municipalities and areas with their official DANE codes, coordinates and accent-insensitive search.

## Usage

### Public holidays

```kotlin
import io.github.ivanarroyo28.colombia.holidays.ColombianHolidays
import io.github.ivanarroyo28.colombia.holidays.isColombianHoliday

ColombianHolidays.isHoliday(LocalDate.of(2026, 7, 13))   // true: Chiquinquirá, moved to Monday
LocalDate.of(2026, 7, 9).isColombianHoliday()             // false: celebration day, not a day off

ColombianHolidays.holidaysOf(2026).size                   // 19
ColombianHolidays.nextHoliday()                           // next holiday from today, in Colombian time

val sanJose = ColombianHolidays.holidayOn(LocalDate.of(2025, 3, 24))!!
sanJose.name             // "Día de San José"
sanJose.celebrationDate  // 2025-03-19 (Wednesday)
sanJose.isMoved          // true: Ley Emiliani moved it to Monday
```

### Business days

```kotlin
import io.github.ivanarroyo28.colombia.businessdays.BusinessCalendar
import io.github.ivanarroyo28.colombia.businessdays.plusBusinessDays

val calendar = BusinessCalendar.MONDAY_TO_FRIDAY

calendar.isBusinessDay(LocalDate.of(2026, 11, 16))                 // false: holiday Monday
calendar.addBusinessDays(LocalDate.of(2026, 12, 18), 10)           // 2027-01-05: skips Christmas and New Year
calendar.businessDaysIn(YearMonth.of(2026, 11)).size               // 19
LocalDate.of(2026, 11, 13).plusBusinessDays(1)                     // 2026-11-17

// Your own rules: Saturdays worked and the company closed on December 24
val store = BusinessCalendar(
    workingDays = DayOfWeek.entries.toSet() - DayOfWeek.SUNDAY,
    extraDaysOff = setOf(LocalDate.of(2026, 12, 24)),
)
```

### NIT and ID documents

```kotlin
import io.github.ivanarroyo28.colombia.documents.Cedula
import io.github.ivanarroyo28.colombia.documents.DocumentType
import io.github.ivanarroyo28.colombia.documents.Nit

Nit.verificationDigit("900373115")   // 3
Nit.format("900373115")              // "900.373.115-3"
Nit.isValid("800.197.268-4")         // true (the DIAN's own NIT)
Nit.isValid("800.197.268-5")         // false: wrong verification digit

Cedula.isValid("1.234.567.890")      // true (shape only: cédulas have no check digit)
DocumentType.fromDianCode("13")      // CITIZENSHIP_CARD, "Cédula de ciudadanía"
```

### Pesos

```kotlin
import io.github.ivanarroyo28.colombia.pesos.Pesos

Pesos.format(1234567)                              // "$ 1.234.567"
Pesos.format(BigDecimal("1500.5"), decimals = 2)   // "$ 1.500,50"
Pesos.parse("$ 1.234.567,50")                      // 1234567.50
Pesos.parse("1.5")                                 // null: ambiguous, never a wrong number

Pesos.toWords(1200000)                             // "un millón doscientos mil pesos"
Pesos.toWords(BigDecimal("21500.50"))              // "veintiún mil quinientos pesos con cincuenta centavos"
Pesos.toWords(1000000, mcte = true).uppercase()    // "UN MILLÓN DE PESOS M/CTE"
```

### DIVIPOLA (departments and municipalities)

```kotlin
import io.github.ivanarroyo28.colombia.divipola.Divipola

Divipola.municipality("13001")?.fullName      // "Cartagena de Indias, Bolívar"
Divipola.department("05")?.name               // "Antioquia"
Divipola.municipalitiesOf("91").size          // 11 (Amazonas, with its non-municipalized areas)

Divipola.search("bogota").first().name        // "Bogotá, D.C."
Divipola.search("la union").map { it.fullName }
// ["La Unión, Antioquia", "La Unión, Nariño", "La Unión, Sucre", "La Unión, Valle del Cauca"]
```

Names come in normal capitalization ("San José del Guaviare") and also exactly as DANE writes them (`officialName`).
To refresh the data when DANE publishes changes, run `./gradlew updateDivipola`.

Edge cases handled: two holidays on the same day (June 30, 2025), holidays added by law from a given year (9 July, since 2026), and dates computed in Colombian time even on servers in UTC.

## Installation

Coming soon via JitPack and Maven Central.

## License

Code: [MIT](LICENSE) © Ivan Arroyo

DIVIPOLA data (`src/main/resources/.../divipola/`): © DANE, *División Político-Administrativa de Colombia*,
taken from [datos.gov.co](https://www.datos.gov.co/d/gdxc-w37w) and licensed under
[CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/). The files were reformatted
(tab-separated, coordinates unchanged). If you redistribute that data, keep the credit and the same license.

---

## Español

**Utilidades en Kotlin para Colombia**: festivos, días hábiles, NIT, pesos y DIVIPOLA.
Kotlin puro y sin dependencias: funciona en **Android** (minSdk 26+) y en **servidores** (Ktor, Spring…).

> 🚧 **En construcción.** La primera versión (v0.1.0) viene en camino.

### Qué incluirá

- ✅ **Festivos**: Ley Emiliani (Ley 51 de 1983), festivos que dependen de la Pascua y el nuevo festivo del 9 de julio (Ley 2578 de 2026). Cada regla sabe desde qué año aplica.
- ✅ **Días hábiles**: saber si un día es hábil, sumar N días hábiles, contar días hábiles entre dos fechas.
- ✅ **NIT y documentos**: dígito de verificación de la DIAN, validación y formato (`900.373.115-3`).
- ✅ **Pesos**: formato y lectura de montos en COP, y montos en letras ("un millón doscientos mil pesos").
- ✅ **DIVIPOLA**: los 33 departamentos y 1.122 municipios y áreas con sus códigos oficiales del DANE, coordenadas y búsqueda sin importar tildes.

Los datos de DIVIPOLA son del DANE (datos.gov.co) y tienen licencia CC BY-SA 4.0; el código de la librería es MIT.
