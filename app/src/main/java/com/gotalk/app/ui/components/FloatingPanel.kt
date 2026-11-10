package com.gotalk.app.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.GoTalkYellowDark

private val FloatingPanelShape = RoundedCornerShape(28.dp)

// La cápsula del header mide 52 dp con 6 dp de margen arriba y abajo: el panel queda justo debajo, con 6 dp de aire
private val FloatingPanelTopOffset = 64.dp

// Los paneles nacen en la esquina superior derecha, bajo los botones de la cápsula
private val FloatingPanelOrigin = TransformOrigin(pivotFractionX = 0.9f, pivotFractionY = 0f)

/**
 * Contenedor común de las ventanas flotantes de Descubrir (notificaciones y filtros): scrim que oscurece el
 * fondo y cierra al tocarlo, botón Atrás del sistema, y una tarjeta amarilla (28 dp, borde dorado fino y sombra
 * como la barra inferior) anclada arriba a la derecha, bajo la cápsula. Aparece con escala + fade desde la
 * esquina del botón y se va con la animación contraria.
 *
 * El panel mide ~90% del ancho (con tope de 420 dp) y como mucho [maxHeightFraction] del alto; si el contenido
 * no cabe, quien lo use debe ofrecer su propio scroll dentro de [content].
 * Va dentro del contenedor de la pantalla, encima del resto, y no ocupa nada mientras [visible] es false.
 */
@Composable
fun FloatingPanel(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    maxHeightFraction: Float = 0.6f,
    paneTitle: String = "",
    content: @Composable ColumnScope.() -> Unit
) {
    BackHandler(enabled = visible, onBack = onDismiss)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxPanelHeight = maxHeight * maxHeightFraction

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            ) {}
        }

        AnimatedVisibility(
            visible = visible,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = FloatingPanelTopOffset, end = 18.dp),
            enter = fadeIn(tween(220)) + scaleIn(
                initialScale = 0.85f,
                transformOrigin = FloatingPanelOrigin,
                animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow)
            ),
            exit = fadeOut(tween(150)) + scaleOut(
                targetScale = 0.9f,
                transformOrigin = FloatingPanelOrigin,
                animationSpec = tween(150)
            )
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(0.9f)
                    .heightIn(max = maxPanelHeight)
                    .shadow(8.dp, FloatingPanelShape, ambientColor = GoTalkInk, spotColor = GoTalkInk)
                    // TalkBack anuncia el título al aparecer la ventana
                    .semantics { this.paneTitle = paneTitle }
                    .clip(FloatingPanelShape)
                    .background(GoTalkYellow)
                    .border(1.5.dp, GoTalkYellowDark, FloatingPanelShape)
                    // Los toques sobre el panel no deben llegar al scrim (que cerraría la ventana)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                content = content
            )
        }
    }
}
