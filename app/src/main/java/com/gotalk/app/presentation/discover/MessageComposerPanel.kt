package com.gotalk.app.presentation.discover

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
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
import com.gotalk.app.ui.components.WaveTopEdge
import com.gotalk.app.ui.theme.ComposerBodyBrush
import com.gotalk.app.ui.theme.ComposerBodyTop
import com.gotalk.app.ui.theme.ComposerBorderBrush
import com.gotalk.app.ui.theme.ComposerChipBorderBrush
import com.gotalk.app.ui.theme.ComposerChipPressedFill
import com.gotalk.app.ui.theme.ComposerFieldBorder
import com.gotalk.app.ui.theme.ComposerHeaderBrush
import com.gotalk.app.ui.theme.ComposerFocusBorder
import com.gotalk.app.ui.theme.ComposerHeaderGlossBrush
import com.gotalk.app.ui.theme.ComposerSendBrush
import com.gotalk.app.ui.theme.ComposerSendDisabledFill
import com.gotalk.app.ui.theme.ComposerLabelGray
import com.gotalk.app.ui.theme.ComposerShadowTint
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.PillShape

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
 * Atrás y animación que notificaciones y filtros). Sin estado propio: recibe el borrador y avisa con lambdas.
 * Mientras se envía no se puede editar; al terminar muestra "¡Enviado!" y quien la use la cierra.
 *
 * Diseño: encabezado amarillo vivo con brillo y ola (quién), cuerpo blanco-crema limpio (qué escribir) y detalles
 * de vidrio (borde con reflejo, sombra dorada). Los degradados son tokens del tema (`Composer*`).
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
        containerBrush = ComposerBodyBrush,
        borderBrush = ComposerBorderBrush,
        shadowColor = ComposerShadowTint,
        anchor = FloatingPanelAnchor.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            ComposerHeader(recipient = recipient, onDismiss = onDismiss)

            Column(
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 2.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Para romper el hielo",
                    color = ComposerLabelGray,
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
}

/**
 * Encabezado vivo: degradado diagonal amarillo con brillo de vidrio arriba, el destinatario (foto, "Escríbele a",
 * nombre y edad), el botón de cerrar y, abajo, la ola animada que lo separa del cuerpo. La ola reutiliza
 * [WaveTopEdge]: sus capas traslúcidas del color del cuerpo se ven como un amarillo apenas más claro.
 * La animación infinita solo corre mientras la ventana está en composición (visible).
 */
