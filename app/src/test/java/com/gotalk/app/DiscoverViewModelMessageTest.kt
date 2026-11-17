package com.gotalk.app

import com.gotalk.app.presentation.discover.DiscoverViewModel
import com.gotalk.app.presentation.discover.MaxMessageLength
import com.gotalk.app.presentation.discover.MessageSendDelayMillis
import com.gotalk.app.presentation.discover.MessageSendState
import com.gotalk.app.presentation.discover.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelMessageTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var vm: DiscoverViewModel
    private val state get() = vm.uiState.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        vm = DiscoverViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `abrir deja el redactor visible con borrador vacio y sin envio`() {
        vm.openMessageComposer()
        assertTrue(state.showMessageComposer)
        assertEquals("", state.messageDraft)
        assertEquals(MessageSendState.Idle, state.messageSendState)
        assertFalse(state.canSendMessage)
    }

    @Test
    fun `escribir actualiza el borrador y habilita enviar`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola, ¿practicamos inglés?")
        assertEquals("Hola, ¿practicamos inglés?", state.messageDraft)
        assertTrue(state.canSendMessage)
    }

    @Test
    fun `el borrador se corta en el limite de caracteres`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("a".repeat(MaxMessageLength + 30))
        assertEquals(MaxMessageLength, state.messageDraft.length)
        assertEquals(0, state.messageCharsLeft)
    }

    @Test
    fun `un mensaje vacio o solo con espacios no se puede enviar`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("   \n  ")
        assertFalse(state.canSendMessage)
        vm.sendMessage()
        assertEquals(MessageSendState.Idle, state.messageSendState)
    }

    @Test
    fun `enviar pasa por Sending y luego Sent`() {
        val scope = TestScope(dispatcher)
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        assertEquals(MessageSendState.Sending, state.messageSendState)
        assertFalse(state.canSendMessage)

        scope.advanceTimeBy(MessageSendDelayMillis - 1)
        scope.runCurrent()
        assertEquals(MessageSendState.Sending, state.messageSendState)

        scope.advanceTimeBy(2)
        scope.runCurrent()
        assertEquals(MessageSendState.Sent, state.messageSendState)
    }

    @Test
    fun `mientras se envia no se puede editar ni enviar de nuevo`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        vm.updateMessageDraft("Otro texto")
        assertEquals("Hola", state.messageDraft)
        vm.sendMessage()
        assertEquals(MessageSendState.Sending, state.messageSendState)
    }

    @Test
    fun `cerrar sin enviar descarta el borrador`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.closeMessageComposer()
        assertFalse(state.showMessageComposer)
        assertEquals("", state.messageDraft)
        vm.openMessageComposer()
        assertEquals("", state.messageDraft)
    }

    @Test
    fun `cerrar durante el envio lo cancela`() {
        val scope = TestScope(dispatcher)
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        vm.closeMessageComposer()
        scope.advanceTimeBy(MessageSendDelayMillis * 2)
        scope.runCurrent()
        assertEquals(MessageSendState.Idle, state.messageSendState)
        assertFalse(state.showMessageComposer)
    }

    @Test
    fun `cambiar de tarjeta reinicia el redactor`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.onAccept()
        assertFalse(state.showMessageComposer)
        assertEquals("", state.messageDraft)
    }

    @Test
    fun `deshacer tambien reinicia el redactor`() {
        vm.onReject()
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.onUndo()
        assertFalse(state.showMessageComposer)
        assertEquals("", state.messageDraft)
    }

    @Test
    fun `abrir notificaciones o filtros cierra el redactor y viceversa`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.openNotifications()
        assertFalse(state.showMessageComposer)
        assertEquals("", state.messageDraft)

        vm.openMessageComposer()
        assertTrue(state.showMessageComposer)
        assertFalse(state.showNotifications)

        vm.openFilters()
        assertFalse(state.showMessageComposer)
        vm.openMessageComposer()
        assertFalse(state.showFilters)
    }

    @Test
    fun `un envio exitoso queda en la bandeja y marca la tarjeta como ya escrita`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("  Hola  ")
        vm.sendMessage()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(MessageSendState.Sent, state.messageSendState)
        assertEquals(1, state.sentMessages.size)
        assertEquals("Hola", state.sentMessages.single().text) // se envía recortado
        assertEquals("Ángela R.", state.sentMessages.single().recipientName)
        assertTrue(state.profileAlreadyMessaged)
    }

    @Test
    fun `el aviso de ya escrito es solo de esa tarjeta y vuelve al deshacer`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        dispatcher.scheduler.advanceUntilIdle()
        vm.closeMessageComposer()
        assertTrue(state.profileAlreadyMessaged)

        vm.onAccept()
        assertFalse(state.profileAlreadyMessaged)
        vm.onUndo()
        assertTrue(state.profileAlreadyMessaged)
    }

    @Test
    fun `cerrar durante el envio no deja nada en la bandeja`() {
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        vm.closeMessageComposer()
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(state.sentMessages.isEmpty())
        assertFalse(state.profileAlreadyMessaged)
    }

    @Test
    fun `si el envio falla queda en Failed, conserva el borrador y no registra nada`() {
        vm.messageSender = MessageSender { _, _ -> false }
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(MessageSendState.Failed, state.messageSendState)
        assertEquals("Hola", state.messageDraft)
        assertTrue(state.canSendMessage) // "Reintentar"
        assertTrue(state.sentMessages.isEmpty())
        assertFalse(state.profileAlreadyMessaged)
    }

    @Test
    fun `reintentar tras un fallo puede terminar bien y registra un solo mensaje`() {
        var attempts = 0
        vm.messageSender = MessageSender { _, _ -> ++attempts > 1 }
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(MessageSendState.Failed, state.messageSendState)

        vm.sendMessage()
        assertEquals(MessageSendState.Sending, state.messageSendState)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(MessageSendState.Sent, state.messageSendState)
        assertEquals(1, state.sentMessages.size)
    }

    @Test
    fun `editar tras un fallo quita el error`() {
        vm.messageSender = MessageSender { _, _ -> false }
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        dispatcher.scheduler.advanceUntilIdle()

        vm.updateMessageDraft("Hola, ¿qué tal?")
        assertEquals(MessageSendState.Idle, state.messageSendState)
        assertEquals("Hola, ¿qué tal?", state.messageDraft)
    }

    @Test
    fun `un error inesperado del sender cuenta como fallo`() {
        vm.messageSender = MessageSender { _, _ -> error("sin red") }
        vm.openMessageComposer()
        vm.updateMessageDraft("Hola")
        vm.sendMessage()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(MessageSendState.Failed, state.messageSendState)
    }

    @Test
    fun `sin tarjeta no se puede abrir el redactor`() {
        // Se pasan todos los perfiles hasta que no queda ninguno
        repeat(10) { vm.onReject() }
        assertEquals(null, state.profile)
        vm.openMessageComposer()
        assertFalse(state.showMessageComposer)
    }
}
