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
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
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

    /** Deja la tarjeta en el centro sin animar (se usa cuando ya salió volando y es invisible). */
    suspend fun snapToCenter() {
        x.snapTo(0f)
        y.snapTo(0f)
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

/** Fracción del ancho que hay que arrastrar para confirmar el swipe. */
const val SwipeThresholdFraction = 0.38f

/** Velocidad (dp/s) a partir de la cual un gesto rápido (fling) confirma aunque no llegue al umbral. */
const val SwipeFlingVelocityDp = 800f

/** Con fling, el dedo debe haber recorrido al menos esta fracción del ancho (evita confirmar por un roce). */
const val SwipeFlingMinFraction = 0.08f

/**
 * Decide si al soltar se confirma el swipe y hacia dónde. Devuelve null si la tarjeta debe volver al centro.
 * Confirma si el desplazamiento supera el umbral, o si hubo un fling en el mismo sentido del desplazamiento.
 */
fun resolveSwipeDirection(offsetX: Float, velocityXDp: Float, width: Float): SwipeDirection? {
    if (width <= 0f) return null
    val side = if (offsetX >= 0f) SwipeDirection.Accept else SwipeDirection.Reject
    if (abs(offsetX) >= width * SwipeThresholdFraction) return side
    val sameSide = velocityXDp * offsetX > 0f
    val fling = abs(velocityXDp) >= SwipeFlingVelocityDp && abs(offsetX) >= width * SwipeFlingMinFraction
    return if (fling && sameSide) side else null
}

/**
 * Arrastre horizontal de la tarjeta. Observa los eventos en la pasada Initial (antes que los hijos) y
 * solo "toma" el gesto si, pasado el touch slop, el movimiento es principalmente horizontal; entonces
 * consume los eventos y los hijos (botones, arrastre vertical del panel) se cancelan solos.
 * Si el movimiento es más vertical, no consume nada y el gesto pasa tal cual a los hijos.
 *
 * Al soltar llama a [onRelease] con la velocidad horizontal (dp/s) y el ancho de la tarjeta (px). Si devuelve
 * true, el swipe se confirmó y quien lo maneja se encarga de la tarjeta (no se regresa al centro); si devuelve
 * false (o el gesto se cancela), la tarjeta vuelve al centro con spring.
 */
fun Modifier.swipeDrag(
    state: SwipeDragState,
    enabled: Boolean,
    onRelease: (velocityXDp: Float, width: Float) -> Boolean
): Modifier =
    pointerInput(state, enabled) {
        if (!enabled) return@pointerInput
        val slop = viewConfiguration.touchSlop
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val tracker = VelocityTracker()
            tracker.addPointerInputChange(down)
            var total = Offset.Zero
            var dragging = false
            var confirmed = false
            try {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    tracker.addPointerInputChange(change)
                    if (change.changedToUpIgnoreConsumed()) {
                        if (dragging) {
                            val vx = tracker.calculateVelocity().x / density
                            confirmed = onRelease(vx, size.width.toFloat())
                        }
                        break
                    }
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
                // Si no se confirmó (soltó sin llegar al umbral, se canceló o se deshabilitó a mitad): regresa al centro
                if (dragging && !confirmed) state.settle()
            }
        }
    }
