package com.gotalk.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.GoTalkGreenEnd
import com.gotalk.app.ui.theme.GoTalkGreenStart
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkRedEnd
import com.gotalk.app.ui.theme.GoTalkRedStart
import com.gotalk.app.ui.theme.GoTalkYellow

/**
 * Fila de acciones de Descubrir: Rechazar (84 dp), Mensaje (58 dp) y Aceptar (84 dp).
 * Mensaje es más pequeño a propósito: queda al centro, al alcance del pulgar, sin
 * competir con las decisiones. Sin lógica: solo dispara las lambdas recibidas.
 */
@Composable
fun ActionButtonsRow(
    onReject: () -> Unit,
    onMessage: () -> Unit,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            icon = Icons.Outlined.Close,
            contentDescription = "Rechazar",
            onClick = onReject,
            size = 84.dp,
            iconSize = 40.dp,
            containerBrush = Brush.linearGradient(listOf(GoTalkRedStart, GoTalkRedEnd)),
            contentColor = Color.White
        )
        CircleIconButton(
            icon = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Enviar mensaje",
            onClick = onMessage,
            size = 58.dp,
            iconSize = 26.dp,
            containerBrush = SolidColor(GoTalkYellow),
            contentColor = GoTalkInk,
            border = BorderStroke(3.dp, Color.White)
        )
        CircleIconButton(
            icon = Icons.Filled.Favorite,
            contentDescription = "Aceptar",
            onClick = onAccept,
            size = 84.dp,
            iconSize = 38.dp,
            containerBrush = Brush.linearGradient(listOf(GoTalkGreenStart, GoTalkGreenEnd)),
            contentColor = Color.White
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBEF, widthDp = 390)
@Composable
private fun ActionButtonsRowPreview() {
    ActionButtonsRow(onReject = {}, onMessage = {}, onAccept = {})
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBEF, widthDp = 320, heightDp = 600)
@Composable
private fun ActionButtonsRowSmallPreview() {
    ActionButtonsRow(onReject = {}, onMessage = {}, onAccept = {})
}