@Composable
private fun ComposerHeader(recipient: DiscoverProfile, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().background(ComposerHeaderBrush)) {
        Box(modifier = Modifier.matchParentSize().background(ComposerHeaderGlossBrush))

        Row(
            // El padding de abajo deja libre la franja de la ola (36 dp, su cresta sube ~12 dp)
            modifier = Modifier.padding(start = 18.dp, end = 10.dp, top = 14.dp, bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(recipient.photoRes),
                contentDescription = null, // decorativa: el nombre ya dice a quién
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .size(52.dp)
                    .shadow(4.dp, CircleShape, ambientColor = ComposerShadowTint, spotColor = ComposerShadowTint)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Escríbele a",
                    color = GoTalkInk.copy(alpha = 0.7f),
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
            // Vidrio: círculo blanco al 85% con borde blanco fino
            CircleIconButton(
                icon = Icons.Outlined.Close,
                contentDescription = "Cerrar sin enviar",
                onClick = onDismiss,
                size = 36.dp,
                iconSize = 20.dp,
                containerBrush = SolidColor(Color.White.copy(alpha = 0.85f)),
                contentColor = GoTalkInk,
                border = BorderStroke(1.dp, Color.White)
            )
        }

        WaveTopEdge(
            reveal = { 1f },
            color = ComposerBodyTop,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Chip blanco con un mensaje sugerido; al tocarlo reemplaza el borrador. Borde dorado en degradado y sombra mínima.
 * Al presionar: escala con spring y fondo amarillo suave. Zona táctil de 48 dp.
 */
@Composable
private fun SuggestionChip(text: String, enabled: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "chipScale"
    )
    val fill by animateColorAsState(
        targetValue = if (pressed) ComposerChipPressedFill else Color.White,
        label = "chipFill"
    )
    Box(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .widthIn(max = 240.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interaction,
                indication = null, // el cambio de fondo y la escala ya hacen de respuesta al toque
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
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
                .shadow(2.dp, PillShape, ambientColor = ComposerShadowTint, spotColor = ComposerShadowTint)
                .clip(PillShape)
                .background(fill)
                .border(1.dp, ComposerChipBorderBrush, PillShape)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

/** Campo blanco de 20 dp con borde gris cálido muy suave y contador n/200. */
@Composable
private fun MessageField(
    draft: String,
    charsLeft: Int,
    editable: Boolean,
    onDraftChange: (String) -> Unit
) {
    // Con foco el borde pasa del gris cálido al dorado, y un poco más grueso
    var focused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(if (focused) ComposerFocusBorder else ComposerFieldBorder, label = "fieldBorder")
    val borderWidth by animateDpAsState(if (focused) 1.5.dp else 1.dp, label = "fieldBorderWidth")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(FieldShape)
            .background(Color.White)
            .border(borderWidth, borderColor, FieldShape)
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
                    .onFocusChanged { focused = it.isFocused }
            )
        }
        Text(
            text = "${draft.length}/$MaxMessageLength",
            // Al llegar al tope el contador se oscurece; "charsLeft" nunca baja de 0 porque el borrador se corta
            color = if (charsLeft <= 0) GoTalkInk else ComposerLabelGray,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

/**
 * Botón de 52 dp: "Enviar", "Enviando…" (con indicador) o "¡Enviado!" (con palomita). Activo es el amarillo vivo
 * con brillo y sombra dorada, con texto oscuro (contraste alto); inactivo se apaga a crema grisáceo y baja la sombra.
 * Al presionar encoge con spring.
 */
@Composable
private fun SendButton(sendState: MessageSendState, canSend: Boolean, onSend: () -> Unit) {
    val active = canSend || sendState != MessageSendState.Idle
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && canSend) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "sendScale"
    )
    // 0 = apagado, 1 = encendido: el degradado y la sombra aparecen y se van con suavidad
    val glow by animateFloatAsState(targetValue = if (active) 1f else 0f, label = "sendGlow")
    val contentColor = if (active) GoTalkInk else ComposerLabelGray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow((6 * glow).dp, PillShape, ambientColor = ComposerShadowTint, spotColor = ComposerShadowTint)
            .clip(PillShape)
            .background(ComposerSendDisabledFill)
            .clickable(
                interactionSource = interaction,
                indication = null, // la escala con spring ya hace de respuesta al toque
                enabled = canSend,
                role = Role.Button,
                onClick = onSend
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = glow }
                .background(ComposerSendBrush)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = glow }
                .background(ComposerHeaderGlossBrush)
        )
        AnimatedContent(
            targetState = sendState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "sendLabel"
        ) { state ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (state) {
                    MessageSendState.Idle -> Icon(
                        Icons.AutoMirrored.Filled.Send, contentDescription = null,
                        tint = contentColor, modifier = Modifier.size(20.dp)
                    )
                    MessageSendState.Sending -> CircularProgressIndicator(
                        color = contentColor, strokeWidth = 2.5.dp, modifier = Modifier.size(20.dp)
                    )
                    MessageSendState.Sent -> Icon(
                        Icons.Filled.Check, contentDescription = null,
                        tint = contentColor, modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = when (state) {
                        MessageSendState.Idle -> "Enviar"
                        MessageSendState.Sending -> "Enviando…"
                        MessageSendState.Sent -> "¡Enviado!"
                    },
                    color = contentColor,
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
