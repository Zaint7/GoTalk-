package com.gotalk.app.presentation.discover

import androidx.annotation.DrawableRes
import com.gotalk.app.R

/** Idioma de un perfil: nombre, bandera y, si aplica, nivel (ej. "B1" para el idioma que aprende). */
data class ProfileLanguage(
    val name: String,
    @DrawableRes val flagRes: Int = flagForLanguage(name),
    val level: String? = null
)

/**
 * Perfil mostrado en Descubrir. Datos falsos por ahora; el backend llega después.
 * Los campos nuevos (país, idiomas, sobre mí) tienen valores por defecto derivados de los antiguos,
 * así que lo que ya usaba [DiscoverProfile] sigue funcionando igual.
 */
data class DiscoverProfile(
    val name: String,             // "Ángela R."
    val age: Int,                 // 29
    val countryCode: String,      // "ES"
    val nativeLanguage: String,   // "Español"
    val learningLanguage: String, // "Inglés"
    @DrawableRes val photoRes: Int,
    val countryName: String = countryNameFor(countryCode),                         // "España"
    val spokenLanguages: List<ProfileLanguage> = listOf(ProfileLanguage(nativeLanguage)), // idiomas que habla
    val learning: ProfileLanguage = ProfileLanguage(learningLanguage),             // idioma que quiere aprender (+ nivel)
    val about: String = ""                                                         // "Sobre mí"; puede estar vacío
)

/** Bandera del país a partir del código ISO. Si no hay recurso, usa el logo G. */
@DrawableRes
fun flagForCountry(countryCode: String): Int = when (countryCode.uppercase()) {
    "ES" -> R.drawable.flag_es
    "GB", "UK" -> R.drawable.flag_gb
    "FR" -> R.drawable.flag_fr
    "IT" -> R.drawable.flag_it
    else -> R.drawable.logo_g
}

/** Nombre del país en español a partir del código ISO. Si no lo conoce, devuelve el propio código. */
fun countryNameFor(countryCode: String): String = when (countryCode.uppercase()) {
    "ES" -> "España"
    "GB", "UK" -> "Reino Unido"
    "FR" -> "Francia"
    "IT" -> "Italia"
    else -> countryCode
}

/** Bandera asociada a un idioma (español, inglés, francés e italiano; el resto usa el logo G). */
@DrawableRes
fun flagForLanguage(language: String): Int = when (language.trim().lowercase()) {
    "español", "spanish" -> R.drawable.flag_es
    "inglés", "ingles", "english" -> R.drawable.flag_gb
    "francés", "frances", "french" -> R.drawable.flag_fr
    "italiano", "italian" -> R.drawable.flag_it
    else -> R.drawable.logo_g
}

/** Perfiles de prueba para previews y para el ViewModel falso del paso 6. */
val fakeDiscoverProfiles: List<DiscoverProfile> = listOf(
    // Descripción larga
    DiscoverProfile(
        name = "Ángela R.", age = 29, countryCode = "ES",
        nativeLanguage = "Español", learningLanguage = "Inglés", photoRes = R.drawable.card_photo,
        learning = ProfileLanguage("Inglés", level = "B1"),
        about = "Hola, soy Ángela. Trabajo como diseñadora gráfica en Madrid y me encanta viajar, " +
            "probar comida de todos los países y descubrir series en versión original. Llevo dos años " +
            "aprendiendo inglés y busco a alguien con quien practicar conversación de forma relajada, " +
            "sin presión, un par de veces por semana. A cambio, puedo ayudarte con tu español, " +
            "con expresiones del día a día y con todo lo que quieras saber sobre España. " +
            "Los fines de semana suelo salir a caminar o a fotografiar la ciudad."
    ),
    // Descripción vacía
    DiscoverProfile(
        name = "Lucas M.", age = 24, countryCode = "GB",
        nativeLanguage = "Inglés", learningLanguage = "Español", photoRes = R.drawable.card_photo,
        learning = ProfileLanguage("Español", level = "A2")
    ),
    // Habla 3 idiomas
    DiscoverProfile(
        name = "Marta S.", age = 31, countryCode = "ES",
        nativeLanguage = "Español", learningLanguage = "Inglés", photoRes = R.drawable.card_photo,
        spokenLanguages = listOf(
            ProfileLanguage("Español"),
            ProfileLanguage("Francés"),
            ProfileLanguage("Italiano")
        ),
        learning = ProfileLanguage("Inglés", level = "B2"),
        about = "Profesora de idiomas. Me gusta el cine y la cocina."
    )
)
