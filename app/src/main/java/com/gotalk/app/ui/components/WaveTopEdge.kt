package com.gotalk.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/** Alto de la franja donde se dibuja la ola del borde superior del panel. */
val WaveTopEdgeHeight: Dp = 36.dp

private class EdgeLayer(
    val alpha: Float,
    val amplitudeScale: Float,
    val cycles: Float,
    val phase: Float,
    val speed: Float // vueltas completas por ciclo de animación (entero, para que el bucle sea continuo)
)

// De atrás hacia delante: más grande y tenue -> más pequeña y opaca (misma lógica de 3 capas que SwipeWaveOverlay)
private val EdgeLayers = listOf(
    EdgeLayer(alpha = 0.45f, amplitudeScale = 1.0f, cycles = 1.5f, phase = 2.0f, speed = 1f),
    EdgeLayer(alpha = 0.70f, amplitudeScale = 0.75f, cycles = 2.0f, phase = 1.0f, speed = -1f),
    EdgeLayer(alpha = 1.00f, amplitudeScale = 0.50f, cycles = 2.5f, phase = 0.0f, speed = 2f)
)

private const val EdgeSteps = 36
private const val EdgeTwoPi = (2 * PI).toFloat()

/**
 * Borde superior ondulado del panel: tres capas de ola de [color] que se mueven en bucle.
 * [reveal] es el progreso de apertura del panel (0 cerrado, 1 abierto): la amplitud va de 18 dp (agua que
 * sube) a 6 dp (agua asentada) y vuelve a crecer al cerrar. Se lee solo al dibujar, sin recomponer.
 *
 * La animación infinita vive dentro de este composable: solo corre mientras el panel está en pantalla.
 */
@Composable
fun WaveTopEdge(
    reveal: () -> Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "waveTopEdge")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = EdgeTwoPi,
        animationSpec = infiniteRepeatable(tween(durationMillis = 2400, easing = LinearEasing), RepeatMode.Restart),
        label = "waveTopEdgePhase"
    )
    val path = remember { Path() }

    Canvas(modifier = modifier.fillMaxWidth().height(WaveTopEdgeHeight)) {
        val w = size.width
        val h = size.height
        val p = reveal().coerceIn(0f, 1f)
        val amplitude = (18f - 12f * p).dp.toPx() // 18 dp al principio/cierre, 6 dp asentada
        val centerY = h / 2f

        EdgeLayers.forEach { layer ->
            val amp = amplitude * layer.amplitudeScale
            val shift = layer.phase + phase * layer.speed
            path.reset()
            path.moveTo(0f, h)
            for (i in 0..EdgeSteps) {
                val x = w * i / EdgeSteps
                val y = centerY + amp * sin(x / w * EdgeTwoPi * layer.cycles + shift)
                path.lineTo(x, y)
            }
            path.lineTo(w, h)
            path.close()
            drawPath(path, color.copy(alpha = layer.alpha))
        }
    }
}
