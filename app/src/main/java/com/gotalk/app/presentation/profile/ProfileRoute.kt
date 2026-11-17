package com.gotalk.app.presentation.profile

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val TAG = "ProfileRoute"

/** Conecta el ViewModel con la pantalla sin estado. La edición (lápices) llega en el paso siguiente. */
@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        profile = state.draft,
        canSave = state.canSave,
        onEditClick = { Log.d(TAG, "Editar perfil") },
        onEditPhotoClick = { Log.d(TAG, "Cambiar foto") },
        onSave = { viewModel.save() },
        modifier = modifier
    )
}
