package com.gotalk.app.presentation.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.components.ActionButtonsRow
import com.gotalk.app.ui.components.GoTalkBanner
import com.gotalk.app.ui.components.ProfileCard
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.Nunito

/**
 * Pantalla Descubrir, sin estado: recibe el perfil actual y las lambdas.
 * La barra inferior no vive aquí (la pone MainScaffold) y el banner ya maneja los insets
 * de la barra de estado.
 */
@Composable
fun DiscoverScreen(
    profile: DiscoverProfile?,
    canUndo: Boolean,
    onFilters: () -> Unit,
    onNotifications: () -> Unit,
    onUndo: () -> Unit,
    onMore: () -> Unit,
    onReject: () -> Unit,
    onMessage: () -> Unit,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GoTalkCream)
    ) {
        GoTalkBanner(
            onFiltersClick = onFilters,
            onNotificationsClick = onNotifications
        )

        if (profile != null) {
            ProfileCard(
                name = profile.name,
                age = profile.age,
                photoRes = profile.photoRes,
                countryFlagRes = flagForCountry(profile.countryCode),
                nativeLanguageLabel = "${profile.nativeLanguage} nativo",
                nativeLanguageFlagRes = flagForLanguage(profile.nativeLanguage),
                learningLanguageLabel = "Aprendiendo ${profile.learningLanguage.lowercase()}",
                learningLanguageFlagRes = flagForLanguage(profile.learningLanguage),
                onUndoClick = onUndo,
                onMoreClick = onMore,
                undoEnabled = canUndo,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 240.dp)
                    .padding(horizontal = 18.dp)
            )
            ActionButtonsRow(
                onReject = onReject,
                onMessage = onMessage,
                onAccept = onAccept
            )
        } else {
            // Sin más perfiles: mensaje simple (el estado vacío real llega con el backend)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay más perfiles por ahora",
                    color = GoTalkInk,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun DiscoverScreenPreview() {
    DiscoverScreen(
        profile = fakeDiscoverProfiles[0],
        canUndo = true,
        onFilters = {}, onNotifications = {}, onUndo = {}, onMore = {},
        onReject = {}, onMessage = {}, onAccept = {}
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun DiscoverScreenSecondProfilePreview() {
    DiscoverScreen(
        profile = fakeDiscoverProfiles[1],
        canUndo = false,
        onFilters = {}, onNotifications = {}, onUndo = {}, onMore = {},
        onReject = {}, onMessage = {}, onAccept = {}
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun DiscoverScreenThirdProfilePreview() {
    DiscoverScreen(
        profile = fakeDiscoverProfiles[2],
        canUndo = true,
        onFilters = {}, onNotifications = {}, onUndo = {}, onMore = {},
        onReject = {}, onMessage = {}, onAccept = {}
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun DiscoverScreenSmallPreview() {
    DiscoverScreen(
        profile = fakeDiscoverProfiles[0],
        canUndo = true,
        onFilters = {}, onNotifications = {}, onUndo = {}, onMore = {},
        onReject = {}, onMessage = {}, onAccept = {}
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun DiscoverScreenEmptyPreview() {
    DiscoverScreen(
        profile = null,
        canUndo = true,
        onFilters = {}, onNotifications = {}, onUndo = {}, onMore = {},
        onReject = {}, onMessage = {}, onAccept = {}
    )
}
