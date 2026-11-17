package com.gotalk.app

import com.gotalk.app.presentation.discover.MaxMessageLength
import com.gotalk.app.presentation.discover.fakeDiscoverProfiles
import com.gotalk.app.presentation.discover.messageSuggestions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageSuggestionsTest {

    @Test
    fun `usan el primer nombre y los idiomas del GoTalker`() {
        val suggestions = messageSuggestions(fakeDiscoverProfiles[0]) // Ángela R., español nativo, aprende inglés
        assertTrue(suggestions.any { it.contains("Ángela") && !it.contains("R.") })
        assertTrue(suggestions.any { it.contains("español") })
        assertTrue(suggestions.any { it.contains("inglés") })
    }

    @Test
    fun `todas caben en el limite de caracteres`() {
        fakeDiscoverProfiles.forEach { profile ->
            messageSuggestions(profile).forEach { assertTrue(it.length <= MaxMessageLength) }
        }
    }

    @Test
    fun `sin idiomas solo queda la sugerencia general`() {
        val bare = fakeDiscoverProfiles[0].copy(nativeLanguage = "", learningLanguage = "")
        assertEquals(1, messageSuggestions(bare).size)
    }
}
