package com.gotalk.app.presentation.discover

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Estado de Descubrir. [profile] es null cuando ya no quedan perfiles.
 * [canUndo] es false en el primer perfil (no hay nada que deshacer).
 * [notifications] alimenta la ventana de notificaciones y el badge de la campana;
 * [showNotifications] indica si esa ventana está abierta.
 *
 * Filtros: [appliedFilters] es lo que realmente filtra el feed; [draftFilters] es el borrador que edita la
 * ventana de filtros y solo pasa a ser aplicado con "Aplicar". [noResults] es true cuando los filtros
 * aplicados no dejan a nadie (distinto de "ya viste a todos").
 */
data class DiscoverUiState(
    val profile: DiscoverProfile? = null,
    val canUndo: Boolean = false,
    val notifications: List<GoTalkNotification> = emptyList(),
    val showNotifications: Boolean = false,
    val appliedFilters: DiscoverFilters = DiscoverFilters(),
    val draftFilters: DiscoverFilters = DiscoverFilters(),
    val showFilters: Boolean = false,
    val noResults: Boolean = false
) {
    /** Cuántos filtros aplicados se salen del valor por defecto. */
    val activeFiltersCount: Int get() = appliedFilters.activeFiltersCount
}

@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {

    // Más adelante los perfiles vendrán de un repositorio (Firebase);
    // por ahora se toman de la lista falsa.
    private val allProfiles: List<DiscoverProfile> = fakeDiscoverProfiles

    private var appliedFilters = DiscoverFilters()
    private var draftFilters = DiscoverFilters()
    private var showFilters = false

    // Lo que se ve en el feed: todos los perfiles filtrados con appliedFilters
    private var profiles: List<DiscoverProfile> = filterProfiles(allProfiles, appliedFilters)

    // Posición dentro de la lista. Rango 0..profiles.size: el valor profiles.size
    // significa "ya se pasó el último perfil" (profile = null).
    private var index = 0

    // Notificaciones (datos falsos por ahora)
    private var notifications: List<GoTalkNotification> = fakeNotifications
    private var showNotifications = false

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    fun onReject() = advance()

    fun onAccept() = advance()

    fun onUndo() {
        // Si se acabaron los perfiles, deshacer vuelve al último; en el primero no hace nada.
        if (index > 0) {
            index--
            publish()
        }
    }

    // Solo una ventana flotante a la vez: abrir una cierra la otra.

    fun openNotifications() {
        showNotifications = true
        showFilters = false
        publish()
    }

    fun closeNotifications() {
        showNotifications = false
        publish()
    }

    fun deleteNotification(id: Long) {
        notifications = notifications.filterNot { it.id == id }
        publish()
    }

    fun clearAllNotifications() {
        notifications = emptyList()
        publish()
    }

    /** Abre la ventana de filtros con un borrador nuevo, copia de lo aplicado. */
    fun openFilters() {
        draftFilters = appliedFilters
        showFilters = true
        showNotifications = false
        publish()
    }

    /** Cierra sin aplicar: el borrador se descarta y los filtros vigentes no cambian. */
    fun closeFilters() {
        showFilters = false
        draftFilters = appliedFilters
        publish()
    }

    /** Modifica el borrador, p. ej. `updateDraft { it.copy(onlyOnline = true) }`. No toca el feed. */
    fun updateDraft(transform: (DiscoverFilters) -> DiscoverFilters) {
        draftFilters = transform(draftFilters)
        publish()
    }

    /** El borrador pasa a ser el filtro vigente, el feed se recalcula desde el primer perfil y la ventana se cierra. */
    fun applyFilters() {
        appliedFilters = draftFilters
        refreshFeed()
        showFilters = false
        publish()
    }

    /** Deja el borrador en los valores por defecto (no aplica nada hasta tocar "Aplicar"). */
    fun resetDraft() {
        draftFilters = DiscoverFilters()
        publish()
    }

    /** "Quitar filtros" del estado vacío: quita los filtros vigentes y vuelve a mostrar todo el feed. */
    fun clearAppliedFilters() {
        appliedFilters = DiscoverFilters()
        draftFilters = appliedFilters
        refreshFeed()
        publish()
    }

    private fun refreshFeed() {
        profiles = filterProfiles(allProfiles, appliedFilters)
        index = 0
    }

    private fun advance() {
        if (index < profiles.size) {
            index++
            publish()
        }
    }

    private fun publish() {
        _uiState.update { buildState() }
    }

    private fun buildState() = DiscoverUiState(
        profile = profiles.getOrNull(index),
        canUndo = index > 0,
        notifications = notifications,
        showNotifications = showNotifications,
        appliedFilters = appliedFilters,
        draftFilters = draftFilters,
        showFilters = showFilters,
        noResults = profiles.isEmpty()
    )
}
