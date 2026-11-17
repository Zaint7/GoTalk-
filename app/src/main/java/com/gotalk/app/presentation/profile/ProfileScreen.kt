package com.gotalk.app.presentation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.R
import com.gotalk.app.ui.components.CircleIconButton
import com.gotalk.app.ui.components.GoTalkPillButton
import com.gotalk.app.ui.components.SearchPickerDialog
import com.gotalk.app.ui.components.WaveTop
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkMuted
import com.gotalk.app.ui.theme.GoTalkRedEnd
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.GoTalkYellowDark
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.PillShape

private val PhotoSize = 132.dp
private val InfoCardShape = RoundedCornerShape(24.dp)

private enum class Picker { None, Country, NativeLanguage }

/**
 * Pestaña Perfil, sin estado: muestra el borrador y avisa con lambdas. El lápiz del header activa la edición
 * ([editing]): las tarjetas de país e idioma abren un selector con buscador y la descripción se escribe en su
 * tarjeta. [errors] son los campos obligatorios que faltan (ya filtrados: solo tras intentar guardar).
 * La barra inferior no vive aquí (la pone MainScaffold).
 */
@Composable
fun ProfileScreen(
    profile: UserProfile,
    editing: Boolean,
    errors: Set<ProfileError>,
    saveEnabled: Boolean,
    savedVisible: Boolean,
    onToggleEditing: () -> Unit,
    onEditPhotoClick: () -> Unit,
    onCountryChange: (String) -> Unit,
    onNativeLanguageChange: (String) -> Unit,
    onAboutChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var picker by remember { mutableStateOf(Picker.None) }

    Box(modifier = modifier.fillMaxSize().background(GoTalkCream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(editing = editing, onToggleEditing = onToggleEditing)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(Modifier.height(4.dp))
                ProfilePhoto(profile = profile, onEditPhotoClick = onEditPhotoClick)
                Spacer(Modifier.height(4.dp))

                InfoCard(
                    icon = Icons.Outlined.LocationOn,
                    label = "Origen",
                    error = if (ProfileError.COUNTRY_REQUIRED in errors) "Elige tu país para guardar" else null,
                    onClick = if (editing) ({ picker = Picker.Country }) else null,
                    onClickLabel = "Elegir país"
                ) {
                    val country = profile.countryName
                    if (country == null) {
                        Placeholder("Elige tu país")
                    } else {
                        FlagValue(
                            flagRes = profile.countryFlagRes ?: R.drawable.logo_g,
                            text = if (profile.city.isBlank()) country else "$country, ${profile.city}"
                        )
                    }
                }
                InfoCard(
                    icon = Icons.Outlined.Language,
                    label = "Idioma nativo",
                    error = if (ProfileError.NATIVE_LANGUAGE_REQUIRED in errors) "Elige tu idioma nativo para guardar" else null,
                    onClick = if (editing) ({ picker = Picker.NativeLanguage }) else null,
                    onClickLabel = "Elegir idioma nativo"
                ) {
                    val native = profile.nativeLanguage
                    if (native == null) {
                        Placeholder("Elige tu idioma nativo")
                    } else {
                        FlagValue(flagRes = native.flagRes, text = native.name)
                    }
                }
                InfoCard(icon = Icons.Outlined.Description, label = "Mi descripción") {
                    if (editing) {
                        AboutEditor(about = profile.about, onAboutChange = onAboutChange)
                    } else if (profile.about.isBlank()) {
                        Placeholder("Cuéntale al mundo algo sobre ti")
                    } else {
                        Text(
                            text = profile.about,
                            color = GoTalkInk,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            lineHeight = 21.sp
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))
                GoTalkPillButton(
                    text = "Guardar",
                    onClick = onSave,
                    enabled = saveEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
            }
        }

        SavedToast(
            visible = savedVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }

    when (picker) {
        Picker.Country -> SearchPickerDialog(
            title = "Elige tu país",
            options = CountryOptions,
            selectedKey = profile.countryCode,
            onSelect = onCountryChange,
            onDismiss = { picker = Picker.None },
            searchPlaceholder = "Buscar país"
        )
        Picker.NativeLanguage -> SearchPickerDialog(
            title = "Tu idioma nativo",
            options = LanguageOptions,
            selectedKey = profile.nativeLanguage?.name,
            onSelect = onNativeLanguageChange,
            onDismiss = { picker = Picker.None },
            searchPlaceholder = "Buscar idioma"
        )
        Picker.None -> Unit
    }
}

/** Fondo amarillo con ola (el mismo [WaveTop] del banner de Descubrir), logo centrado y lápiz a la derecha. */
@Composable
private fun ProfileHeader(editing: Boolean, onToggleEditing: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        WaveTop(modifier = Modifier.matchParentSize())
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(108.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo_gotalk),
                contentDescription = "GoTalk!",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
                    .width(130.dp)
                    .height(48.dp)
            )
            // Lápiz = editar; en edición se vuelve una palomita para terminar (los cambios siguen en el borrador)
            CircleIconButton(
                icon = if (editing) Icons.Outlined.Check else Icons.Outlined.Edit,
                contentDescription = if (editing) "Terminar de editar" else "Editar perfil",
                onClick = onToggleEditing,
                size = 44.dp,
                iconSize = 22.dp,
                containerBrush = SolidColor(if (editing) GoTalkInk else Color.White),
                contentColor = if (editing) GoTalkYellow else GoTalkInk,
                elevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 14.dp)
            )
        }
    }
}

