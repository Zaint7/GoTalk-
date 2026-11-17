package com.gotalk.app.presentation.profile

import com.gotalk.app.presentation.discover.countryNameFor
import com.gotalk.app.presentation.discover.flagForCountry
import com.gotalk.app.presentation.discover.flagForLanguage
import com.gotalk.app.ui.components.PickerOption

// Países que ofrece el selector. Solo ES, GB, FR e IT tienen bandera propia; el resto usa el logo G
// (el mismo respaldo de siempre) hasta que se agreguen sus banderas.
private val CountryCodes = listOf(
    "ES", "GB", "FR", "IT", "DE", "PT", "US", "MX", "AR", "CO", "CL", "PE", "BR", "JP", "KR"
)

// Los mismos idiomas que ofrece el registro
private val LanguageNames = listOf(
    "Español", "Inglés", "Francés", "Alemán", "Portugués", "Italiano", "Japonés", "Coreano"
)

val CountryOptions: List<PickerOption> = CountryCodes
    .map { PickerOption(key = it, label = countryNameFor(it), flagRes = flagForCountry(it)) }
    .sortedBy { it.label }

val LanguageOptions: List<PickerOption> = LanguageNames
    .map { PickerOption(key = it, label = it, flagRes = flagForLanguage(it)) }
    .sortedBy { it.label }
