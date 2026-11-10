@file:OptIn(ExperimentalFoundationApi::class)

package com.gotalk.app.presentation.discover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDecoration
import com.gotalk.app.ui.theme.GoTalkRedEnd
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.components.CircleIconButton
import com.gotalk.app.ui.components.FloatingPanel
import com.gotalk.app.ui.theme.GoTalkGreenEnd
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkMuted
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.GoTalkYellowSoft
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.PillShape

private val NotificationCardShape = RoundedCornerShape(20.dp)

/**
 * Ventana de notificaciones: tarjeta flotante bajo la campana (vía [FloatingPanel]) con un encabezado
 * "Notificaciones" y una lista de tarjetas blancas. La lista hace scroll interno si no cabe.
 * Sin estado propio: recibe la lista y avisa con [onDismiss].
 */
@Composable
fun NotificationsPanel(
    visible: Boolean,
    notifications: List<GoTalkNotification>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (Long) -> Unit = {},
    onClearAll: () -> Unit = {}
) {
    FloatingPanel(
        visible = visible,
        onDismiss = onDismiss,
        modifier = modifier,
        paneTitle = "Notificaciones"
    ) {
        // Encabezado: título, contador y botón de cerrar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notificaciones",
                color = GoTalkInk,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f)
            )
            if (notifications.isNotEmpty()) {
                // Zona táctil de 48 dp de alto aunque el texto sea pequeño
                Text(
                    text = "Borrar todo",
                    color = GoTalkInk,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(PillShape)
                        .clickable(role = Role.Button, onClick = onClearAll)
                        .padding(horizontal = 10.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
            CircleIconButton(
                icon = Icons.Outlined.Close,
                contentDescription = "Cerrar notificaciones",
                onClick = onDismiss,
                size = 36.dp,
                iconSize = 20.dp,
                containerBrush = SolidColor(Color.White.copy(alpha = 0.6f))
            )
        }

        if (notifications.isEmpty()) {
            EmptyNotifications()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    SwipeableNotificationCard(
                        notification = notification,
                        onDelete = { onDelete(notification.id) },
                        // Los vecinos suben suavemente a ocupar el hueco de la tarjeta borrada
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta que se borra deslizándola hacia cualquier lado: detrás aparece una franja roja con un bote de basura.
 * Al soltar pasado el umbral se llama a [onDelete] y la tarjeta sale.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableNotificationCard(
    notification: GoTalkNotification,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnDelete by rememberUpdatedState(onDelete)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { target ->
            if (target != SwipeToDismissBoxValue.Settled) currentOnDelete()
            true
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        // Deslizar no es posible con TalkBack: se ofrece el borrado como acción de accesibilidad
        modifier = modifier.semantics {
            customActions = listOf(
                CustomAccessibilityAction("Borrar notificación") {
                    currentOnDelete()
                    true
                }
            )
        },
        backgroundContent = {
            val direction = dismissState.dismissDirection
            // Sin deslizar no se pinta nada: evita un borde rojo detrás de la tarjeta
            if (direction != SwipeToDismissBoxValue.Settled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(NotificationCardShape)
                        .background(GoTalkRedEnd)
                        .padding(horizontal = 20.dp),
                    contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) {
                        Alignment.CenterStart
                    } else {
                        Alignment.CenterEnd
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Borrar notificación",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) {
        NotificationCard(notification)
    }
}

/** Tarjeta blanca: avatar con inicial + insignia del tipo, frase (nombre en negrita) y tiempo relativo. */
@Composable
private fun NotificationCard(notification: GoTalkNotification, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(NotificationCardShape)
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(GoTalkYellowSoft),
                contentAlignment = Alignment.Center
            ) {
                if (notification.avatarRes != null) {
                    Image(
                        painter = painterResource(notification.avatarRes),
                        contentDescription = null, // decorativa: el texto ya nombra a la persona
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(46.dp)
                    )
                } else {
                    Text(
                        text = notification.personName.take(1).uppercase(),
                        color = GoTalkInk,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
            }
            TypeBadge(
                type = notification.type,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = boldName(notification),
                color = GoTalkInk,
                fontFamily = Nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = notification.timeAgo,
                color = GoTalkHintGray,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

/** Pone en negrita el nombre cuando la frase empieza por él ("Elena te ha enviado…"). */
private fun boldName(notification: GoTalkNotification) = buildAnnotatedString {
    val text = notification.text
    val name = notification.personName
    if (name.isNotEmpty() && text.startsWith(name)) {
        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append(name) }
        append(text.removePrefix(name))
    } else {
        append(text)
    }
}

/** Insignia circular con el ícono del tipo; cada tipo tiene su color para escanear la lista de un vistazo. */
@Composable
private fun TypeBadge(type: NotificationType, modifier: Modifier = Modifier) {
    val (icon, background, tint) = when (type) {
        NotificationType.INVITACION -> Triple(Icons.Filled.PersonAdd, GoTalkYellow, GoTalkInk)
        NotificationType.MENSAJE -> Triple(Icons.Filled.ChatBubble, GoTalkInk, GoTalkYellow)
        NotificationType.VIDEOLLAMADA -> Triple(Icons.Filled.Videocam, GoTalkGreenEnd, Color.White)
    }
    TypeBadgeCircle(icon = icon, background = background, tint = tint, modifier = modifier)
}

@Composable
private fun TypeBadgeCircle(icon: ImageVector, background: Color, tint: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(22.dp)
            .background(background, CircleShape)
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // decorativo: el texto de la tarjeta ya describe la notificación
            tint = tint,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
private fun EmptyNotifications() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.NotificationsNone,
            contentDescription = null,
            tint = GoTalkInk,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "Estás al día",
            color = GoTalkInk,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Text(
            text = "No tienes notificaciones nuevas.",
            color = GoTalkMuted,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFE626, widthDp = 390, heightDp = 700)
@Composable
private fun NotificationsPanelPreview() {
    NotificationsPanel(visible = true, notifications = fakeNotifications, onDismiss = {})
}

@Preview(showBackground = true, backgroundColor = 0xFFFFE626, widthDp = 320, heightDp = 480)
@Composable
private fun NotificationsPanelManyPreview() {
    // Muchas notificaciones, una con nombre y texto larguísimos, en pantalla pequeña: la lista debe hacer scroll
    val many = List(15) { i ->
        GoTalkNotification(
            id = 100L + i,
            type = NotificationType.entries[i % 3],
            personName = "Maximiliano",
            text = if (i == 0) {
                "Maximiliano Alejandro de la Vega te ha enviado una invitación para practicar inglés este fin de semana."
            } else {
                "Maximiliano te ha enviado un mensaje."
            },
            timeAgo = "Hace ${i + 1} h"
        )
    }
    NotificationsPanel(visible = true, notifications = many, onDismiss = {})
}

@Preview(showBackground = true, backgroundColor = 0xFFFFE626, widthDp = 390, heightDp = 700)
@Composable
private fun NotificationsPanelEmptyPreview() {
    NotificationsPanel(visible = true, notifications = emptyList(), onDismiss = {})
}
