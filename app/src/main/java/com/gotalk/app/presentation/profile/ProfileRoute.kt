package com.gotalk.app.presentation.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gotalk.app.presentation.discover.ProfileLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Cuánto se ve un aviso ("Perfil guardado", error de foto...). */
private const val ToastMillis = 2200L

/**
 * Conecta el ViewModel con la pantalla sin estado. Aquí vive lo puramente visual (si se está editando, el aviso
 * visible) y la elección de la foto: el selector del sistema entrega una Uri temporal, [ProfilePhotoStore] la copia
 * a la app y esa copia entra al borrador como cualquier otro cambio (solo "Guardar" la vuelve definitiva).
 */
@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editing by rememberSaveable { mutableStateOf(false) }
    var toast by remember { mutableStateOf<ProfileToast?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val photoStore = remember { ProfilePhotoStore(context) }

    // Selector de fotos del sistema: no necesita permisos. Si el usuario cancela, uri es null y no pasa nada.
    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                val copy = photoStore.import(uri)
                if (copy != null) {
                    viewModel.updateDraft { it.copy(photoUri = copy) }
                } else {
                    toast = ProfileToast("No se pudo usar esa foto", isError = true)
                }
            }
        }
    }

    // "Atrás" primero cierra la edición; sin edición sigue el comportamiento de la barra (volver a Descubrir)
    BackHandler(enabled = editing) { editing = false }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(ToastMillis)
            toast = null
        }
    }

    // Borra las fotos copiadas que ya no usa ni el perfil guardado ni el borrador
    LaunchedEffect(state.profile.photoUri, state.draft.photoUri) {
        photoStore.cleanup(setOf(state.profile.photoUri, state.draft.photoUri))
    }

    ProfileScreen(
        profile = state.draft,
        editing = editing,
        errors = state.visibleErrors,
        saveEnabled = state.hasChanges,
        toast = toast,
        onToggleEditing = { editing = !editing },
        onEditPhotoClick = {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onCountryChange = { code -> viewModel.updateDraft { it.copy(countryCode = code) } },
        onNativeLanguageChange = { name -> viewModel.updateDraft { it.copy(nativeLanguage = ProfileLanguage(name)) } },
        onAboutChange = { text -> viewModel.updateDraft { it.copy(about = text) } },
        onSave = {
            if (viewModel.save()) {
                editing = false
                toast = ProfileToast("Perfil guardado")
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
