package com.gotalk.app

import com.gotalk.app.presentation.profile.CountryOptions
import com.gotalk.app.presentation.profile.LanguageOptions
import com.gotalk.app.ui.components.filterPickerOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchPickerFilterTest {

    @Test
    fun emptyQueryReturnsEverything() {
        assertEquals(LanguageOptions, filterPickerOptions(LanguageOptions, ""))
        assertEquals(LanguageOptions, filterPickerOptions(LanguageOptions, "   "))
    }

    @Test
    fun ignoresAccentsAndCase() {
        assertEquals(listOf("Inglés"), filterPickerOptions(LanguageOptions, "INGLES").map { it.label })
        assertEquals(listOf("Perú"), filterPickerOptions(CountryOptions, "peru").map { it.label })
    }

    @Test
    fun matchesAnywhereInTheName() {
        val labels = filterPickerOptions(CountryOptions, "unid").map { it.label }
        assertTrue("Reino Unido" in labels && "Estados Unidos" in labels)
    }

    @Test
    fun noMatchReturnsEmpty() {
        assertTrue(filterPickerOptions(CountryOptions, "zzz").isEmpty())
    }

    @Test
    fun catalogsAreSortedAndHaveUniqueKeys() {
        assertEquals(CountryOptions.map { it.label }.sorted(), CountryOptions.map { it.label })
        assertEquals(CountryOptions.size, CountryOptions.map { it.key }.toSet().size)
        assertEquals(LanguageOptions.size, LanguageOptions.map { it.key }.toSet().size)
    }
}
