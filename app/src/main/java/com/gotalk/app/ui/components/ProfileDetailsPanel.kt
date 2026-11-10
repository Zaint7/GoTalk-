@file:OptIn(ExperimentalLayoutApi::class)

package com.gotalk.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.R
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkFieldBorder
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkMuted
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.PillShape

/** Idioma para mostrar en el panel: nombre, bandera y nivel opcional (ej. "B1"). */
data class DetailLanguage(
    val name: String,
    @DrawableRes val flagRes: Int,
    val level: String? = null
)

private val FieldShape = RoundedCornerShape(20.dp)

/**
 * Panel de detalles del GoTalker: amarillo, con borde superior de ola animada ([WaveTopEdge]), un "handle"
 * y campos blancos de solo lectura (Origen, Habla, Quiere aprender y Sobre mí) que entran en cascada.
 * [reveal] es el progreso de apertura (0 a 1). El alto lo decide quien lo usa; si el contenido no cabe,
 * el panel hace scroll vertical.
 */
@Composable
fun ProfileDetailsPanel(
    originName: String,
    @DrawableRes originFlagRes: Int,
    spokenLanguages: List<DetailLanguage>,
    learning: DetailLanguage?,
    about: String,
    modifier: Modifier = Modifier,
    reveal: () -> Float = { 1f }
) {
    Box(modifier = modifier) {
    // Borde superior: ola amarilla animada. El cuerpo arranca por debajo del punto más bajo de la ola.
    WaveTopEdge(
        reveal = reveal,
        color = GoTalkYellow,
        modifier = Modifier.align(Alignment.TopStart)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = WaveTopEdgeHeight - 8.dp)
            .background(GoTalkYellow)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Handle: invita a arrastrar
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 40.dp, height = 5.dp)
                .clip(PillShape)
                .background(GoTalkInk.copy(alpha = 0.25f))
        )

        DetailField(icon = Icons.Outlined.LocationOn, label = "Origen", reveal = reveal, index = 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = originName,
                    color = GoTalkInk,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(originFlagRes),
                    contentDescription = null, // decorativa: el texto ya dice el país
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                )
            }
        }

        if (spokenLanguages.isNotEmpty()) {
            DetailField(icon = Icons.Outlined.Language, label = "Habla", reveal = reveal, index = 1) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    spokenLanguages.forEach { DetailChip(text = it.name, flagRes = it.flagRes) }
                }
            }
        }

        if (learning != null) {
            DetailField(icon = Icons.Outlined.MenuBook, label = "Quiere aprender", reveal = reveal, index = 2) {
                DetailChip(
                    text = if (learning.level != null) "${learning.name} · ${learning.level}" else learning.name,
                    flagRes = learning.flagRes,
                    highlighted = true
                )
            }
        }

        DetailField(icon = Icons.Outlined.Person, label = "Sobre mí", reveal = reveal, index = 3) {
            if (about.isBlank()) {
                Text(
                    text = "Aún no ha escrito nada",
                    color = GoTalkHintGray,
                    fontFamily = Nunito,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp
                )
            } else {
                // Scroll interno propio: una descripción larga no estira el panel
                Box(
                    modifier = Modifier
                        .heightIn(max = 96.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = about,
                        color = GoTalkInk,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
    }
}

/**
 * Entrada en cascada: cada campo aparece con fade + desplazamiento vertical corto (16 dp). Se deriva del
 * progreso de apertura (cada campo arranca ~60 ms después del anterior sobre una animación de 600 ms),
 * así que también funciona al arrastrar y al cerrar. Se lee solo al dibujar.
 */
private fun Modifier.cascade(reveal: () -> Float, index: Int): Modifier = graphicsLayer {
    val start = 0.30f + 0.10f * index
    val t = ((reveal() - start) / 0.35f).coerceIn(0f, 1f)
    alpha = t
    translationY = (1f - t) * 16.dp.toPx()
}

/** Campo blanco de solo lectura: ícono negro a la izquierda, etiqueta pequeña y contenido. */
@Composable
private fun DetailField(
    icon: ImageVector,
    label: String,
    reveal: () -> Float,
    index: Int,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .cascade(reveal, index)
            .fillMaxWidth()
            .clip(FieldShape)
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // decorativo: la etiqueta ya dice de qué es el campo
            tint = GoTalkInk,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                color = GoTalkMuted,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            content()
        }
    }
}

/** Chip en píldora para fondo blanco; el destacado va en amarillo. */
@Composable
private fun DetailChip(
    text: String,
    @DrawableRes flagRes: Int,
    highlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(if (highlighted) GoTalkYellow else GoTalkCream)
            .border(1.dp, if (highlighted) GoTalkInk.copy(alpha = 0.15f) else GoTalkFieldBorder, PillShape)
            .padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(flagRes),
            contentDescription = null, // decorativa: el texto ya dice el idioma
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = GoTalkInk,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF555555, widthDp = 354, heightDp = 300)
@Composable
private fun ProfileDetailsPanelPreview() {
    ProfileDetailsPanel(
        originName = "España",
        originFlagRes = R.drawable.flag_es,
        spokenLanguages = listOf(
            DetailLanguage("Español", R.drawable.flag_es),
            DetailLanguage("Francés", R.drawable.flag_fr),
            DetailLanguage("Italiano", R.drawable.flag_it)
        ),
        learning = DetailLanguage("Inglés", R.drawable.flag_gb, "B1"),
        about = "Hola, soy Ángela. Me encanta viajar, probar comida de todos los países y descubrir series " +
            "en versión original. Busco a alguien con quien practicar conversación sin presión.",
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF555555, widthDp = 354, heightDp = 300)
@Composable
private fun ProfileDetailsPanelEmptyPreview() {
    ProfileDetailsPanel(
        originName = "Reino Unido",
        originFlagRes = R.drawable.flag_gb,
        spokenLanguages = listOf(DetailLanguage("Inglés", R.drawable.flag_gb)),
        learning = DetailLanguage("Español", R.drawable.flag_es, "A2"),
        about = "",
        modifier = Modifier.fillMaxWidth()
    )
}
