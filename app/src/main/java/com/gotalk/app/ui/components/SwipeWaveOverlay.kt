package com.gotalk.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.GoTalkGreenEnd
import com.gotalk.app.ui.theme.GoTalkGreenStart
import com.gotalk.app.ui.theme.GoTalkRedEnd
import com.gotalk.app.ui.theme.GoTalkRedStart
import kotlin.math.PI
import kotlin.math.sin

/** Hacia dónde sale la tarjeta: [Accept] va de izquierda a derecha (verde), [Reject] de derecha a izquierda (rojo). */
enum class SwipeDirection(val sign: Float) {
    Reject(-1f),
    Accept(1f)
}

private class WaveLayer(
    val alpha: Float,
    val amplitudeDp: Float,
    val cycles: Float,
    val phase: Float,
    val lead: Float,
    val color: (SwipeDirection) -> Color
)

// De la más adelantada (tenue) a la de atrás (opaca): se dibujan en este orden para dar profundidad
private val WaveLayers = listOf(
    WaveLayer(0.35f, 34f, 2.0f, 2.1f, 0.10f) { if (it == SwipeDirection.Accept) GoTalkGreenStart else GoTalkRedStart },
    WaveLayer(0.60f, 26f, 2.5f, 1.0f, 0.05f) { if (it == SwipeDirection.Accept) GoTalkGreenStart else GoTalkRedStart },
    WaveLayer(0.90f, 18f, 3.0f, 0.0f, 0.00f) { if (it == SwipeDirection.Accept) GoTalkGreenEnd else GoTalkRedEnd }
)

private const val WAVE_STEPS = 40
private const val TWO_PI = (2 * PI).toFloat()

/**
 * Ola rápida que barre el área de forma vertical: el frente es una línea ondulada vertical que avanza
 * en horizontal. Dibuja tres capas con fase, amplitud y alpha distintos y la fase avanza con el progreso,
 * así que la ola "vibra" mientras avanza. Al terminar se disipa y llama a [onFinished]; el llamador debe
 * quitarla de la composición para que no dibuje en reposo.
 */
@Composable
fun SwipeWaveOverlay(
    direction: SwipeDirection,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val fade = remember { Animatable(1f) }
    val currentOnFinished by rememberUpdatedState(onFinished)
    val path = remember { Path() }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis = 500, easing = FastOutSlowInEasing))
        fade.animateTo(0f, tween(durationMillis = 160))
        currentOnFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val p = progress.value
        val fadeAlpha = fade.value

        WaveLayers.forEach { layer ->
            val amp = layer.amplitudeDp.dp.toPx()
            val margin = amp * 2f
            // Empieza fuera de la pantalla y termina cubriéndola por completo
            val base = p * (1f + layer.lead) * (w + margin * 2f) - margin
            val phase = layer.phase + p * TWO_PI * 3f
            val edge = if (direction.sign > 0f) -margin * 2f else w + margin * 2f

            path.reset()
            path.moveTo(edge, 0f)
            for (i in 0..WAVE_STEPS) {
                val y = h * i / WAVE_STEPS
                val x = base + amp * sin(y / h * TWO_PI * layer.cycles + phase)
                path.lineTo(if (direction.sign > 0f) x else w - x, y)
            }
            path.lineTo(edge, h)
            path.close()

            drawPath(path, layer.color(direction).copy(alpha = layer.alpha * fadeAlpha))
        }
    }
}
