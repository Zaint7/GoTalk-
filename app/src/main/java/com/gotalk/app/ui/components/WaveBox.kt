package com.gotalk.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.GoTalkYellowDark
import com.gotalk.app.ui.theme.GoTalkYellowSoft
import kotlin.math.PI
import kotlin.math.sin

/**
 * Recuadro de 56x44 dp con "agua" amarilla ondulada. [level] (0..1) es la altura del agua
 * medida desde abajo; la anima quien lo llama. Contiene una animación infinita, así que
 * solo debe estar en composición mientras se vea (el ítem seleccionado).
 */
@Composable
fun WaveBox(level: Float, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "wave")
    // Fase de 0 a 2π en 2200 ms: al llegar a 2π la senoidal coincide con la de 0, sin salto
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "wavePhase"
    )

    Canvas(
        modifier = modifier
            .size(width = 56.dp, height = 44.dp)
            .clip(RoundedCornerShape(18.dp))
    ) {
        val w = size.width
        val h = size.height
        val amplitude = h * 0.08f
        val top = h * (1f - level)
        val step = 4f

        // Fondo suave: aparece junto con el agua
        drawRect(GoTalkYellowSoft, alpha = (level / 0.6f).coerceIn(0f, 1f))

        // Onda de atrás (más oscura, gira al revés) y de adelante: fases distintas dan profundidad
        fun wave(cycles: Float, offset: Float): Path = Path().apply {
            moveTo(0f, h)
            var x = 0f
            while (x <= w) {
                val y = top + amplitude * sin(2 * PI * cycles * x / w + offset).toFloat()
                lineTo(x, y)
                x += step
            }
            lineTo(w, h)
            close()
        }
        drawPath(wave(cycles = 1.3f, offset = -phase + PI.toFloat()), GoTalkYellowDark, alpha = 0.55f)
        drawPath(wave(cycles = 1f, offset = phase), GoTalkYellow)
    }
}

@Preview(showBackground = true)
@Composable
private fun WaveBoxPreview() {
    WaveBox(level = 0.6f)
}
