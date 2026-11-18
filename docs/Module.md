# Module colombia-kt

Kotlin utilities for Colombia: public holidays, business days, NIT and ID documents, pesos (COP) and DIVIPOLA.

Pure Kotlin with no dependencies. It works on Android (minSdk 26+) and on the JVM (Java 11+).

```kotlin
implementation("io.github.ivanarroyo28:colombia-kt:0.1.0")
```

Source code, changelog and examples: [github.com/IvanArroyo28/colombia-kt](https://github.com/IvanArroyo28/colombia-kt)

# Package io.github.ivanarroyo28.colombia

Shared helpers, such as Colombia's time zone (`America/Bogota`) and today's date in Colombian time.

# Package io.github.ivanarroyo28.colombia.holidays

Colombian public holidays from 1984 on: Ley 51 de 1983 (Ley Emiliani), Easter-based holidays and the
9 July holiday added by Ley 2578 de 2026. Start with [ColombianHolidays].

# Package io.github.ivanarroyo28.colombia.businessdays

Business day calculations that skip weekends and Colombian holidays. Start with [BusinessCalendar].

# Package io.github.ivanarroyo28.colombia.documents

NIT verification digit (DIAN), cédula validation and DIAN document type codes.

# Package io.github.ivanarroyo28.colombia.pesos

Formatting, strict parsing and amounts in Spanish words for Colombian pesos (COP).

# Package io.github.ivanarroyo28.colombia.divipola

Colombia's departments and municipalities with their official DANE codes (DIVIPOLA).
Data: DANE, datos.gov.co, licensed CC BY-SA 4.0. Start with [Divipola].
