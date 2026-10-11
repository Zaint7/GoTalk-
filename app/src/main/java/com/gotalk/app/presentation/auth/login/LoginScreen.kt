package com.gotalk.app.presentation.auth.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gotalk.app.R
import com.gotalk.app.ui.components.PrimaryButton
import com.gotalk.app.ui.components.SocialButton
import com.gotalk.app.ui.components.TabSelector
import com.gotalk.app.ui.components.UnderlinedTextField
import com.gotalk.app.ui.theme.GoTalkCardBorder
import com.gotalk.app.ui.theme.GoTalkTextPrimary
import com.gotalk.app.ui.theme.GoTalkTheme
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.GoTalkYellowLight

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterNext: () -> Unit
) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val isRegisterValid = email.contains("@") &&
        email.substringAfter("@").contains(".") &&
        password.length >= 6

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val screenHeight = maxHeight
        val topBlockHeight = screenHeight * 0.48f
        val logoTopPadding = screenHeight * 0.10f
        val cardOverlap = screenHeight * 0.10f
        val cardShape = RoundedCornerShape(40.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // heightIn(min) hace que los weight funcionen dentro del scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = screenHeight),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bloque amarillo (48% de la altura de pantalla)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topBlockHeight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
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
                            .padding(top = logoTopPadding)
                            .fillMaxWidth(0.7f)
                            .widthIn(max = 288.dp)
                            .aspectRatio(288f / 90f)
                    )
                }

                // Tarjeta superpuesta al borde inferior del bloque amarillo
                Column(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val overlap = cardOverlap.roundToPx().coerceAtMost(placeable.height)
                            layout(placeable.width, placeable.height - overlap) {
                                placeable.place(0, -overlap)
                            }
                        }
                        .fillMaxWidth(0.80f)
                        .widthIn(max = 320.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = cardShape,
                            ambientColor = Color.Black,
                            spotColor = Color.Black
                        )
                        .clip(cardShape)
                        .background(Color.White)
                        .border(1.dp, GoTalkCardBorder, cardShape)
                        .padding(horizontal = 28.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    AnimatedContent(targetState = selectedTab, label = "cardContent") { tab ->
                        if (tab == 0) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Bienvenido a GoTalk!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = GoTalkTextPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Column(
                                    modifier = Modifier
                                        .widthIn(max = 233.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    TabSelector(
                                        tabs = listOf("inicio de sesion", "registrarse"),
                                        selectedIndex = selectedTab,
                                        onTabSelected = { selectedTab = it },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(22.dp))

                                    UnderlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        placeholder = "Correo",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    UnderlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        placeholder = "Contraseña",
                                        isPassword = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(22.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
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
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    PrimaryButton(
                                        text = "iniciar sesion",
                                        onClick = { onLoginSuccess() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(22.dp))

                                Text(
                                    text = "¿Olvidaste tu contraseña?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoTalkTextPrimary,
                                    textDecoration = TextDecoration.Underline,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = 233.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TabSelector(
                                    tabs = listOf("inicio de sesion", "registrarse"),
                                    selectedIndex = selectedTab,
                                    onTabSelected = { selectedTab = it },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(26.dp))

                                Text(
                                    text = "¡Completa tu registro!",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = GoTalkTextPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(30.dp))

                                UnderlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    placeholder = "Correo",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                UnderlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = "Contraseña",
                                    isPassword = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                SocialButton(
                                    icon = R.drawable.ic_facebook,
                                    text = "Facebook",
                                    onClick = { /* TODO: registro con Facebook */ }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                SocialButton(
                                    icon = R.drawable.ic_instagram,
                                    text = "Instagram",
                                    onClick = { /* TODO: registro con Instagram */ }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                SocialButton(
                                    icon = R.drawable.ic_google,
                                    text = "Gmail",
                                    onClick = { /* TODO: registro con Google */ }
                                )

                                Spacer(modifier = Modifier.height(30.dp))

                                PrimaryButton(
                                    text = "siguiente",
                                    onClick = onRegisterNext,
                                    enabled = isRegisterValid,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                )
                            }
                        }
                    }
                }

                // Políticas centradas en el espacio restante, mínimo 24 dp arriba y abajo
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Políticas de privacidad",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.navigationBarsPadding()
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(name = "Pixel 4", showBackground = true, device = Devices.PIXEL_4)
@Composable
private fun LoginScreenPixel4Preview() {
    GoTalkTheme {
        LoginScreen(onLoginSuccess = {}, onRegisterNext = {})
    }
}

@Preview(name = "Pixel 7 Pro", showBackground = true, device = Devices.PIXEL_7_PRO)
@Composable
private fun LoginScreenPixel7ProPreview() {
    GoTalkTheme {
        LoginScreen(onLoginSuccess = {}, onRegisterNext = {})
    }
}

@Preview(name = "Telefono pequeno", showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun LoginScreenSmallPreview() {
    GoTalkTheme {
        LoginScreen(onLoginSuccess = {}, onRegisterNext = {})
    }
}
