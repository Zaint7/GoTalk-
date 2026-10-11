package com.gotalk.app.presentation.register

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.components.BackCircleButton
import com.gotalk.app.ui.components.FilterDropdown
import com.gotalk.app.ui.components.GoTalkPillButton
import com.gotalk.app.ui.components.PhotoPickerBox
import com.gotalk.app.ui.components.WaveBottom
import com.gotalk.app.ui.components.WaveTop
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkInk

private val Languages = listOf(
    "Español", "Inglés", "Francés", "Alemán", "Portugués", "Italiano", "Japonés", "Coreano"
)
private val GoTalkerGenders = listOf("Sin preferencia", "Femenino", "Masculino", "No binario")

@Composable
fun RegisterStep2Screen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var language by rememberSaveable { mutableStateOf<String?>(null) }
    var country by rememberSaveable { mutableStateOf<String?>(null) }
    var gender by rememberSaveable { mutableStateOf<String?>(null) }
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) photoUri = uri.toString() }

    val canContinue = language != null && country != null
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(GoTalkCream)
    ) {
        val photoSize = if (maxWidth < 320.dp) 140.dp else 168.dp

        WaveTop(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(statusBarTop + 50.dp)
        )
        WaveBottom(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(50.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackCircleButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 28.dp, top = 8.dp)
            )

            Column(
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Completa los filtros",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoTalkInk,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(40.dp))
                FilterDropdown(
                    label = "Selecciona el idioma de tu interés",
                    placeholder = "Selecciona una opción",
                    selected = language,
                    options = Languages,
                    onSelected = { language = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(28.dp))
                FilterDropdown(
                    label = "Selecciona el país de tu interés",
                    placeholder = "Selecciona una opción",
                    selected = country,
                    options = Countries,
                    onSelected = { country = it },
                    modifier = Modifier.fillMaxWidth()
                )

                // Opcional: el texto informativo hace de etiqueta del tercer desplegable.
                Spacer(Modifier.height(26.dp))
                FilterDropdown(
                    label = "Puedes seleccionar el género de tu GoTalker",
                    placeholder = "Selecciona una opción",
                    selected = gender,
                    options = GoTalkerGenders,
                    onSelected = { gender = it },
                    labelCentered = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Por último selecciona una foto de perfil",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    color = GoTalkInk,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))
                PhotoPickerBox(
                    imageUri = photoUri,
                    onClick = {
                        photoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    size = photoSize
                )

                Spacer(Modifier.height(39.dp))
                GoTalkPillButton(
                    text = "Continuar",
                    onClick = onContinue,
                    enabled = canContinue,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier.fillMaxWidth()
                )
                // 32 dp bajo el botón; la ola inferior queda detrás del contenido.
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Preview(name = "Pixel 4", device = "id:pixel_4", showSystemUi = true)
@Composable
private fun RegisterStep2ScreenPreview() {
    RegisterStep2Screen(onBack = {}, onContinue = {})
}

@Preview(name = "Telefono pequeño 320x600", widthDp = 320, heightDp = 600)
@Composable
private fun RegisterStep2ScreenSmallPreview() {
    RegisterStep2Screen(onBack = {}, onContinue = {})
}
