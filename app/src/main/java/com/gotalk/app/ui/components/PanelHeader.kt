package com.gotalk.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.ComposerBodyTop
import com.gotalk.app.ui.theme.ComposerHeaderBrush
import com.gotalk.app.ui.theme.ComposerHeaderGlossBrush
import com.gotalk.app.ui.theme.GoTalkInk

/**
 * Encabezado "vivo" de las ventanas flotantes: degradado amarillo diagonal con brillo de vidrio arriba y, abajo,
 * la ola animada de [WaveTopEdge] (en el color del cuerpo) que lo separa del contenido. Pensado para ir como
 * primer elemento de un [FloatingPanel] con `ComposerBodyBrush`. La ola solo corre mientras el panel está en pantalla.
 */
@Composable
fun PanelHeader(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxWidth().background(ComposerHeaderBrush)) {
        Box(modifier = Modifier.matchParentSize().background(ComposerHeaderGlossBrush))
        Row(
            // El padding de abajo deja libre la franja de la ola (36 dp, su cresta sube ~12 dp)
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 26.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
        WaveTopEdge(
            reveal = { 1f },
            color = ComposerBodyTop,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/** Botón de cerrar "de vidrio" para [PanelHeader]: círculo blanco al 85% con borde blanco fino e ícono oscuro. */
@Composable
fun GlassCloseButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    CircleIconButton(
        icon = Icons.Outlined.Close,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        size = 36.dp,
        iconSize = 20.dp,
        containerBrush = SolidColor(Color.White.copy(alpha = 0.85f)),
        contentColor = GoTalkInk,
        border = BorderStroke(1.dp, Color.White)
    )
}
