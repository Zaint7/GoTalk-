package com.gotalk.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import com.gotalk.app.ui.theme.GoTalkYellow

/** Ola amarilla superior de ancho completo con borde inferior ondulado. */
@Composable
fun WaveTop(modifier: Modifier = Modifier) {
    // TODO: reemplazar por ic_wave_top.xml exportado de Figma.
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, h * 0.55f)
            cubicTo(w * 0.80f, h * 1.15f, w * 0.60f, h * 0.15f, w * 0.40f, h * 0.65f)
            cubicTo(w * 0.25f, h * 1.00f, w * 0.10f, h * 0.60f, 0f, h * 0.85f)
            close()
        }
        drawPath(path, GoTalkYellow)
    }
}

/** Ola amarilla inferior de ancho completo con borde superior ondulado. */
@Composable
fun WaveBottom(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.45f)
            cubicTo(w * 0.15f, h * 0.05f, w * 0.30f, h * 0.90f, w * 0.50f, h * 0.45f)
            cubicTo(w * 0.70f, h * 0.00f, w * 0.85f, h * 0.80f, w, h * 0.30f)
            lineTo(w, h)
            close()
        }
        drawPath(path, GoTalkYellow)
    }
}
