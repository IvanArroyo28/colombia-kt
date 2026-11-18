# Changelog

All notable changes to this project are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses [Semantic Versioning](https://semver.org/).

## [0.1.0] - 2026-11-18

First release.

### Added

- **Public holidays** (`holidays`): all Colombian holidays from 1984 on, following Ley 51 de 1983 (Ley Emiliani),
  Easter-based holidays and the 9 July holiday added by Ley 2578 de 2026. Holidays moved to Monday keep their
  original celebration date.
- **Business days** (`businessdays`): `BusinessCalendar` to check business days, add or subtract N business days
  and count them between two dates, with custom working days and extra days off.
- **NIT and ID documents** (`documents`): DIAN verification digit, validation and formatting of NIT,
  cédula validation and DIAN document type codes.
- **Pesos** (`pesos`): format and strictly parse COP amounts, and write amounts in Spanish words.
- **DIVIPOLA** (`divipola`): the 33 departments and 1,122 municipalities and areas with their DANE codes,
  coordinates and accent-insensitive search. Data from DANE (datos.gov.co), CC BY-SA 4.0.

[0.1.0]: https://github.com/IvanArroyo28/colombia-kt/releases/tag/v0.1.0
