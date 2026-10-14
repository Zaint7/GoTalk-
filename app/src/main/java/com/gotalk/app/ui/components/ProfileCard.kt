@file:OptIn(ExperimentalLayoutApi::class)

package com.gotalk.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.R
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.ProfileCardShape

/**
 * Tarjeta de perfil: foto a pantalla completa, degradado oscuro en el 56% inferior,
 * Deshacer arriba a la izquierda, Ver más arriba a la derecha y datos abajo.
 * Recibe datos simples (no el modelo) para no depender de la capa de presentación.
 * El alto lo decide quien la usa (en DiscoverScreen, con weight(1f)).
 */
@Composable
fun ProfileCard(
    name: String,
    age: Int,
    @DrawableRes photoRes: Int,
    @DrawableRes countryFlagRes: Int,
    nativeLanguageLabel: String,
    @DrawableRes nativeLanguageFlagRes: Int,
    learningLanguageLabel: String,
    @DrawableRes learningLanguageFlagRes: Int,
    onUndoClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(12.dp, ProfileCardShape)
            .clip(ProfileCardShape)
    ) {
        Image(
            painter = painterResource(photoRes),
            contentDescription = "Foto de perfil de $name, $age años",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopEnd,
            modifier = Modifier.fillMaxSize()
        )

        // Degradado para que el texto se lea
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.56f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
                    )
                )
        )

        // El visual mide 42 dp dentro de una zona táctil de 48 dp: 9 + 3 = 12 dp al borde
        CircleIconButton(
            icon = Icons.AutoMirrored.Outlined.Undo,
            contentDescription = "Deshacer última acción",
            onClick = onUndoClick,
            size = 42.dp,
            containerBrush = SolidColor(Color.White.copy(alpha = 0.75f)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(9.dp)
        )
        CircleIconButton(
            icon = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "Ver más del perfil",
            onClick = onMoreClick,
            size = 42.dp,
            iconSize = 26.dp,
            containerBrush = SolidColor(GoTalkYellow),
            contentColor = GoTalkInk,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(9.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    color = Color.White,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = age.toString(),
                    color = Color.White,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
                Spacer(Modifier.width(10.dp))
                Image(
                    painter = painterResource(countryFlagRes),
                    contentDescription = null, // decorativa
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileChip(text = nativeLanguageLabel, flagRes = nativeLanguageFlagRes)
                ProfileChip(text = learningLanguageLabel, flagRes = learningLanguageFlagRes)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBEF, widthDp = 354, heightDp = 480)
@Composable
private fun ProfileCardPreview() {
    ProfileCard(
        name = "Ángela R.",
        age = 29,
        photoRes = R.drawable.card_photo,
        countryFlagRes = R.drawable.flag_es,
        nativeLanguageLabel = "Español nativo",
        nativeLanguageFlagRes = R.drawable.flag_es,
        learningLanguageLabel = "Aprendiendo inglés",
        learningLanguageFlagRes = R.drawable.flag_gb,
        onUndoClick = {},
        onMoreClick = {},
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBEF, widthDp = 320, heightDp = 600)
@Composable
private fun ProfileCardSmallPreview() {
    ProfileCard(
        name = "Ángela R.",
        age = 29,
        photoRes = R.drawable.card_photo,
        countryFlagRes = R.drawable.flag_es,
        nativeLanguageLabel = "Español nativo",
        nativeLanguageFlagRes = R.drawable.flag_es,
        learningLanguageLabel = "Aprendiendo inglés",
        learningLanguageFlagRes = R.drawable.flag_gb,
        onUndoClick = {},
        onMoreClick = {},
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    )
}
