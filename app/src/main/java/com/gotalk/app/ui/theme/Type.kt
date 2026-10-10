@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
package com.gotalk.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gotalk.app.R

// Familias del diseño: Luckiest Guy (botones/pestañas), Montserrat (saludo), Roboto (campos y enlaces).
// TODO: agregar los .ttf a res/font (luckiest_guy, montserrat_regular, roboto_regular)
//  y reemplazar FontFamily.Default por FontFamily(Font(R.font.xxx)).
val LuckiestGuy: FontFamily = FontFamily(Font(R.font.luckiest_guy))
@OptIn(ExperimentalTextApi::class)
val Montserrat: FontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.montserrat_regular, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700)))
)
val Roboto: FontFamily = FontFamily(Font(R.font.roboto_regular))

val Typography = Typography(
    // "Bienvenido a GoTalk!" - Montserrat 20
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    // "iniciar sesion" en el botón - Luckiest Guy 20
    labelLarge = TextStyle(
        fontFamily = LuckiestGuy,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    // Pestañas "inicio de sesion" / "registrarse" - Luckiest Guy 15
    labelMedium = TextStyle(
        fontFamily = LuckiestGuy,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    // Campos "Correo" / "Contraseña" - Roboto 14
    bodyMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    // "¿Olvidaste tu contraseña?" / "Políticas de privacidad" - Roboto 12
    bodySmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)
