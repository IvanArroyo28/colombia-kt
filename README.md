# colombia-kt 🇨🇴

[![CI](https://github.com/IvanArroyo28/colombia-kt/actions/workflows/ci.yml/badge.svg)](https://github.com/IvanArroyo28/colombia-kt/actions/workflows/ci.yml)
![Kotlin](https://img.shields.io/badge/Kotlin-2.3-7F52FF?logo=kotlin&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)

**Kotlin utilities for Colombia**: public holidays, business days, NIT, pesos and DIVIPOLA.
Pure Kotlin with no dependencies, so it works on **Android** (minSdk 26+) and on the **server** (Ktor, Spring…).

> 🚧 **Under construction.** The first version (v0.1.0) is on its way. *[Leer en español ↓](#español)*

## Roadmap

- [ ] **Public holidays**: Ley 51 de 1983 (Ley Emiliani), Easter-based holidays and the new 9 July holiday from Ley 2578 de 2026. Every rule knows the year it took effect.
- [ ] **Business days**: is it a business day, add N business days, count business days between two dates.
- [ ] **NIT and ID documents**: DIAN verification digit, validation and formatting (`900.373.115-3`).
- [ ] **Pesos**: format and parse COP amounts, and write amounts in words ("un millón doscientos mil pesos").
- [ ] **DIVIPOLA**: departments and municipalities with their official DANE codes.

## Installation

Coming soon via JitPack and Maven Central.

## License

[MIT](LICENSE) © Ivan Arroyo

---

## Español

**Utilidades en Kotlin para Colombia**: festivos, días hábiles, NIT, pesos y DIVIPOLA.
Kotlin puro y sin dependencias: funciona en **Android** (minSdk 26+) y en **servidores** (Ktor, Spring…).

> 🚧 **En construcción.** La primera versión (v0.1.0) viene en camino.

### Qué incluirá

- **Festivos**: Ley Emiliani (Ley 51 de 1983), festivos que dependen de la Pascua y el nuevo festivo del 9 de julio (Ley 2578 de 2026). Cada regla sabe desde qué año aplica.
- **Días hábiles**: saber si un día es hábil, sumar N días hábiles, contar días hábiles entre dos fechas.
- **NIT y documentos**: dígito de verificación de la DIAN, validación y formato (`900.373.115-3`).
- **Pesos**: formato y lectura de montos en COP, y montos en letras ("un millón doscientos mil pesos").
- **DIVIPOLA**: departamentos y municipios con sus códigos oficiales del DANE.
