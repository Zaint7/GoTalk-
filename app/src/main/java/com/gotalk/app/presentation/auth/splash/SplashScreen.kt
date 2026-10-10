package com.gotalk.app.presentation.auth.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashScreen(onSplashFinished: (String) -> Unit) {

    val viewModel: SplashViewModel = hiltViewModel()
    val navigateTo by viewModel.navigateTo.collectAsState()

    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "splashAlpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
    }

    LaunchedEffect(navigateTo) {
        navigateTo?.let { destination ->
            onSplashFinished(destination)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "GoTalk",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C63FF),
            modifier = Modifier.alpha(alphaAnim)
        )
    }
}