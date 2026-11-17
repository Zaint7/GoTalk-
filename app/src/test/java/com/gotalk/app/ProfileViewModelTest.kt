package com.gotalk.app

import com.gotalk.app.presentation.discover.ProfileLanguage
import com.gotalk.app.presentation.profile.MaxAboutLength
import com.gotalk.app.presentation.profile.ProfileError
import com.gotalk.app.presentation.profile.ProfileViewModel
import com.gotalk.app.presentation.profile.fakeUserProfile
import com.gotalk.app.presentation.profile.toDiscoverProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileViewModelTest {

    @Test
    fun startsWithoutChanges() {
        val state = ProfileViewModel().uiState.value
        assertFalse(state.hasChanges)
        assertFalse(state.canSave)
        assertTrue(state.validationErrors.isEmpty())
    }

    @Test
    fun editsGoToDraftNotToSavedProfile() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(city = "Sevilla") }
        val state = vm.uiState.value
        assertTrue(state.hasChanges)
        assertEquals("Sevilla", state.draft.city)
        assertEquals(fakeUserProfile.city, state.profile.city)
    }

    @Test
    fun saveTurnsDraftIntoProfile() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(city = "Sevilla") }
        assertTrue(vm.save())
        val state = vm.uiState.value
        assertEquals("Sevilla", state.profile.city)
        assertFalse(state.hasChanges)
    }

    @Test
    fun revertingTheEditClearsHasChanges() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(city = "Sevilla") }
        vm.updateDraft { it.copy(city = fakeUserProfile.city) }
        assertFalse(vm.uiState.value.hasChanges)
    }

    @Test
    fun countryAndNativeLanguageAreRequired() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(countryCode = null, nativeLanguage = null) }
        val state = vm.uiState.value
        assertEquals(setOf(ProfileError.COUNTRY_REQUIRED, ProfileError.NATIVE_LANGUAGE_REQUIRED), state.validationErrors)
        assertFalse(state.canSave)
    }

    @Test
    fun saveIsRejectedWhenInvalidAndKeepsSavedProfile() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(countryCode = null) }
        assertFalse(vm.save())
        assertEquals(fakeUserProfile, vm.uiState.value.profile)
        assertTrue(vm.uiState.value.hasChanges)
    }

    @Test
    fun cityIsOptional() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(city = "") }
        assertTrue(vm.uiState.value.validationErrors.isEmpty())
        assertTrue(vm.save())
    }

    @Test
    fun aboutIsCutAtMaxLength() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(about = "a".repeat(MaxAboutLength + 50)) }
        assertEquals(MaxAboutLength, vm.uiState.value.draft.about.length)
    }

    @Test
    fun otherLanguagesDropNativeAndDuplicates() {
        val vm = ProfileViewModel()
        vm.updateDraft {
            it.copy(
                otherLanguages = listOf(
                    ProfileLanguage("Español"), ProfileLanguage("Italiano", level = "A1"), ProfileLanguage("Italiano", level = "B2")
                )
            )
        }
        assertEquals(listOf("Italiano"), vm.uiState.value.draft.otherLanguages.map { it.name })
    }

    @Test
    fun discardRestoresSavedProfile() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(city = "Sevilla") }
        vm.discardChanges()
        assertFalse(vm.uiState.value.hasChanges)
        assertEquals(fakeUserProfile, vm.uiState.value.draft)
    }

    @Test
    fun discoverViewShowsExactlyWhatWasEdited() {
        val seen = fakeUserProfile.toDiscoverProfile()
        assertEquals(fakeUserProfile.countryName, seen.countryName)
        assertEquals(fakeUserProfile.about, seen.about)
        assertEquals(fakeUserProfile.learning, seen.learning)
        // Nativo primero, luego los otros idiomas con su nivel
        assertEquals(
            listOfNotNull(fakeUserProfile.nativeLanguage) + fakeUserProfile.otherLanguages,
            seen.spokenLanguages
        )
    }
}
