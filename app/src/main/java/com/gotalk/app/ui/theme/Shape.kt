package com.gotalk.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Formas de Descubrir: tarjeta de perfil 32dp, barra inferior 30dp, chips en píldora.
val ProfileCardShape = RoundedCornerShape(32.dp)
val BottomBarShape = RoundedCornerShape(30.dp)
val PillShape = RoundedCornerShape(50)

val GoTalkShapes = Shapes(
    large = ProfileCardShape,
    medium = BottomBarShape
)
