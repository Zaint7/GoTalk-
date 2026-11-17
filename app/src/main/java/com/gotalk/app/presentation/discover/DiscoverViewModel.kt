package com.gotalk.app.presentation.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Estado de Descubrir. [profile] es null cuando ya no quedan perfiles.
 * [canUndo] es false en el primer perfil (no hay nada que deshacer).
 * [notifications] alimenta la ventana de notificaciones y el badge de la campana;
 * [showNotifications] indica si esa ventana está abierta.
 *
 * Filtros: [appliedFilters] es lo que realmente filtra el feed; [draftFilters] es el borrador que edita la
 * ventana de filtros y solo pasa a ser aplicado con "Aplicar". [noResults] es true cuando los filtros
 * aplicados no dejan a nadie (distinto de "ya viste a todos").
 */
data class DiscoverUiState(
    val profile: DiscoverProfile? = null,
    val canUndo: Boolean = false,
    val notifications: List<GoTalkNotification> = emptyList(),
    val showNotifications: Boolean = false,
    val appliedFilters: DiscoverFilters = DiscoverFilters(),
    val draftFilters: DiscoverFilters = DiscoverFilters(),
    val showFilters: Boolean = false,
    val noResults: Boolean = false,
    /** Cuántos GoTalkers dejaría el borrador si se aplicara ahora (se actualiza en vivo en la ventana de filtros). */
    val draftMatchCount: Int = 0,
    /** Redactor de "Enviar mensaje" para el GoTalker de la tarjeta actual ([profile]). */
    val showMessageComposer: Boolean = false,
    val messageDraft: String = "",
    val messageSendState: MessageSendState = MessageSendState.Idle
) {
    /** Cuántos filtros aplicados se salen del valor por defecto. */
    val activeFiltersCount: Int get() = appliedFilters.activeFiltersCount

    /** Caracteres que quedan antes de llegar al límite del mensaje. */
    val messageCharsLeft: Int get() = MaxMessageLength - messageDraft.length

    /** "Enviar" disponible: hay un GoTalker, el mensaje no está vacío (ni es solo espacios) y no se está enviando. */
    val canSendMessage: Boolean
        get() = showMessageComposer && profile != null && messageDraft.isNotBlank() &&
            messageSendState == MessageSendState.Idle
}

/** Máximo de caracteres de un mensaje de presentación. */
const val MaxMessageLength = 200

/** Cuánto dura el envío simulado (sin backend todavía). */
const val MessageSendDelayMillis = 700L

/** Estado del envío: [Idle] escribiendo, [Sending] enviando, [Sent] enviado (la ventana lo muestra y se cierra). */
enum class MessageSendState { Idle, Sending, Sent }

