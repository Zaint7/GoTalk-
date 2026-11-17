package com.gotalk.app.presentation.discover

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.components.CircleIconButton
import com.gotalk.app.ui.components.FloatingPanel
import com.gotalk.app.ui.components.FloatingPanelAnchor
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkMuted
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.PillShape
import com.gotalk.app.ui.theme.SoftGoldBorder
import com.gotalk.app.ui.theme.SoftYellowAccent
import com.gotalk.app.ui.theme.SoftYellowHeader
import com.gotalk.app.ui.theme.SoftYellowSurface

private val FieldShape = RoundedCornerShape(20.dp)

/**
 * Mensajes sugeridos para romper el hielo con [profile], armados con sus datos (idioma nativo, idioma que aprende
 * y primer nombre). Todos caben de sobra en [MaxMessageLength].
 */
fun messageSuggestions(profile: DiscoverProfile): List<String> {
    val firstName = profile.name.trim().substringBefore(' ')
    val native = profile.nativeLanguage.trim().lowercase()
    val learning = profile.learningLanguage.trim().lowercase()
    return buildList {
        add("¡Hola, $firstName! ¿Intercambiamos idiomas?")
        if (native.isNotEmpty()) add("¡Hola! ¿Practicamos $native juntos un rato?")
        if (learning.isNotEmpty()) add("Vi que aprendes $learning. ¿Te ayudo a practicar?")
    }
}

/**
 * Ventana "Enviar mensaje" para el GoTalker de la tarjeta (vía [FloatingPanel], centrada y con el mismo scrim,
 * borde dorado, sombra, Atrás y animación que notificaciones y filtros). Sin estado propio: recibe el borrador
 * y avisa con lambdas. Mientras se envía no se puede editar; al terminar muestra "¡Enviado!" y quien la use la cierra.
 */
@Composable
fun MessageComposerPanel(
    visible: Boolean,
    profile: DiscoverProfile?,
    draft: String,
    charsLeft: Int,
    sendState: MessageSendState,
    canSend: Boolean,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Al cerrarse porque cambió la tarjeta, la ventana conserva al destinatario mientras hace su animación de salida
    var shown by remember { mutableStateOf(profile) }
    if (profile != null) shown = profile
    val recipient = shown ?: return

    FloatingPanel(
        visible = visible,
        onDismiss = onDismiss,
        modifier = modifier,
        maxHeightFraction = 0.92f,
        paneTitle = "Enviar mensaje a ${recipient.name}",
        containerColor = SoftYellowSurface,
        borderColor = SoftGoldBorder,
        anchor = FloatingPanelAnchor.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Recipient(recipient = recipient, onDismiss = onDismiss)

            Text(
                text = "Para romper el hielo",
                color = GoTalkMuted,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                messageSuggestions(recipient).forEach { suggestion ->
                    SuggestionChip(
                        text = suggestion,
                        enabled = sendState == MessageSendState.Idle,
                        onClick = { onDraftChange(suggestion) }
                    )
                }
            }

            MessageField(
                draft = draft,
                charsLeft = charsLeft,
                editable = sendState == MessageSendState.Idle,
                onDraftChange = onDraftChange
            )

            SendButton(sendState = sendState, canSend = canSend, onSend = onSend)
        }
    }
}

/** A quién le escribes: foto, "Escríbele a", nombre con edad y botón de cerrar. */
@Composable
private fun Recipient(recipient: DiscoverProfile, onDismiss: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(recipient.photoRes),
            contentDescription = null, // decorativa: el nombre ya dice a quién
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Escríbele a",
                color = GoTalkMuted,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "${recipient.name}, ${recipient.age}",
                color = GoTalkInk,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        CircleIconButton(
            icon = Icons.Outlined.Close,
            contentDescription = "Cerrar sin enviar",
            onClick = onDismiss,
            size = 36.dp,
            iconSize = 20.dp,
            containerBrush = SolidColor(SoftYellowHeader)
        )
    }
}

