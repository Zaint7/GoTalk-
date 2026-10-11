package com.gotalk.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gotalk.app.R
import com.gotalk.app.ui.theme.GoTalkInk

/** Cabecera de pasos de 64 dp: flecha atrás (izq.), logo G (centro) y "Paso x de y" (der.). */
@Composable
fun StepHeader(
    step: Int,
    total: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                modifier = Modifier.size(24.dp),
                tint = GoTalkInk
            )
        }

        Image(
            painter = painterResource(R.drawable.logo_g),
            contentDescription = "GoTalk",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .height(56.dp)
        )

        Text(
            text = "Paso $step de $total",
            style = MaterialTheme.typography.bodyMedium,
            color = GoTalkInk,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
        )
    }
}
