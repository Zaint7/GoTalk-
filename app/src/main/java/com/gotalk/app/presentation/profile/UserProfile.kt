package com.gotalk.app.presentation.profile

import androidx.annotation.DrawableRes
import com.gotalk.app.R
import com.gotalk.app.presentation.discover.DiscoverProfile
import com.gotalk.app.presentation.discover.Gender
import com.gotalk.app.presentation.discover.ProfileLanguage
import com.gotalk.app.presentation.discover.countryNameFor
import com.gotalk.app.presentation.discover.flagForCountry

/** Máximo de caracteres de la descripción ("Mi descripción"). */
const val MaxAboutLength = 300

/**
 * Perfil del usuario, editable en la pestaña Perfil. Reutiliza [ProfileLanguage] (nombre, bandera, nivel) y los
 * helpers de país de Descubrir, así que lo que se edita aquí es exactamente lo que otros ven en el panel de
 * detalles de la tarjeta (ver [toDiscoverProfile]).
 *
 * [name] y [age] vienen del registro y no se editan en este módulo. La foto es una Uri (texto) o un recurso
 * mock; con el backend llegará solo la Uri.
 */
data class UserProfile(
    val name: String,
    val age: Int,
    val photoUri: String? = null,
    @DrawableRes val photoRes: Int? = null,
    val countryCode: String? = null,              // "ES"; null = sin elegir (obligatorio para guardar)
    val city: String = "",                        // opcional
    val nativeLanguage: ProfileLanguage? = null,  // obligatorio para guardar
    val otherLanguages: List<ProfileLanguage> = emptyList(), // otros idiomas que habla, con nivel
    val learning: ProfileLanguage? = null,        // idioma que quiere aprender (+ nivel)
    val about: String = ""                        // máx. [MaxAboutLength]
) {
    val countryName: String? get() = countryCode?.let(::countryNameFor)

    @get:DrawableRes
    val countryFlagRes: Int? get() = countryCode?.let(::flagForCountry)
}

/** Lo que impide guardar el perfil. */
enum class ProfileError { COUNTRY_REQUIRED, NATIVE_LANGUAGE_REQUIRED }

/** País e idioma nativo son obligatorios. Vacío = se puede guardar. */
fun UserProfile.validate(): Set<ProfileError> = buildSet {
    if (countryCode.isNullOrBlank()) add(ProfileError.COUNTRY_REQUIRED)
    if (nativeLanguage == null) add(ProfileError.NATIVE_LANGUAGE_REQUIRED)
}

/**
 * Deja el perfil en un estado coherente: descripción cortada a [MaxAboutLength] y, en "otros idiomas", sin
 * repetidos ni el idioma nativo (el panel de Descubrir ya lo pone primero).
 */
fun UserProfile.normalized(): UserProfile = copy(
    about = about.take(MaxAboutLength),
    otherLanguages = otherLanguages
        .filterNot { it.name == nativeLanguage?.name }
        .distinctBy { it.name }
)

/**
 * Cómo ven los demás este perfil en Descubrir. El panel de detalles lee país, idiomas que habla (nativo primero),
 * idioma que aprende y descripción. La foto por Uri aún no la pinta la tarjeta (usa recursos), así que sin recurso
 * se muestra la foto de prueba hasta que llegue el backend.
 */
fun UserProfile.toDiscoverProfile(): DiscoverProfile = DiscoverProfile(
    name = name,
    age = age,
    countryCode = countryCode.orEmpty(),
    nativeLanguage = nativeLanguage?.name.orEmpty(),
    learningLanguage = learning?.name.orEmpty(),
    photoRes = photoRes ?: R.drawable.card_photo,
    countryName = countryName.orEmpty(),
    spokenLanguages = listOfNotNull(nativeLanguage) + otherLanguages,
    learning = learning ?: ProfileLanguage(""),
    about = about,
    gender = Gender.OTRO
)

/** Perfil de prueba para el ViewModel y los previews (sin backend todavía). */
val fakeUserProfile = UserProfile(
    name = "Santiago V.",
    age = 25,
    photoRes = R.drawable.card_photo,
    countryCode = "ES",
    city = "Madrid",
    nativeLanguage = ProfileLanguage("Español"),
    otherLanguages = listOf(ProfileLanguage("Francés", level = "A2")),
    learning = ProfileLanguage("Inglés", level = "B1"),
    about = "Me gusta viajar, el cine y practicar idiomas con gente de todo el mundo."
)
