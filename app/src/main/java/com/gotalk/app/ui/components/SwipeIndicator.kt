package com.gotalk.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.GoTalkGreenEnd
import com.gotalk.app.ui.theme.GoTalkGreenStart
import com.gotalk.app.ui.theme.GoTalkRedEnd
import com.gotalk.app.ui.theme.GoTalkRedStart
import com.gotalk.app.ui.theme.ProfileCardShape

/** Opacidad máxima del tinte de color sobre la tarjeta (al llegar al umbral de confirmación). */
private const val MaxTintAlpha = 0.30f

/**
 * Indicador de lo que pasará al soltar: tinte suave del color de la acción y un sello (corazón verde a la
 * derecha, X roja a la izquierda) cuyo alpha y tamaño crecen con la distancia al centro; llega al máximo en
 * el umbral de confirmación. Todo se lee en bloques de dibujo (sin recomponer por frame) y no intercepta toques.
 * Pensado para ir DENTRO de la capa que se mueve con la tarjeta.
 */
@Composable
fun SwipeIndicator(state: SwipeDragState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(ProfileCardShape)) {
        // Derecha = me gusta (verde); izquierda = no me gusta (rojo)
        IndicatorLayer(state = state, sign = 1f, tint = GoTalkGreenEnd) {
            Stamp(
                brush = Brush.linearGradient(listOf(GoTalkGreenStart, GoTalkGreenEnd)),
                icon = Icons.Filled.Favorite,
                iconSizeDp = 52
            )
        }
        IndicatorLayer(state = state, sign = -1f, tint = GoTalkRedEnd) {
            Stamp(
                brush = Brush.linearGradient(listOf(GoTalkRedStart, GoTalkRedEnd)),
                icon = Icons.Outlined.Close,
                iconSizeDp = 56
            )
        }
    }
}

@Composable
private fun IndicatorLayer(
    state: SwipeDragState,
    sign: Float,
    tint: Color,
    stamp: @Composable () -> Unit
) {
    // Progreso 0..1 hacia este lado: 0 en el centro o del lado contrario, 1 en el umbral
    fun androidx.compose.ui.graphics.GraphicsLayerScope.progress(): Float =
        (sign * state.x.value / (size.width * SwipeThresholdFraction)).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = progress() * MaxTintAlpha }
                .background(tint)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    val p = progress()
                    alpha = p
                    val s = 0.6f + 0.5f * p
                    scaleX = s
                    scaleY = s
                }
        ) {
            stamp()
        }
    }
}

@Composable
private fun Stamp(brush: Brush, icon: androidx.compose.ui.graphics.vector.ImageVector, iconSizeDp: Int) {
    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(CircleShape)
            .background(brush)
            .border(4.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Decorativo: la acción ya se anuncia con los botones X y corazón
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSizeDp.dp))
    }
}
