package com.gotalk.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gotalk.app.ui.theme.GoTalkPlusKhaki
import com.gotalk.app.ui.theme.GoTalkYellowSoft

/**
 * Cuadrado selector de foto (168 dp por defecto, radio 40 dp). Sin foto muestra el "+";
 * con foto muestra la imagen recortada y oculta el "+".
 */
@Composable
fun PhotoPickerBox(
    imageUri: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 168.dp
) {
    val shape = RoundedCornerShape(40.dp)
    val plusLength = size * (72f / 168f)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(GoTalkYellowSoft)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val stroke = 14.dp
            Box(
                modifier = Modifier
                    .size(width = plusLength, height = stroke)
                    .clip(RoundedCornerShape(stroke / 2))
                    .background(GoTalkPlusKhaki)
            )
            Box(
                modifier = Modifier
                    .size(width = stroke, height = plusLength)
                    .clip(RoundedCornerShape(stroke / 2))
                    .background(GoTalkPlusKhaki)
            )
        }
    }
}
