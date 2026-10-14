package com.gotalk.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.R
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.PillShape

/** Chip en píldora con bandera circular de 22 dp y texto, pensado para ir sobre la foto. */
@Composable
fun ProfileChip(
    text: String,
    @DrawableRes flagRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(PillShape)
            .background(Color.Black.copy(alpha = 0.40f))
            .border(1.dp, Color.White.copy(alpha = 0.35f), PillShape)
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
            color = Color.White,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF555555)
@Composable
private fun ProfileChipPreview() {
    ProfileChip(text = "Español nativo", flagRes = R.drawable.flag_es)
}
