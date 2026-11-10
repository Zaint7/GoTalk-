package com.gotalk.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.R
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkNotificationDot
import com.gotalk.app.ui.theme.PillShape

// Tinte de la sombra de la cápsula (amarillo oscuro, no gris) y color que "recorta" el badge sobre el vidrio
private val GlassShadowTint = Color(0xFFB38F00)
private val BadgeCutout = Color(0xFFFFF9D2)

/**
 * Banner superior: fondo amarillo ondulado dibujado en código (WaveTop), logo "GoTalk!" a la izquierda
 * y, a la derecha, la cápsula de vidrio con los botones Filtros y Notificaciones.
 * El fondo se extiende bajo la barra de estado y el contenido queda debajo de ella.
 *
 * [notificationCount] > 0 muestra el número en el badge ("9+" como máximo); si es 0 y
 * [hasNotifications] es true, el badge es solo un punto.
 */
@Composable
fun GoTalkBanner(
    onFiltersClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasNotifications: Boolean = true,
    notificationCount: Int = 0
) {
    Box(modifier = modifier.fillMaxWidth()) {
        WaveTop(modifier = Modifier.matchParentSize())
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(124.dp)
        ) {
            // Mismo padding horizontal que la tarjeta de perfil (18 dp) para alinear con los bordes de la foto
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_gotalk),
                    contentDescription = "GoTalk!",
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart,
                    modifier = Modifier
                        .width(130.dp)
                        .height(48.dp)
                )
                GlassCapsule {
                    GlassIconButton(
                        icon = Icons.Outlined.Tune,
                        contentDescription = "Filtros",
                        onClick = onFiltersClick
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, GoTalkInk.copy(alpha = 0.22f), Color.Transparent)
                                )
                            )
                    )
                    GlassIconButton(
                        icon = Icons.Outlined.Notifications,
                        contentDescription = when {
                            notificationCount > 0 -> "Notificaciones, $notificationCount sin leer"
                            hasNotifications -> "Notificaciones, tienes nuevas"
                            else -> "Notificaciones"
                        },
                        onClick = onNotificationsClick,
                        showBadge = hasNotifications || notificationCount > 0,
                        badgeCount = notificationCount
                    )
                }
            }
        }
    }
}

/**
 * Cápsula "vidrio/plástico" sobre el amarillo: relleno translúcido en degradado, borde con reflejo,
 * brillo en la mitad superior y sombra con tinte amarillo. Sin blur real: todo se simula con gradientes.
 */
@Composable
private fun GlassCapsule(content: @Composable RowScope.() -> Unit) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 10.dp,
                shape = PillShape,
                ambientColor = GlassShadowTint,
                spotColor = GlassShadowTint
            )
            // Etapa de color sobre la que se apoya el vidrio: evita que el amarillo se "coma" el relleno
            .background(GoTalkCream.copy(alpha = 0.25f), PillShape)
            .clip(PillShape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.88f), GoTalkCream.copy(alpha = 0.60f))
                )
            )
            .border(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color.White, Color.White.copy(alpha = 0.55f))
                ),
                PillShape
            )
    ) {
        // Brillo en la mitad superior
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                            endY = size.height * 0.5f
                        ),
                        size = Size(size.width, size.height * 0.5f)
                    )
                }
        )
        Row(
            modifier = Modifier
                .height(52.dp)
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/** Botón de 44 dp (zona táctil de 48) con escala al presionar y badge opcional arriba a la derecha. */
@Composable
private fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    showBadge: Boolean = false,
    badgeCount: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "glassButtonScale"
    )
    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = GoTalkInk
        )
        // Al quedar sin notificaciones el badge se encoge y se desvanece; al salir de la composición
        // también se detiene su pulso infinito.
        AnimatedVisibility(
            visible = showBadge,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 3.dp, end = 3.dp)
        ) {
            NotificationBadge(count = badgeCount)
        }
    }
}

/** Badge rojo con borde del color de la cápsula; solo se compone con notificaciones, así el pulso no corre en reposo. */
@Composable
private fun NotificationBadge(count: Int, modifier: Modifier = Modifier) {
    val pulse by rememberInfiniteTransition(label = "badgePulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "badgePulseScale"
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            .then(
                if (count > 0) Modifier.defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                else Modifier.size(16.dp) // punto de 12 dp + borde de 2 dp
            )
            .background(GoTalkNotificationDot, CircleShape)
            .border(2.dp, BadgeCutout, CircleShape)
            .padding(horizontal = if (count > 0) 3.dp else 0.dp),
        contentAlignment = Alignment.Center
    ) {
        if (count > 0) {
            Text(
                text = if (count > 9) "9+" else count.toString(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 10.sp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun GoTalkBannerPreview() {
    GoTalkBanner(onFiltersClick = {}, onNotificationsClick = {})
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun GoTalkBannerCountPreview() {
    GoTalkBanner(onFiltersClick = {}, onNotificationsClick = {}, notificationCount = 12)
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun GoTalkBannerSmallPreview() {
    GoTalkBanner(onFiltersClick = {}, onNotificationsClick = {})
}