/** Foto circular con aro amarillo y un lápiz pequeño en la esquina inferior derecha. */
@Composable
private fun ProfilePhoto(profile: UserProfile, onEditPhotoClick: () -> Unit) {
    Box(modifier = Modifier.size(PhotoSize + 12.dp)) {
        Image(
            painter = painterResource(profile.photoRes ?: R.drawable.card_photo),
            contentDescription = "Tu foto de perfil",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .align(Alignment.Center)
                .size(PhotoSize)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .border(5.dp, GoTalkYellow, CircleShape)
        )
        CircleIconButton(
            icon = Icons.Outlined.Edit,
            contentDescription = "Cambiar foto",
            onClick = onEditPhotoClick,
            size = 36.dp,
            iconSize = 18.dp,
            containerBrush = SolidColor(GoTalkYellow),
            contentColor = GoTalkInk,
            border = BorderStroke(2.dp, GoTalkCream),
            elevation = 4.dp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

/**
 * Tarjeta crema con borde dorado fino y sombra suave: ícono + etiqueta arriba y el contenido debajo.
 * Con [onClick] se puede tocar (muestra un chevron); con [error] el borde y el mensaje se vuelven rojos.
 */
@Composable
private fun InfoCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    content: @Composable () -> Unit
) {
    val borderBrush = if (error != null) BorderStroke(1.5.dp, GoTalkRedEnd) else BorderStroke(1.dp, GoTalkYellowDark)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, InfoCardShape, ambientColor = GoTalkYellowDark, spotColor = GoTalkYellowDark)
            .clip(InfoCardShape)
            .background(GoTalkCream)
            .border(borderBrush, InfoCardShape)
            .then(
                if (onClick != null) Modifier.clickable(role = Role.Button, onClickLabel = onClickLabel, onClick = onClick)
                else Modifier
            )
            .padding(horizontal = 18.dp, vertical = 14.dp)
            // TalkBack lee la tarjeta como una sola cosa: "Origen, España"
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = GoTalkInk, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                color = GoTalkMuted,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            if (onClick != null) {
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = GoTalkInk, modifier = Modifier.size(24.dp))
            }
        }
        content()
        if (error != null) {
            Text(
                text = error,
                color = GoTalkRedEnd,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

/** Campo de la descripción dentro de su tarjeta: texto libre con contador n/300. */
@Composable
private fun AboutEditor(about: String, onAboutChange: (String) -> Unit) {
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (about.isEmpty()) {
                Placeholder("Cuéntale al mundo algo sobre ti")
            }
            BasicTextField(
                value = about,
                onValueChange = { onAboutChange(it.take(MaxAboutLength)) },
                textStyle = TextStyle(
                    color = GoTalkInk,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                ),
                cursorBrush = SolidColor(GoTalkInk),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
            )
        }
        Text(
            text = "${about.length}/$MaxAboutLength",
            color = if (about.length >= MaxAboutLength) GoTalkInk else GoTalkHintGray,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun FlagValue(flagRes: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(flagRes),
            contentDescription = null, // decorativa: el texto ya lo dice
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            color = GoTalkInk,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Placeholder(text: String) {
    Text(
        text = text,
        color = GoTalkHintGray,
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    )
}

/** Aviso breve "Perfil guardado": píldora oscura con palomita que entra con spring y se va sola. */
@Composable
private fun SavedToast(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        exit = fadeOut() + scaleOut(targetScale = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .shadow(8.dp, PillShape)
                .clip(PillShape)
                .background(GoTalkInk)
                .padding(horizontal = 18.dp, vertical = 12.dp)
                // Se anuncia en TalkBack sin tener que enfocarlo
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = GoTalkYellow, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Perfil guardado",
                color = Color.White,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        profile = fakeUserProfile, editing = false, errors = emptySet(), saveEnabled = false, savedVisible = false,
        onToggleEditing = {}, onEditPhotoClick = {}, onCountryChange = {}, onNativeLanguageChange = {},
        onAboutChange = {}, onSave = {}
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun ProfileScreenEditingErrorsPreview() {
    ProfileScreen(
        profile = fakeUserProfile.copy(countryCode = null, nativeLanguage = null, about = ""),
        editing = true,
        errors = setOf(ProfileError.COUNTRY_REQUIRED, ProfileError.NATIVE_LANGUAGE_REQUIRED),
        saveEnabled = true, savedVisible = false,
        onToggleEditing = {}, onEditPhotoClick = {}, onCountryChange = {}, onNativeLanguageChange = {},
        onAboutChange = {}, onSave = {}
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun ProfileScreenLongSmallSavedPreview() {
    ProfileScreen(
        profile = fakeUserProfile.copy(
            city = "San Cristóbal de La Laguna",
            about = "Texto de prueba muy largo. ".repeat(11)
        ),
        editing = true, errors = emptySet(), saveEnabled = true, savedVisible = true,
        onToggleEditing = {}, onEditPhotoClick = {}, onCountryChange = {}, onNativeLanguageChange = {},
        onAboutChange = {}, onSave = {}
    )
}
