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
    fun errorsAreHiddenUntilSaveIsAttempted() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(countryCode = null) }
        assertTrue(vm.uiState.value.visibleErrors.isEmpty())
        assertFalse(vm.save())
        assertEquals(setOf(ProfileError.COUNTRY_REQUIRED), vm.uiState.value.visibleErrors)
    }

    @Test
    fun visibleErrorDisappearsWhenFieldIsFixed() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(countryCode = null) }
        vm.save()
        vm.updateDraft { it.copy(countryCode = "FR") }
        assertTrue(vm.uiState.value.visibleErrors.isEmpty())
        assertTrue(vm.save())
        assertFalse(vm.uiState.value.showErrors)
    }

    @Test
    fun discardHidesErrors() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(countryCode = null) }
        vm.save()
        vm.discardChanges()
        assertFalse(vm.uiState.value.showErrors)
        assertTrue(vm.uiState.value.visibleErrors.isEmpty())
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
    fun pickingAPhotoIsADraftChangeUntilSaved() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(photoUri = "file:///fotos/photo_1.img") }
        assertTrue(vm.uiState.value.hasChanges)
        assertEquals(null, vm.uiState.value.profile.photoUri)
        assertTrue(vm.save())
        assertEquals("file:///fotos/photo_1.img", vm.uiState.value.profile.photoUri)
        assertFalse(vm.uiState.value.hasChanges)
    }

    @Test
    fun discardingRestoresThePreviousPhoto() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(photoUri = "file:///fotos/photo_2.img") }
        vm.discardChanges()
        assertEquals(null, vm.uiState.value.draft.photoUri)
        assertFalse(vm.uiState.value.hasChanges)
    }

    @Test
    fun photoChangeAloneDoesNotNeedOtherFields() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(photoUri = "file:///fotos/photo_3.img") }
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun blankCountryCountsAsMissing() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(countryCode = "  ") }
        assertEquals(setOf(ProfileError.COUNTRY_REQUIRED), vm.uiState.value.validationErrors)
    }

    @Test
    fun savedProfileIsWhatDiscoverShowsAfterEditing() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(countryCode = "FR", about = "Bonjour", nativeLanguage = ProfileLanguage("Francés")) }
        assertTrue(vm.save())
        val seen = vm.uiState.value.profile.toDiscoverProfile()
        assertEquals("Francia", seen.countryName)
        assertEquals("FR", seen.countryCode)
        assertEquals("Bonjour", seen.about)
        assertEquals("Francés", seen.spokenLanguages.first().name)
    }

    @Test
    fun unsavedDraftDoesNotReachDiscover() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(about = "Sin guardar") }
        assertEquals(fakeUserProfile.about, vm.uiState.value.profile.toDiscoverProfile().about)
    }

    @Test
    fun cleaningTheDescriptionIsAChangeAndStillValid() {
        val vm = ProfileViewModel()
        vm.updateDraft { it.copy(about = "") }
        assertTrue(vm.uiState.value.hasChanges)
        assertTrue(vm.save())
        assertEquals("", vm.uiState.value.profile.about)
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
