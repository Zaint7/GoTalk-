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
 */
data class DiscoverUiState(
    val profile: DiscoverProfile? = null,
    val canUndo: Boolean = false
)

@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {

    // Más adelante los perfiles vendrán de un repositorio (Firebase);
    // por ahora se toman de la lista falsa.
    private val profiles: List<DiscoverProfile> = fakeDiscoverProfiles

    // Posición dentro de la lista. Rango 0..profiles.size: el valor profiles.size
    // significa "ya se pasó el último perfil" (profile = null).
    private var index = 0

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
        canUndo = index > 0
    )
}
