package com.gotalk.app.presentation.discover

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val TAG = "DiscoverRoute"

/**
 * Conecta el ViewModel con la pantalla sin estado. Las acciones que todavía no tienen
 * pantalla (Filtros, Notificaciones, Mensaje, Ver más) solo dejan un log.
 */
@Composable
fun DiscoverRoute(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverScreen(
        profile = state.profile,
        canUndo = state.canUndo,
        onFilters = { Log.d(TAG, "Filtros") },
        notificationCount = state.notifications.size,
        onNotifications = viewModel::openNotifications,
        onUndo = viewModel::onUndo,
        onMore = { Log.d(TAG, "Ver más") },
        onReject = viewModel::onReject,
        onMessage = { Log.d(TAG, "Mensaje") },
        onAccept = viewModel::onAccept,
        modifier = modifier
    )
}
