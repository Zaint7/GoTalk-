package com.gotalk.app.presentation.profile

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gotalk.app.presentation.discover.ProfileLanguage
import kotlinx.coroutines.delay

private const val TAG = "ProfileRoute"

/** Cuánto se ve el aviso "Perfil guardado". */
private const val SavedToastMillis = 2200L

/**
 * Conecta el ViewModel con la pantalla sin estado. Aquí vive solo lo puramente visual: si se está editando
 * y si se ve el aviso de guardado. El cambio de foto llega en un paso posterior.
 */
@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editing by rememberSaveable { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    // "Atrás" primero cierra la edición; sin edición sigue el comportamiento de la barra (volver a Descubrir)
    BackHandler(enabled = editing) { editing = false }

    LaunchedEffect(saved) {
        if (saved) {
            delay(SavedToastMillis)
            saved = false
        }
    }

    ProfileScreen(
        profile = state.draft,
        editing = editing,
        errors = state.visibleErrors,
        saveEnabled = state.hasChanges,
        savedVisible = saved,
        onToggleEditing = { editing = !editing },
        onEditPhotoClick = { Log.d(TAG, "Cambiar foto") },
        onCountryChange = { code -> viewModel.updateDraft { it.copy(countryCode = code) } },
        onNativeLanguageChange = { name -> viewModel.updateDraft { it.copy(nativeLanguage = ProfileLanguage(name)) } },
        onAboutChange = { text -> viewModel.updateDraft { it.copy(about = text) } },
        onSave = {
            if (viewModel.save()) {
                editing = false
                saved = true
            } else {
                // Faltan campos obligatorios: se abre la edición para que pueda completarlos
                editing = true
            }
        },
        onDiscard = {
            viewModel.discardChanges()
            editing = false
        },
        modifier = modifier
    )
}
