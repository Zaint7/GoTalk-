package com.gotalk.app

import com.gotalk.app.presentation.discover.DiscoverFilters
import com.gotalk.app.presentation.discover.GenderFilter
import com.gotalk.app.presentation.discover.fakeDiscoverProfiles
import com.gotalk.app.presentation.discover.filterProfiles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoverFiltersTest {

    private val all = fakeDiscoverProfiles

    private fun names(filters: DiscoverFilters) = filterProfiles(all, filters).map { it.name }

    @Test
    fun `sin filtros no descarta a nadie`() {
        assertEquals(all, filterProfiles(all, DiscoverFilters()))
        assertTrue(DiscoverFilters().isDefault)
        assertEquals(0, DiscoverFilters().activeFiltersCount)
    }

    @Test
    fun `rango de edad incluye los extremos`() {
        assertEquals(listOf("Lucas M.", "Giulia B."), names(DiscoverFilters(ageRange = 24..26)))
    }

    @Test
    fun `genero filtra y Todos no`() {
        assertEquals(listOf("Lucas M.", "Pierre D."), names(DiscoverFilters(gender = GenderFilter.HOMBRE)))
        assertEquals(all.size, filterProfiles(all, DiscoverFilters(gender = GenderFilter.TODOS)).size)
    }

    @Test
    fun `idiomas que habla basta con que coincida uno`() {
        val result = names(DiscoverFilters(languagesTheySpeak = setOf("francés", "Inglés")))
        assertEquals(listOf("Lucas M.", "Marta S.", "Pierre D.", "Giulia B."), result)
    }

    @Test
    fun `idioma que aprende`() {
        assertEquals(listOf("Lucas M.", "Pierre D."), names(DiscoverFilters(languageTheyLearn = "Español")))
    }

    @Test
    fun `paises y solo en linea se combinan`() {
        assertEquals(listOf("Ángela R.", "Marta S."), names(DiscoverFilters(countries = setOf("es"), onlyOnline = true)))
        assertEquals(listOf("Ángela R.", "Marta S.", "Pierre D."), names(DiscoverFilters(onlyOnline = true)))
    }

    @Test
    fun `filtros que no dejan a nadie devuelven lista vacia`() {
        assertTrue(names(DiscoverFilters(countries = setOf("IT"), onlyOnline = true)).isEmpty())
    }

    @Test
    fun `cuenta solo los filtros fuera del valor por defecto`() {
        val filters = DiscoverFilters(
            ageRange = 20..30,
            gender = GenderFilter.MUJER,
            languageTheyLearn = "Inglés"
        )
        assertEquals(3, filters.activeFiltersCount)
    }
}
