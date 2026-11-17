package com.gotalk.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Ventana "Enviar mensaje": encabezado vivo + cuerpo limpio + detalles de vidrio.
// Son tokens para reutilizar en las ventanas de notificaciones y filtros.

private val ComposerGold = Color(0xFFF5C400)

/** Cuerpo de la tarjeta: blanco-crema casi uniforme, con un degradado casi imperceptible hacia abajo. */
val ComposerBodyTop = Color(0xFFFFFEF8)
val ComposerBodyBottom = Color(0xFFFFF9E3)
val ComposerBodyBrush: Brush = Brush.verticalGradient(listOf(ComposerBodyTop, ComposerBodyBottom))

/** Borde con reflejo: dorado casi pleno arriba (90%) que se desvanece abajo (25%). */
val ComposerBorderBrush: Brush = Brush.verticalGradient(
    listOf(ComposerGold.copy(alpha = 0.90f), ComposerGold.copy(alpha = 0.25f))
)

/** Encabezado: amarillo vivo cálido en diagonal (arriba a la izquierda -> abajo a la derecha). */
val ComposerHeaderBrush: Brush = Brush.linearGradient(listOf(Color(0xFFFFE600), Color(0xFFFFD000)))

/** Brillo de "plástico/vidrio" sobre la mitad superior del encabezado, como la cápsula del banner. */
val ComposerHeaderGlossBrush: Brush = Brush.verticalGradient(
    0f to Color.White.copy(alpha = 0.30f),
    0.5f to Color.Transparent,
    1f to Color.Transparent
)

/** Sombra con tinte dorado (no gris), igual que la cápsula del banner. */
val ComposerShadowTint = Color(0xFFB38F00)

/** Chips de sugerencias: borde dorado suave y relleno al presionar. */
val ComposerChipBorderBrush: Brush = Brush.linearGradient(listOf(Color(0xFFF7D94C), Color(0xFFE8B600)))
val ComposerChipPressedFill = Color(0xFFFFF3B0)

/** Campo de texto: borde gris cálido muy suave y, con foco, dorado. */
val ComposerFieldBorder = Color(0xFFE7E2D3)
val ComposerFocusBorder = ComposerGold

/** Botón Enviar: el amarillo vivo del encabezado en vertical (con el mismo brillo encima). Inactivo: crema grisáceo. */
val ComposerSendBrush: Brush = Brush.verticalGradient(listOf(Color(0xFFFFEB55), Color(0xFFFFD000)))
val ComposerSendDisabledFill = Color(0xFFEFEBDD)

/** Etiquetas pequeñas sobre el cuerpo crema: gris cálido con contraste > 4.5:1. */
val ComposerLabelGray = Color(0xFF6F6A5C)
