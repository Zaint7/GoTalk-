package com.gotalk.app.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.gotalk.app.presentation.discover.DiscoverRoute
import com.gotalk.app.presentation.discover.DiscoverScreen
import com.gotalk.app.presentation.discover.fakeDiscoverProfiles
import com.gotalk.app.ui.components.BottomBarItem
import com.gotalk.app.ui.components.GoTalkBottomBar
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.Nunito

/**
 * Contenedor principal con la barra inferior. Muestra la pantalla de la pestaña activa.
 * [discoverContent] permite sustituir Descubrir (por ejemplo en previews, sin Hilt).
 */
@Composable
fun MainScaffold(
    modifier: Modifier = Modifier,
    discoverContent: @Composable (Modifier) -> Unit = { DiscoverRoute(modifier = it) }
) {
    var selectedName by rememberSaveable { mutableStateOf(BottomBarItem.DISCOVER.name) }
    val selected = BottomBarItem.valueOf(selectedName)

    // En otra pestaña, "atrás" vuelve a Descubrir; en Descubrir sigue el comportamiento normal
    BackHandler(enabled = selected != BottomBarItem.DISCOVER) {
        selectedName = BottomBarItem.DISCOVER.name
    }

    Scaffold(
        modifier = modifier,
        containerColor = GoTalkCream,
        // Sin insets del sistema: el banner maneja la barra de estado y la barra inferior
        // maneja la de navegación.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            GoTalkBottomBar(
                selected = selected,
                onSelect = { selectedName = it.name }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())

        when (selected) {
            BottomBarItem.DISCOVER -> discoverContent(contentModifier)
            BottomBarItem.COMMUNITY -> PlaceholderTab("Comunidad", contentModifier)
            BottomBarItem.CHATS -> PlaceholderTab("Chats", contentModifier)
            BottomBarItem.PROFILE -> PlaceholderTab("Perfil", contentModifier)
        }
    }
}

@Composable
private fun PlaceholderTab(title: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = title,
            color = GoTalkInk,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MainScaffoldPreview() {
    MainScaffold(
        discoverContent = { contentModifier ->
            DiscoverScreen(
                profile = fakeDiscoverProfiles[0],
                canUndo = true,
                onFilters = {}, onNotifications = {}, onUndo = {}, onMore = {},
                onReject = {}, onMessage = {}, onAccept = {},
                modifier = contentModifier
            )
        }
    )
}
