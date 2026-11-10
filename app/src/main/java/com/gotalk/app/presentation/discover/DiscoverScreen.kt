package com.gotalk.app.presentation.discover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.components.ActionButtonsRow
import com.gotalk.app.ui.components.GoTalkBanner
import com.gotalk.app.ui.components.ProfileCard
import com.gotalk.app.ui.components.SwipeDirection
import com.gotalk.app.ui.components.SwipeWaveOverlay
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    // exit: 0 = tarjeta en reposo, 1 = ya salió volando. enter: entrada suave de la tarjeta siguiente
    val exit = remember { Animatable(0f) }
    val enter = remember { Animatable(1f) }
    var swipeDirection by remember { mutableStateOf(SwipeDirection.Accept) }
    var swiping by remember { mutableStateOf(false) }
    var waveId by remember { mutableIntStateOf(0) }
    var waveActive by remember { mutableStateOf(false) }

    // Único punto de entrada de la animación: lo usan los botones y, si algún día existe, el gesto de swipe.
    // La lógica real (onAccept / onReject) se llama al terminar de salir la tarjeta.
    fun triggerSwipe(direction: SwipeDirection) {
        if (swiping || profile == null) return
        swiping = true
        swipeDirection = direction
        waveId++
        waveActive = true
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        scope.launch {
            exit.animateTo(1f, tween(durationMillis = 380, easing = FastOutSlowInEasing))
            if (direction == SwipeDirection.Accept) onAccept() else onReject()
            // La tarjeta siguiente entra con scale 0.95 -> 1 y fade in
            exit.snapTo(0f)
            enter.snapTo(0f)
            enter.animateTo(1f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
            swiping = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
                        .graphicsLayer {
                            val dir = swipeDirection.sign
                            val e = exit.value
                            val n = enter.value
                            translationX = dir * e * size.width * 1.3f
                            rotationZ = dir * 10f * e
                            val s = 0.95f + 0.05f * n
                            scaleX = s
                            scaleY = s
                            alpha = (1f - e) * n
                        }
                )
                ActionButtonsRow(
                    onReject = { triggerSwipe(SwipeDirection.Reject) },
                    onMessage = onMessage,
                    onAccept = { triggerSwipe(SwipeDirection.Accept) },
                    enabled = !swiping
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
        // La ola solo existe mientras dura la animación; al terminar se quita de la composición
        if (waveActive) {
            key(waveId) {
                SwipeWaveOverlay(
                    direction = swipeDirection,
                    onFinished = { waveActive = false }
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
