package com.gotalk.app.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.R
import com.gotalk.app.ui.components.CircleIconButton
import com.gotalk.app.ui.components.GoTalkPillButton
import com.gotalk.app.ui.components.WaveTop
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkMuted
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.GoTalkYellowDark
import com.gotalk.app.ui.theme.Nunito

private val PhotoSize = 132.dp
private val InfoCardShape = RoundedCornerShape(24.dp)

/**
 * Pestaña Perfil, sin estado: muestra el borrador y avisa con lambdas. En este paso es solo de lectura;
 * los botones del lápiz y "Guardar" ya están conectados, y la edición llega en el paso siguiente.
 * La barra inferior no vive aquí (la pone MainScaffold).
 */
@Composable
fun ProfileScreen(
    profile: UserProfile,
    canSave: Boolean,
    onEditClick: () -> Unit,
    onEditPhotoClick: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GoTalkCream)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(onEditClick = onEditClick)

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

            InfoCard(icon = Icons.Outlined.LocationOn, label = "Origen") {
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
            InfoCard(icon = Icons.Outlined.Language, label = "Idioma nativo") {
                val native = profile.nativeLanguage
                if (native == null) {
                    Placeholder("Elige tu idioma nativo")
                } else {
                    FlagValue(flagRes = native.flagRes, text = native.name)
                }
            }
            InfoCard(icon = Icons.Outlined.Description, label = "Mi descripción") {
                if (profile.about.isBlank()) {
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
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

/** Fondo amarillo con ola (el mismo [WaveTop] del banner de Descubrir), logo centrado y lápiz a la derecha. */
@Composable
private fun ProfileHeader(onEditClick: () -> Unit) {
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
            // Mismo margen (18 dp) que el resto de pantallas; la zona táctil es de 48 dp
            CircleIconButton(
                icon = Icons.Outlined.Edit,
                contentDescription = "Editar perfil",
                onClick = onEditClick,
                size = 44.dp,
                iconSize = 22.dp,
                containerBrush = SolidColor(Color.White),
                contentColor = GoTalkInk,
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

/** Tarjeta crema con borde dorado fino y sombra suave: ícono + etiqueta arriba y el contenido debajo. */
@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, InfoCardShape, ambientColor = GoTalkYellowDark, spotColor = GoTalkYellowDark)
            .clip(InfoCardShape)
            .background(GoTalkCream)
            .border(1.dp, GoTalkYellowDark, InfoCardShape)
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
                fontSize = 13.sp
            )
        }
        content()
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

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        profile = fakeUserProfile, canSave = false,
        onEditClick = {}, onEditPhotoClick = {}, onSave = {}
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun ProfileScreenEmptyPreview() {
    ProfileScreen(
        profile = fakeUserProfile.copy(countryCode = null, nativeLanguage = null, about = "", city = ""),
        canSave = false,
        onEditClick = {}, onEditPhotoClick = {}, onSave = {}
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun ProfileScreenLongSmallPreview() {
    ProfileScreen(
        profile = fakeUserProfile.copy(
            city = "San Cristóbal de La Laguna",
            about = "Texto de prueba muy largo. ".repeat(11)
        ),
        canSave = true,
        onEditClick = {}, onEditPhotoClick = {}, onSave = {}
    )
}
