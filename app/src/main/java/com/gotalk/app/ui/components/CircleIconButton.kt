package com.gotalk.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.GoTalkInk

/**
 * Botón circular genérico. El visual mide [size], pero la zona táctil nunca baja de 48 dp.
 * Se reutiliza para Filtros, Notificaciones, Deshacer, Ver más y los botones de acción.
 */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    containerBrush: Brush = SolidColor(Color.White),
    contentColor: Color = GoTalkInk,
    border: BorderStroke? = null,
    badgeColor: Color? = null,
    enabled: Boolean = true,
    elevation: Dp = 0.dp
) {
    val touchSize = maxOf(size, 48.dp)
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .sizeIn(minWidth = touchSize, minHeight = touchSize)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .then(if (elevation > 0.dp) Modifier.shadow(elevation, CircleShape) else Modifier)
                .clip(CircleShape)
                .background(containerBrush)
                .then(if (border != null) Modifier.border(border, CircleShape) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = contentColor
            )
        }
        if (badgeColor != null) {
            // Punto de 11 dp pegado a la esquina superior derecha del círculo visual
            val inset = (touchSize - size) / 2
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = inset, end = inset)
                    .size(11.dp)
                    .background(badgeColor, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFE626)
@Composable
private fun CircleIconButtonPreview() {
    CircleIconButton(
        icon = Icons.Outlined.Tune,
        contentDescription = "Filtros",
        onClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFE626)
@Composable
private fun CircleIconButtonBadgePreview() {
    CircleIconButton(
        icon = Icons.Outlined.Notifications,
        contentDescription = "Notificaciones",
        onClick = {},
        badgeColor = Color(0xFFFF4D4F)
    )
}
