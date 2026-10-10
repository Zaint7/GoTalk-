package com.gotalk.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Familias del diseño: Luckiest Guy (botones/pestañas), Montserrat (saludo), Roboto (campos y enlaces).
// TODO: agregar los .ttf a res/font (luckiest_guy, montserrat_regular, roboto_regular)
//  y reemplazar FontFamily.Default por FontFamily(Font(R.font.xxx)).
val LuckiestGuy: FontFamily = FontFamily.Default
val Montserrat: FontFamily = FontFamily.Default
val Roboto: FontFamily = FontFamily.Default

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
