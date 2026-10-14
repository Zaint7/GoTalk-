package com.gotalk.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gotalk.app.R
import com.gotalk.app.ui.theme.GoTalkNotificationDot

/**
 * Banner superior: fondo amarillo ondulado dibujado en código (WaveTop), letras "GoTalk!" centradas
 * y, a la derecha, los botones Filtros y Notificaciones dibujados en código.
 * El fondo se extiende bajo la barra de estado y el contenido queda debajo de ella.
 */
@Composable
fun GoTalkBanner(
    onFiltersClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasNotifications: Boolean = true
) {
    Box(modifier = modifier.fillMaxWidth()) {
        WaveTop(modifier = Modifier.matchParentSize())
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(124.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo_gotalk),
                contentDescription = "GoTalk!",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(150.dp)
                    .height(52.dp)
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = Icons.Outlined.Tune,
                    contentDescription = "Filtros",
                    onClick = onFiltersClick
                )
                CircleIconButton(
                    icon = Icons.Outlined.Notifications,
                    contentDescription = "Notificaciones",
                    onClick = onNotificationsClick,
                    badgeColor = if (hasNotifications) GoTalkNotificationDot else null
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun GoTalkBannerPreview() {
    GoTalkBanner(onFiltersClick = {}, onNotificationsClick = {})
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun GoTalkBannerSmallPreview() {
    GoTalkBanner(onFiltersClick = {}, onNotificationsClick = {})
}
