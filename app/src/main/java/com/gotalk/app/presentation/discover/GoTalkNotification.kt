package com.gotalk.app.presentation.discover

import androidx.annotation.DrawableRes

/** Tipo de notificación; define el ícono y el estilo en la ventana de notificaciones. */
enum class NotificationType {
    INVITACION,
    MENSAJE,
    VIDEOLLAMADA
}

/**
 * Notificación de GoTalk. Datos falsos por ahora; el backend llega después.
 * [text] es la frase completa tal como se muestra; [personName] sirve para la inicial del avatar
 * cuando no hay [avatarRes].
 */
data class GoTalkNotification(
    val id: Long,
    val type: NotificationType,
    val personName: String,         // "Elena"
    val text: String,               // "Elena te ha enviado una invitación."
    val timeAgo: String,            // "Hace 5 min"
    @DrawableRes val avatarRes: Int? = null
)

/** Notificaciones de prueba con los tres tipos. */
val fakeNotifications: List<GoTalkNotification> = listOf(
    GoTalkNotification(1, NotificationType.INVITACION, "Elena", "Elena te ha enviado una invitación.", "Hace 5 min"),
    GoTalkNotification(2, NotificationType.MENSAJE, "Alan", "Alan te ha enviado un mensaje.", "Hace 20 min"),
    GoTalkNotification(3, NotificationType.VIDEOLLAMADA, "Alisha", "Prepárate para tu videollamada con Alisha.", "Hace 1 h"),
    GoTalkNotification(4, NotificationType.MENSAJE, "Sofía", "Sofía te ha enviado un mensaje.", "Hace 3 h"),
    GoTalkNotification(5, NotificationType.INVITACION, "Marco", "Marco te ha enviado una invitación.", "Ayer")
)
