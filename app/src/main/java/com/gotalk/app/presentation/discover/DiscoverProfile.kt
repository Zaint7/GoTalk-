package com.gotalk.app.presentation.discover

import androidx.annotation.DrawableRes
import com.gotalk.app.R

/** Perfil mostrado en Descubrir. Datos falsos por ahora; el backend llega después. */
data class DiscoverProfile(
    val name: String,             // "Ángela R."
    val age: Int,                 // 29
    val countryCode: String,      // "ES"
    val nativeLanguage: String,   // "Español"
    val learningLanguage: String, // "Inglés"
    @DrawableRes val photoRes: Int
)

/** Bandera del país a partir del código ISO. Si no hay recurso, usa el logo G. */
@DrawableRes
fun flagForCountry(countryCode: String): Int = when (countryCode.uppercase()) {
    "ES" -> R.drawable.flag_es
    "GB", "UK" -> R.drawable.flag_gb
    else -> R.drawable.logo_g
}

/** Bandera asociada a un idioma (por ahora solo español e inglés). */
@DrawableRes
fun flagForLanguage(language: String): Int = when (language.trim().lowercase()) {
    "español", "spanish" -> R.drawable.flag_es
    "inglés", "ingles", "english" -> R.drawable.flag_gb
    else -> R.drawable.logo_g
}

/** Perfiles de prueba para previews y para el ViewModel falso del paso 6. */
val fakeDiscoverProfiles: List<DiscoverProfile> = listOf(
    DiscoverProfile("Ángela R.", 29, "ES", "Español", "Inglés", R.drawable.card_photo),
    DiscoverProfile("Lucas M.", 24, "GB", "Inglés", "Español", R.drawable.card_photo),
    DiscoverProfile("Marta S.", 31, "ES", "Español", "Inglés", R.drawable.card_photo)
)