@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {

    // Más adelante los perfiles vendrán de un repositorio (Firebase);
    // por ahora se toman de la lista falsa.
    private val allProfiles: List<DiscoverProfile> = fakeDiscoverProfiles

    private var appliedFilters = DiscoverFilters()
    private var draftFilters = DiscoverFilters()
    private var showFilters = false

    // Lo que se ve en el feed: todos los perfiles filtrados con appliedFilters
    private var profiles: List<DiscoverProfile> = filterProfiles(allProfiles, appliedFilters)

    // Posición dentro de la lista. Rango 0..profiles.size: el valor profiles.size
    // significa "ya se pasó el último perfil" (profile = null).
    private var index = 0

    // Notificaciones (datos falsos por ahora)
    private var notifications: List<GoTalkNotification> = fakeNotifications
    private var showNotifications = false

    // Redactor de mensajes: el borrador es de la tarjeta actual y no se guarda en ningún sitio
    private var showMessageComposer = false
    private var messageDraft = ""
    private var messageSendState = MessageSendState.Idle
    private var sendJob: Job? = null

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    fun onReject() = advance()

    fun onAccept() = advance()

    fun onUndo() {
        // Si se acabaron los perfiles, deshacer vuelve al último; en el primero no hace nada.
        if (index > 0) {
            index--
            resetMessageComposer()
            publish()
        }
    }

    // Solo una ventana flotante a la vez: abrir una cierra las otras.

    fun openNotifications() {
        showNotifications = true
        showFilters = false
        resetMessageComposer()
        publish()
    }

    /** Abre "Enviar mensaje" para el GoTalker de la tarjeta actual, con el borrador vacío. Sin tarjeta no hace nada. */
    fun openMessageComposer() {
        if (profiles.getOrNull(index) == null) return
        resetMessageComposer()
        showMessageComposer = true
        showNotifications = false
        showFilters = false
        publish()
    }

    /** Cierra sin enviar: el borrador de esta tarjeta se descarta. Si ya se estaba enviando, el envío se cancela. */
    fun closeMessageComposer() {
        resetMessageComposer()
        publish()
    }

    /** Actualiza el borrador, cortado a [MaxMessageLength]. Se ignora si la ventana está cerrada o ya se está enviando. */
    fun updateMessageDraft(text: String) {
        if (!showMessageComposer || messageSendState != MessageSendState.Idle) return
        messageDraft = text.take(MaxMessageLength)
        publish()
    }

    /**
     * Envía el borrador (sin backend: pasa por Sending y a los [MessageSendDelayMillis] queda en Sent).
     * No hace nada si el mensaje está vacío o es solo espacios, o si ya se está enviando.
     */
    fun sendMessage() {
        if (!_uiState.value.canSendMessage) return
        messageSendState = MessageSendState.Sending
        publish()
        sendJob = viewModelScope.launch {
            delay(MessageSendDelayMillis)
            messageSendState = MessageSendState.Sent
            publish()
        }
    }

    /** Deja el redactor cerrado y limpio: sin borrador, sin envío en curso. */
    private fun resetMessageComposer() {
        sendJob?.cancel()
        sendJob = null
        showMessageComposer = false
        messageDraft = ""
        messageSendState = MessageSendState.Idle
    }

    fun closeNotifications() {
        showNotifications = false
        publish()
    }

    fun deleteNotification(id: Long) {
        notifications = notifications.filterNot { it.id == id }
        publish()
    }

    fun clearAllNotifications() {
        notifications = emptyList()
        publish()
    }

    /** Abre la ventana de filtros con un borrador nuevo, copia de lo aplicado. */
    fun openFilters() {
        draftFilters = appliedFilters
        showFilters = true
        showNotifications = false
        resetMessageComposer()
        publish()
    }

    /** Cierra sin aplicar: el borrador se descarta y los filtros vigentes no cambian. */
    fun closeFilters() {
        showFilters = false
        draftFilters = appliedFilters
        publish()
    }

    /** Modifica el borrador, p. ej. `updateDraft { it.copy(onlyOnline = true) }`. No toca el feed. */
    fun updateDraft(transform: (DiscoverFilters) -> DiscoverFilters) {
        draftFilters = transform(draftFilters)
        publish()
    }

    /** El borrador pasa a ser el filtro vigente, el feed se recalcula desde el primer perfil y la ventana se cierra. */
    fun applyFilters() {
        appliedFilters = draftFilters
        refreshFeed()
        showFilters = false
        publish()
    }

    /** Deja el borrador en los valores por defecto (no aplica nada hasta tocar "Aplicar"). */
    fun resetDraft() {
        draftFilters = DiscoverFilters()
        publish()
    }

    /** "Quitar filtros" del estado vacío: quita los filtros vigentes y vuelve a mostrar todo el feed. */
    fun clearAppliedFilters() {
        appliedFilters = DiscoverFilters()
        draftFilters = appliedFilters
        refreshFeed()
        publish()
    }

    private fun refreshFeed() {
        profiles = filterProfiles(allProfiles, appliedFilters)
        index = 0
        resetMessageComposer()
    }

    private fun advance() {
        if (index < profiles.size) {
            index++
            resetMessageComposer()
            publish()
        }
    }

    private fun publish() {
        _uiState.update { buildState() }
    }

    private fun buildState() = DiscoverUiState(
        profile = profiles.getOrNull(index),
        canUndo = index > 0,
        notifications = notifications,
        showNotifications = showNotifications,
        appliedFilters = appliedFilters,
        draftFilters = draftFilters,
        showFilters = showFilters,
        noResults = profiles.isEmpty(),
        draftMatchCount = filterProfiles(allProfiles, draftFilters).size,
        showMessageComposer = showMessageComposer,
        messageDraft = messageDraft,
        messageSendState = messageSendState
    )
}
