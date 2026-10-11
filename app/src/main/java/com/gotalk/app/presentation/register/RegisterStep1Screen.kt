package com.gotalk.app.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.components.GoTalkDateField
import com.gotalk.app.ui.components.GoTalkOutlinedField
import com.gotalk.app.ui.components.GoTalkPillButton
import com.gotalk.app.ui.components.GoTalkSelectField
import com.gotalk.app.ui.components.GoTalkTextArea
import com.gotalk.app.ui.components.StepHeader
import com.gotalk.app.ui.components.WaveTop
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkInk

private val Genders = listOf("Femenino", "Masculino", "No binario", "Prefiero no decirlo")
internal val Countries = listOf(
    "Colombia", "México", "Argentina", "España", "Estados Unidos",
    "Brasil", "Francia", "Alemania", "Japón"
)

@Composable
fun RegisterStep1Screen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Long?>(null) }
    var gender by remember { mutableStateOf<String?>(null) }
    var country by remember { mutableStateOf<String?>(null) }
    var about by remember { mutableStateOf("") }

    val canContinue = firstName.isNotBlank() && lastName.isNotBlank() &&
        birthDate != null && gender != null && country != null

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(GoTalkCream)
    ) {
        // Con pantalla alta, SpaceBetween deja el botón al final; con pantalla baja el contenido hace scroll.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .heightIn(min = maxHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WaveTop(modifier = Modifier.fillMaxWidth().height(36.dp))

                StepHeader(step = 1, total = 2, onBack = onBack)

                Spacer(Modifier.height(40.dp))
                Text(
                    text = "¡Conozcámonos!",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoTalkInk,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Completa tu perfil para conectar con personas de todo el mundo.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 17.sp,
                    color = GoTalkInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp)
                )

                Column(modifier = Modifier.padding(horizontal = 28.dp)) {
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GoTalkOutlinedField(
                            leadingIcon = Icons.Default.Person,
                            placeholder = "Nombre",
                            value = firstName,
                            onValueChange = { firstName = it },
                            modifier = Modifier.weight(1f)
                        )
                        GoTalkOutlinedField(
                            leadingIcon = Icons.Default.Person,
                            placeholder = "Apellido",
                            value = lastName,
                            onValueChange = { lastName = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(17.dp))
                    GoTalkDateField(
                        label = "Fecha de nacimiento",
                        placeholder = "DD/MM/AAAA",
                        selectedMillis = birthDate,
                        onDateSelected = { birthDate = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(18.dp))
                    // TODO: ícono de género (ic_gender.xml de Figma); Person es temporal.
                    GoTalkSelectField(
                        leadingIcon = Icons.Default.Person,
                        label = "Género",
                        placeholder = "Selecciona una opción",
                        selected = gender,
                        options = Genders,
                        onSelected = { gender = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(18.dp))
                    GoTalkSelectField(
                        leadingIcon = Icons.Default.Public,
                        label = "País de origen",
                        placeholder = "Selecciona una opción",
                        selected = country,
                        options = Countries,
                        onSelected = { country = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(18.dp))
                    GoTalkTextArea(
                        placeholder = "Cuéntanos algo sobre ti (opcional)",
                        value = about,
                        onValueChange = { about = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .navigationBarsPadding()
            ) {
                Spacer(Modifier.height(28.dp))
                GoTalkPillButton(
                    text = "Continuar",
                    onClick = onContinue,
                    enabled = canContinue,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Preview(name = "Pixel 4", device = "id:pixel_4", showSystemUi = true)
@Composable
private fun RegisterStep1ScreenPreview() {
    RegisterStep1Screen(onBack = {}, onContinue = {})
}

@Preview(name = "Telefono pequeño 320x600", widthDp = 320, heightDp = 600)
@Composable
private fun RegisterStep1ScreenSmallPreview() {
    RegisterStep1Screen(onBack = {}, onContinue = {})
}
