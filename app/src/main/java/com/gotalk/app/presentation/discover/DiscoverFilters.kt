package com.gotalk.app.presentation.discover

/** Rango de edad por defecto: sin restricción. */
val DefaultAgeRange: IntRange = 18..100

/** Opciones del filtro de género. [TODOS] significa "sin restricción". */
enum class GenderFilter(val label: String) {
    TODOS("Todos"),
    MUJER("Mujer"),
    HOMBRE("Hombre"),
    OTRO("Otro")
}

/**
 * Filtros de Descubrir (inmutable). Cada campo con su valor por defecto significa "cualquiera":
 * rango 18..100, género [GenderFilter.TODOS], conjuntos vacíos, sin idioma que aprende y sin "solo en línea".
 *
 * Los idiomas se guardan por nombre ("Español") y los países por código ISO ("ES").
 */
data class DiscoverFilters(
    val ageRange: IntRange = DefaultAgeRange,
    val gender: GenderFilter = GenderFilter.TODOS,
    val languagesTheySpeak: Set<String> = emptySet(),
    val languageTheyLearn: String? = null,
    val countries: Set<String> = emptySet(),
    val onlyOnline: Boolean = false
) {
    /** Cuántos filtros se salen del valor por defecto. */
    val activeFiltersCount: Int
        get() = listOf(
            ageRange != DefaultAgeRange,
            gender != GenderFilter.TODOS,
            languagesTheySpeak.isNotEmpty(),
            languageTheyLearn != null,
            countries.isNotEmpty(),
            onlyOnline
        ).count { it }

    /** True cuando no hay ningún filtro activo. */
    val isDefault: Boolean
        get() = activeFiltersCount == 0
}

/**
 * Deja solo los perfiles que cumplen todos los [filters]. Función pura: no toca estado, es fácil de probar.
 * Un filtro vacío o por defecto no descarta a nadie. En los idiomas que habla basta con que coincida uno.
 */
fun filterProfiles(profiles: List<DiscoverProfile>, filters: DiscoverFilters): List<DiscoverProfile> {
    if (filters.isDefault) return profiles
    val speak = filters.languagesTheySpeak.map { it.normalized() }.toSet()
    val learn = filters.languageTheyLearn?.normalized()
    val countries = filters.countries.map { it.uppercase() }.toSet()

    return profiles.filter { profile ->
        profile.age in filters.ageRange &&
            genderMatches(filters.gender, profile.gender) &&
            (speak.isEmpty() || profile.spokenLanguages.any { it.name.normalized() in speak }) &&
            (learn == null || profile.learning.name.normalized() == learn) &&
            (countries.isEmpty() || profile.countryCode.uppercase() in countries) &&
            (!filters.onlyOnline || profile.isOnline)
    }
}

private fun genderMatches(filter: GenderFilter, gender: Gender): Boolean = when (filter) {
    GenderFilter.TODOS -> true
    GenderFilter.MUJER -> gender == Gender.MUJER
    GenderFilter.HOMBRE -> gender == Gender.HOMBRE
    GenderFilter.OTRO -> gender == Gender.OTRO
}

private fun String.normalized() = trim().lowercase()
