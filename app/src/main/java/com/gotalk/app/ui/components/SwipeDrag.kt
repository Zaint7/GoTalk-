package com.gotalk.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.math.abs

/** Cuánto del movimiento vertical del dedo se refleja en la tarjeta (se mueve "ligeramente" en Y). */
private const val VerticalFollow = 0.3f

/**
 * Desplazamiento de la tarjeta mientras el dedo la arrastra. Se lee solo en bloques de dibujo
 * (graphicsLayer) para no recomponer por frame.
 */
class SwipeDragState internal constructor(private val scope: CoroutineScope) {
    val x = Animatable(0f)
    val y = Animatable(0f)

    internal fun dragBy(delta: Offset) {
        // UNDISPATCHED: el snap se aplica ya, en el orden en que llegan los eventos del dedo
        scope.launch(start = CoroutineStart.UNDISPATCHED) { x.snapTo(x.value + delta.x) }
        scope.launch(start = CoroutineStart.UNDISPATCHED) { y.snapTo(y.value + delta.y * VerticalFollow) }
    }

    /** Vuelve al centro con un spring suave (offset y, por lo tanto la rotación, regresan a 0). */
    internal fun settle() {
        val spec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium)
        scope.launch { x.animateTo(0f, spec) }
        scope.launch { y.animateTo(0f, spec) }
    }
}

/** Estado de arrastre ligado a [key] (la tarjeta actual): al cambiar de tarjeta arranca de cero. */
@Composable
fun rememberSwipeDragState(key: Any?): SwipeDragState {
    val scope = rememberCoroutineScope()
    return remember(key) { SwipeDragState(scope) }
}

/**
 * Arrastre horizontal de la tarjeta. Observa los eventos en la pasada Initial (antes que los hijos) y
 * solo "toma" el gesto si, pasado el touch slop, el movimiento es principalmente horizontal; entonces
 * consume los eventos y los hijos (botones, arrastre vertical del panel) se cancelan solos.
 * Si el movimiento es más vertical, no consume nada y el gesto pasa tal cual a los hijos.
 */
fun Modifier.swipeDrag(state: SwipeDragState, enabled: Boolean): Modifier =
    pointerInput(state, enabled) {
        if (!enabled) return@pointerInput
        val slop = viewConfiguration.touchSlop
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            var total = Offset.Zero
            var dragging = false
            try {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (change.changedToUpIgnoreConsumed()) break
                    val delta = change.positionChange()
                    if (!dragging) {
                        total += delta
                        if (total.getDistance() > slop) {
                            // Más vertical que horizontal: se deja pasar el gesto a los hijos
                            if (abs(total.x) <= abs(total.y)) return@awaitEachGesture
                            dragging = true
                            state.dragBy(Offset(total.x, total.y))
                            change.consume()
                        }
                    } else {
                        state.dragBy(delta)
                        change.consume()
                    }
                }
            } finally {
                // Soltó, se canceló o se deshabilitó a mitad de arrastre: siempre regresa al centro
                if (dragging) state.settle()
            }
        }
    }
