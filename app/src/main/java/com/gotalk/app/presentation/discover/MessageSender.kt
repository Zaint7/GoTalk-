package com.gotalk.app.presentation.discover

import androidx.annotation.DrawableRes
import kotlinx.coroutines.delay

/**
 * Quien entrega el mensaje. Hoy es un mock; con el backend será la implementación real (Firebase) y podrá fallar
 * de verdad. Devuelve true si el mensaje se entregó y false si no se pudo enviar.
 */
fun interface MessageSender {
    suspend fun send(recipient: DiscoverProfile, text: String): Boolean
}

/** Envío simulado: tarda [MessageSendDelayMillis] y siempre sale bien. */
object MockMessageSender : MessageSender {
    override suspend fun send(recipient: DiscoverProfile, text: String): Boolean {
        delay(MessageSendDelayMillis)
        return true
    }
}

/**
 * Mensaje ya enviado: la "bandeja de enviados" de la sesión. Cuando exista la pestaña Chats, de aquí saldrán las
 * conversaciones. El destinatario se identifica por nombre mientras no haya ids reales.
 */
data class SentMessage(
    val id: Long,
    val recipientName: String,
    @DrawableRes val recipientPhotoRes: Int,
    val text: String,
    val sentAtMillis: Long
)
