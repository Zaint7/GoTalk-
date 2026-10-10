package com.gotalk.app.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gotalk.app.R
import com.gotalk.app.ui.components.PrimaryButton
import com.gotalk.app.ui.components.TabSelector
import com.gotalk.app.ui.components.UnderlinedTextField
import com.gotalk.app.ui.theme.GoTalkCardBorder
import com.gotalk.app.ui.theme.GoTalkTextPrimary
import com.gotalk.app.ui.theme.GoTalkTheme
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.GoTalkYellowLight

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bloque superior (462 dp) + tarjeta superpuesta: la tarjeta empieza en 300 dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Bloque amarillo con esquinas inferiores redondeadas
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 412.dp)
                    .fillMaxWidth()
                    .height(462.dp)
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.Black,
                                    0.55f to Color.Black,
                                    1f to Color.Transparent
                                )
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
                    .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                    .background(Brush.verticalGradient(listOf(GoTalkYellowLight, GoTalkYellow)))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_saludos),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 1f
                )
            }

            Image(
                painter = painterResource(id = R.drawable.logo_gotalk),
                contentDescription = "GoTalk",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 115.dp)
                    .size(width = 288.dp, height = 90.dp)
            )

            val cardShape = RoundedCornerShape(40.dp)
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 300.dp)
                    .width(316.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = cardShape,
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(cardShape)
                    .background(Color.White)
                    .border(1.dp, GoTalkCardBorder, cardShape)
                    .padding(top = 24.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Bienvenido a GoTalk!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoTalkTextPrimary
                )

                Spacer(modifier = Modifier.height(33.dp))

                TabSelector(
                    tabs = listOf("inicio de sesion", "registrarse"),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(27.dp))

                UnderlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Correo",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.width(210.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                UnderlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Contraseña",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.width(210.dp)
                )

                Spacer(modifier = Modifier.height(26.dp))

                Row(
                    modifier = Modifier.width(210.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        R.drawable.ic_facebook,
                        R.drawable.ic_instagram,
                        R.drawable.ic_google,
                        R.drawable.ic_phone
                    ).forEach { icon ->
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                PrimaryButton(
                    text = "iniciar sesion",
                    onClick = { onLoginSuccess() },
                    modifier = Modifier.width(232.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoTalkTextPrimary,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Políticas de privacidad",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9E9E9E),
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    GoTalkTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
