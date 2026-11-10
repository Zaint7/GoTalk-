package com.gotalk.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gotalk.app.R
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkNotificationDot
import com.gotalk.app.ui.theme.GoTalkYellowDark
import com.gotalk.app.ui.theme.PillShape

/**
 * Banner superior: fondo amarillo ondulado dibujado en código (WaveTop), logo "GoTalk!" a la izquierda
 * y, a la derecha, la cápsula crema con los botones Filtros y Notificaciones.
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
            // Mismo padding horizontal que la tarjeta de perfil (18 dp) para alinear con los bordes de la foto
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_gotalk),
                    contentDescription = "GoTalk!",
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart,
                    modifier = Modifier
                        .width(130.dp)
                        .height(48.dp)
                )
                Row(
                    modifier = Modifier
                        .shadow(4.dp, PillShape)
                        .background(GoTalkCream, PillShape)
                        .border(1.dp, GoTalkYellowDark, PillShape),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIconButton(
                        icon = Icons.Outlined.Tune,
                        contentDescription = "Filtros",
                        onClick = onFiltersClick,
                        containerBrush = SolidColor(Color.Transparent)
                    )
                    CircleIconButton(
                        icon = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        onClick = onNotificationsClick,
                        containerBrush = SolidColor(Color.Transparent),
                        badgeColor = if (hasNotifications) GoTalkNotificationDot else null
                    )
                }
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
