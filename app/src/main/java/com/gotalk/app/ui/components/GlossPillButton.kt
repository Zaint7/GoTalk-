package com.gotalk.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.theme.ComposerHeaderGlossBrush
import com.gotalk.app.ui.theme.ComposerLabelGray
import com.gotalk.app.ui.theme.ComposerSendBrush
import com.gotalk.app.ui.theme.ComposerSendDisabledFill
import com.gotalk.app.ui.theme.ComposerShadowTint
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.PillShape

/**
 * Botón en píldora de 48 dp con el amarillo vivo en degradado, brillo de vidrio y sombra dorada (los tokens
 * `Composer*`). Texto oscuro. Al presionar encoge con spring; deshabilitado se apaga a crema grisáceo.
 * Es la versión "vidrio" de [GoTalkPillButton], que se queda con el amarillo plano del resto de la app.
 */
@Composable
fun GlossPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "glossScale"
    )
    // 0 = apagado, 1 = encendido: el degradado y la sombra aparecen y se van con suavidad
    val glow by animateFloatAsState(targetValue = if (enabled) 1f else 0f, label = "glossGlow")

    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow((6 * glow).dp, PillShape, ambientColor = ComposerShadowTint, spotColor = ComposerShadowTint)
            .clip(PillShape)
            .background(ComposerSendDisabledFill)
            .clickable(
                interactionSource = interaction,
                indication = null, // la escala con spring ya hace de respuesta al toque
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.matchParentSize().graphicsLayer { alpha = glow }.background(ComposerSendBrush))
        Box(modifier = Modifier.matchParentSize().graphicsLayer { alpha = glow }.background(ComposerHeaderGlossBrush))
        Text(
            text = text,
            color = if (enabled) GoTalkInk else ComposerLabelGray,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFEF8, widthDp = 360)
@Composable
private fun GlossPillButtonPreview() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        GlossPillButton(text = "Aplicar (2)", onClick = {}, modifier = Modifier.fillMaxWidth())
        GlossPillButton(text = "Aplicar", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
    }
}
