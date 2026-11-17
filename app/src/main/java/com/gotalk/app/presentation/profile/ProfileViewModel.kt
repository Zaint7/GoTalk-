package com.gotalk.app.presentation.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Estado de la pestaña Perfil. [profile] es lo guardado; [draft] es el borrador que edita la pantalla.
 * Solo "Guardar" convierte el borrador en [profile].
 */
data class ProfileUiState(
    val profile: UserProfile,
    val draft: UserProfile
) {
    /** El borrador es distinto de lo guardado. */
    val hasChanges: Boolean get() = draft != profile

    /** Lo que falta para poder guardar (país e idioma nativo son obligatorios). */
    val validationErrors: Set<ProfileError> get() = draft.validate()

    /** "Guardar" está disponible: hay cambios y no faltan campos obligatorios. */
    val canSave: Boolean get() = hasChanges && validationErrors.isEmpty()
}

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    // Más adelante el perfil vendrá de un repositorio (Firebase); por ahora es de prueba.
    private var profile: UserProfile = fakeUserProfile
    private var draft: UserProfile = profile

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Todo cambio va al borrador, p. ej. `updateDraft { it.copy(city = "Sevilla") }`. No toca lo guardado. */
    fun updateDraft(transform: (UserProfile) -> UserProfile) {
        draft = transform(draft).normalized()
        publish()
    }

    /** Guarda el borrador. Devuelve false (sin cambiar nada) si faltan campos obligatorios. */
    fun save(): Boolean {
        if (draft.validate().isNotEmpty()) return false
        profile = draft
        publish()
        return true
    }

    /** Descarta los cambios: el borrador vuelve a ser lo guardado. */
    fun discardChanges() {
        draft = profile
        publish()
    }

    private fun publish() {
        _uiState.update { buildState() }
    }

    private fun buildState() = ProfileUiState(profile = profile, draft = draft)
}
