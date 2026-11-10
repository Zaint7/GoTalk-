package com.gotalk.app

import com.gotalk.app.presentation.discover.DiscoverFilters
import com.gotalk.app.presentation.discover.DiscoverViewModel
import com.gotalk.app.presentation.discover.GenderFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoverViewModelFiltersTest {

    private val vm = DiscoverViewModel()
    private val state get() = vm.uiState.value

    @Test
    fun `editar el borrador no cambia el feed ni lo vigente`() {
        vm.openFilters()
        vm.updateDraft { it.copy(onlyOnline = true, gender = GenderFilter.HOMBRE) }

        assertEquals("Ángela R.", state.profile?.name)
        assertTrue(state.appliedFilters.isDefault)
        assertEquals(2, state.draftFilters.activeFiltersCount)
    }

    @Test
    fun `cerrar sin aplicar descarta el borrador y conserva lo vigente`() {
        vm.openFilters()
        vm.updateDraft { it.copy(onlyOnline = true) }
        vm.applyFilters()

        vm.openFilters()
        vm.updateDraft { it.copy(gender = GenderFilter.MUJER) }
        vm.closeFilters()

        assertFalse(state.showFilters)
        assertTrue(state.appliedFilters.onlyOnline)
        assertEquals(GenderFilter.TODOS, state.appliedFilters.gender)
        // Al reabrir, el borrador vuelve a ser copia de lo vigente
        vm.openFilters()
        assertEquals(state.appliedFilters, state.draftFilters)
    }

    @Test
    fun `aplicar filtra el feed, vuelve al primer perfil y cierra la ventana`() {
        vm.onAccept() // ya se avanzó un perfil
        vm.openFilters()
        vm.updateDraft { it.copy(onlyOnline = true) }
        vm.applyFilters()

        assertFalse(state.showFilters)
        assertEquals(1, state.activeFiltersCount)
        assertEquals("Ángela R.", state.profile?.name)
        assertFalse(state.canUndo)
        vm.onAccept()
        assertEquals("Marta S.", state.profile?.name) // Lucas está desconectado
    }

    @Test
    fun `el contador en vivo sigue al borrador`() {
        vm.openFilters()
        assertEquals(5, state.draftMatchCount)
        vm.updateDraft { it.copy(onlyOnline = true) }
        assertEquals(3, state.draftMatchCount)
        vm.resetDraft()
        assertEquals(5, state.draftMatchCount)
    }

    @Test
    fun `sin resultados se muestra el estado vacio y quitar filtros lo resuelve`() {
        vm.openFilters()
        vm.updateDraft { it.copy(countries = setOf("IT"), onlyOnline = true) }
        vm.applyFilters()

        assertTrue(state.noResults)
        assertNull(state.profile)

        vm.clearAppliedFilters()
        assertFalse(state.noResults)
        assertEquals("Ángela R.", state.profile?.name)
        assertTrue(state.appliedFilters.isDefault)
    }

    @Test
    fun `solo una ventana flotante a la vez`() {
        vm.openNotifications()
        vm.openFilters()
        assertTrue(state.showFilters)
        assertFalse(state.showNotifications)

        vm.openNotifications()
        assertTrue(state.showNotifications)
        assertFalse(state.showFilters)
    }
}
