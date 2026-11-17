package com.gotalk.app.presentation.discover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import com.gotalk.app.ui.theme.PillShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.components.ActionButtonsRow
import com.gotalk.app.ui.components.DetailLanguage
import com.gotalk.app.ui.components.GoTalkBanner
import com.gotalk.app.ui.components.GoTalkPillButton
import com.gotalk.app.ui.theme.GoTalkMuted
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import com.gotalk.app.ui.components.ProfileCard
import com.gotalk.app.ui.components.SwipeDirection
import com.gotalk.app.ui.components.SwipeIndicator
import com.gotalk.app.ui.components.SwipeWaveOverlay
import com.gotalk.app.ui.components.rememberSwipeDragState
import com.gotalk.app.ui.components.resolveSwipeDirection
import com.gotalk.app.ui.components.swipeDrag
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.ProfileCardShape

/** Cuánto se ve "¡Enviado!" antes de que se cierre la ventana de mensaje. */
private const val SentVisibleMillis = 900L

/** Rotación máxima (grados) de la tarjeta al arrastrarla. */
private const val MaxDragRotation = 12f

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
    modifier: Modifier = Modifier,
    notifications: List<GoTalkNotification> = emptyList(),
    showNotifications: Boolean = false,
    onCloseNotifications: () -> Unit = {},
    onDeleteNotification: (Long) -> Unit = {},
    onClearNotifications: () -> Unit = {},
    noResults: Boolean = false,
    onClearFilters: () -> Unit = {},
    draftFilters: DiscoverFilters = DiscoverFilters(),
    showFilters: Boolean = false,
    onCloseFilters: () -> Unit = {},
    onUpdateDraft: ((DiscoverFilters) -> DiscoverFilters) -> Unit = {},
    onResetDraft: () -> Unit = {},
    onApplyFilters: () -> Unit = {},
    activeFiltersCount: Int = 0,
    draftMatchCount: Int? = null,
    showMessageComposer: Boolean = false,
    messageDraft: String = "",
    messageCharsLeft: Int = MaxMessageLength,
    messageSendState: MessageSendState = MessageSendState.Idle,
    canSendMessage: Boolean = false,
    onMessageDraftChange: (String) -> Unit = {},
    onSendMessage: () -> Unit = {},
    onCloseMessageComposer: () -> Unit = {},
    profileAlreadyMessaged: Boolean = false
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
    // El panel de detalles siempre empieza cerrado al cambiar de perfil
    var detailsOpen by remember(profile) { mutableStateOf(false) }
    // Arrastre con el dedo: se reinicia al cambiar de tarjeta
    val dragState = rememberSwipeDragState(profile)

    // Único punto de entrada de la animación: lo usan los botones y, si algún día existe, el gesto de swipe.
    // La lógica real (onAccept / onReject) se llama al terminar de salir la tarjeta.
    fun triggerSwipe(direction: SwipeDirection) {
        if (swiping || detailsOpen || showMessageComposer || profile == null) return
        swiping = true
        swipeDirection = direction
        waveId++
        waveActive = true
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        scope.launch {
            exit.animateTo(1f, tween(durationMillis = 380, easing = FastOutSlowInEasing))
            // Si venía de un arrastre, la tarjeta ya es invisible (alpha 0): se centra el offset del dedo sin que se note
            dragState.snapToCenter()
            if (direction == SwipeDirection.Accept) onAccept() else onReject()
            // La tarjeta siguiente entra con scale 0.95 -> 1 y fade in
            exit.snapTo(0f)
            enter.snapTo(0f)
            enter.animateTo(1f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
            swiping = false
        }
    }

    // Mensaje enviado: un toque de háptico, se deja ver "¡Enviado!" un instante y la ventana se cierra sola
    val currentOnClose by rememberUpdatedState(onCloseMessageComposer)
    // Enviar un mensaje también da like: se marca aquí y se dispara cuando la ventana ya está cerrada (ver más abajo)
    var likeAfterMessage by remember { mutableStateOf(false) }
    LaunchedEffect(messageSendState) {
        when (messageSendState) {
            MessageSendState.Sent -> {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(SentVisibleMillis)
                likeAfterMessage = true
                currentOnClose()
            }
            // Un toque corto también avisa del fallo, sin esperar a que se lea el mensaje
            MessageSendState.Failed -> haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            else -> Unit
        }
    }

    // Mismo punto de entrada que los botones y el gesto: la ola verde, la salida de la tarjeta y onAccept().
    // triggerSwipe no actúa con el redactor abierto, así que se espera a que el estado ya lo dé por cerrado.
    val currentTriggerSwipe by rememberUpdatedState<(SwipeDirection) -> Unit> { triggerSwipe(it) }
    LaunchedEffect(likeAfterMessage, showMessageComposer) {
        if (likeAfterMessage && !showMessageComposer) {
            likeAfterMessage = false
            currentTriggerSwipe(SwipeDirection.Accept)
        }
    }

    // Al soltar el dedo: si se supera el umbral (o hay fling) entra por el MISMO punto que los botones.
    // rememberUpdatedState evita que el gesto use una copia vieja de triggerSwipe (swiping, detailsOpen, profile).
    val onDragRelease by rememberUpdatedState<(Float, Float) -> Boolean> { velocityDp, width ->
        val direction = resolveSwipeDirection(dragState.x.value, velocityDp, width)
        if (direction != null && !swiping && !detailsOpen && !showMessageComposer && profile != null) {
            triggerSwipe(direction)
            true
        } else {
            false
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
                onNotificationsClick = onNotifications,
                hasNotifications = notifications.isNotEmpty(),
                notificationCount = notifications.size,
                activeFiltersCount = activeFiltersCount
            )

            if (profile != null) {
                // La ola vive dentro de este Box: cubre solo el área de la tarjeta, recortada con su forma
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 240.dp)
                        .padding(horizontal = 18.dp)
                ) {
                    // Capa que se mueve con el dedo y sale volando: lleva la tarjeta y su indicador
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            // Antes del graphicsLayer: el gesto se mide en el espacio sin transformar (sin temblor)
                            .swipeDrag(
                                dragState,
                                enabled = !swiping && !detailsOpen && !showMessageComposer,
                                onRelease = { velocityDp, width -> onDragRelease(velocityDp, width) }
                            )
                            .graphicsLayer {
                                val dir = swipeDirection.sign
                                val e = exit.value
                                val n = enter.value
                                val dragX = dragState.x.value
                                translationX = dir * e * size.width * 1.3f + dragX
                                translationY = dragState.y.value
                                rotationZ = dir * 10f * e +
                                    (dragX / size.width * MaxDragRotation).coerceIn(-MaxDragRotation, MaxDragRotation)
                                val s = 0.95f + 0.05f * n
                                scaleX = s
                                scaleY = s
                                alpha = (1f - e) * n
                            }
                    ) {
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
                            onMoreClick = {
                                detailsOpen = !detailsOpen
                                onMore()
                            },
                            undoEnabled = canUndo,
                            expanded = detailsOpen,
                            onExpandedChange = { detailsOpen = it },
                            detailsEnabled = !swiping,
                            originName = profile.countryName,
                            spokenLanguages = profile.spokenLanguages.map { DetailLanguage(it.name, it.flagRes, it.level) },
                            learning = DetailLanguage(profile.learning.name, profile.learning.flagRes, profile.learning.level),
                            about = profile.about,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Aviso si ya le escribiste a este GoTalker: viaja con la tarjeta, entre Deshacer y Ver más
                        val pillProgress by animateFloatAsState(
                            targetValue = if (profileAlreadyMessaged) 1f else 0f,
                            label = "messagedPill"
                        )
                        if (pillProgress > 0f) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp)
                                    .graphicsLayer {
                                        alpha = pillProgress
                                        val s = 0.8f + 0.2f * pillProgress
                                        scaleX = s
                                        scaleY = s
                                    }
                            ) {
                                AlreadyMessagedPill()
                            }
                        }
                        // Sello y tinte: dentro de la misma capa, para que se muevan y roten junto con la tarjeta
                        SwipeIndicator(dragState, Modifier.fillMaxSize())
                    }
                    // Solo existe mientras dura la animación; al terminar se quita de la composición
                    if (waveActive) {
                        key(waveId) {
                            SwipeWaveOverlay(
                                direction = swipeDirection,
                                onFinished = { waveActive = false },
                                modifier = Modifier.clip(ProfileCardShape)
                            )
                        }
                    }
                }
                ActionButtonsRow(
                    onReject = { triggerSwipe(SwipeDirection.Reject) },
                    onMessage = onMessage,
                    onAccept = { triggerSwipe(SwipeDirection.Accept) },
                    enabled = !swiping && !detailsOpen && !showMessageComposer
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (noResults) "No encontramos GoTalkers con estos filtros"
                            else "No hay más perfiles por ahora",
                            color = GoTalkInk,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        if (noResults) {
                            Text(
                                text = "Prueba con menos filtros para ver más personas.",
                                color = GoTalkMuted,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            GoTalkPillButton(text = "Quitar filtros", onClick = onClearFilters)
                        }
                    }
                }
            }
        }
        // Ventana flotante de notificaciones, encima de todo el contenido de la pantalla
        NotificationsPanel(
            visible = showNotifications,
            notifications = notifications,
            onDismiss = onCloseNotifications,
            onDelete = onDeleteNotification,
            onClearAll = onClearNotifications
        )
        // Ventana flotante de filtros (solo una ventana a la vez: lo garantiza el ViewModel)
        FiltersPanel(
            visible = showFilters,
            filters = draftFilters,
            onDismiss = onCloseFilters,
            onChange = onUpdateDraft,
            onReset = onResetDraft,
            onApply = onApplyFilters,
            matchCount = draftMatchCount
        )
        // Ventana de "Enviar mensaje" al GoTalker de la tarjeta (también de una en una: la garantiza el ViewModel)
        MessageComposerPanel(
            visible = showMessageComposer,
            profile = profile,
            draft = messageDraft,
            charsLeft = messageCharsLeft,
            sendState = messageSendState,
            canSend = canSendMessage,
            onDraftChange = onMessageDraftChange,
            onSend = onSendMessage,
            onDismiss = onCloseMessageComposer
        )
    }
}

/** Pastilla de vidrio sobre la foto (mismo estilo que los chips de idioma): "Ya le escribiste". */
@Composable
private fun AlreadyMessagedPill() {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(Color.Black.copy(alpha = 0.40f))
            .border(1.dp, Color.White.copy(alpha = 0.35f), PillShape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = "Ya le escribiste",
            color = Color.White,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
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