/** Chip blanco con un mensaje sugerido; al tocarlo reemplaza el borrador. Zona táctil de 48 dp. */
@Composable
private fun SuggestionChip(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .widthIn(max = 240.dp)
            .clip(PillShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = GoTalkInk,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clip(PillShape)
                .background(Color.White)
                .border(1.dp, SoftGoldBorder, PillShape)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

/** Campo blanco con contador n/200. */
@Composable
private fun MessageField(
    draft: String,
    charsLeft: Int,
    editable: Boolean,
    onDraftChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(FieldShape)
            .background(Color.White)
            .border(1.dp, SoftGoldBorder, FieldShape)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // El placeholder y el campo se leen juntos en TalkBack; el contador se oye como estado del campo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    stateDescription = "${draft.length} de $MaxMessageLength caracteres"
                }
        ) {
            if (draft.isEmpty()) {
                Text(
                    text = "Escribe un mensaje corto…",
                    color = GoTalkHintGray,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
            BasicTextField(
                value = draft,
                onValueChange = onDraftChange,
                enabled = editable,
                textStyle = TextStyle(
                    color = GoTalkInk,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                ),
                cursorBrush = SolidColor(GoTalkInk),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 76.dp)
            )
        }
        Text(
            text = "${draft.length}/$MaxMessageLength",
            // Al llegar al tope el contador se oscurece; "charsLeft" nunca baja de 0 porque el borrador se corta
            color = if (charsLeft <= 0) GoTalkInk else GoTalkHintGray,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

/** Botón oscuro de 52 dp: "Enviar", "Enviando…" (con indicador) o "¡Enviado!" (con palomita). */
@Composable
private fun SendButton(sendState: MessageSendState, canSend: Boolean, onSend: () -> Unit) {
    val container by animateColorAsState(
        targetValue = if (canSend || sendState != MessageSendState.Idle) GoTalkInk else GoTalkInk.copy(alpha = 0.3f),
        label = "sendContainer"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(PillShape)
            .background(container)
            .clickable(enabled = canSend, role = Role.Button, onClick = onSend),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = sendState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "sendLabel"
        ) { state ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (state) {
                    MessageSendState.Idle -> Icon(
                        Icons.AutoMirrored.Filled.Send, contentDescription = null,
                        tint = SoftYellowAccent, modifier = Modifier.size(20.dp)
                    )
                    MessageSendState.Sending -> CircularProgressIndicator(
                        color = SoftYellowAccent, strokeWidth = 2.5.dp, modifier = Modifier.size(20.dp)
                    )
                    MessageSendState.Sent -> Icon(
                        Icons.Filled.Check, contentDescription = null,
                        tint = SoftYellowAccent, modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = when (state) {
                        MessageSendState.Idle -> "Enviar"
                        MessageSendState.Sending -> "Enviando…"
                        MessageSendState.Sent -> "¡Enviado!"
                    },
                    color = Color.White,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun MessageComposerEmptyPreview() {
    MessageComposerPanel(
        visible = true, profile = fakeDiscoverProfiles[0], draft = "", charsLeft = MaxMessageLength,
        sendState = MessageSendState.Idle, canSend = false,
        onDraftChange = {}, onSend = {}, onDismiss = {}
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun MessageComposerSendingPreview() {
    MessageComposerPanel(
        visible = true, profile = fakeDiscoverProfiles[2], draft = "¡Hola! ¿Practicamos español juntos un rato?",
        charsLeft = MaxMessageLength - 42, sendState = MessageSendState.Sending, canSend = false,
        onDraftChange = {}, onSend = {}, onDismiss = {}
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
private fun MessageComposerSmallFullPreview() {
    MessageComposerPanel(
        visible = true, profile = fakeDiscoverProfiles[0], draft = "Texto de prueba. ".repeat(12).take(MaxMessageLength),
        charsLeft = 0, sendState = MessageSendState.Sent, canSend = false,
        onDraftChange = {}, onSend = {}, onDismiss = {}
    )
}
