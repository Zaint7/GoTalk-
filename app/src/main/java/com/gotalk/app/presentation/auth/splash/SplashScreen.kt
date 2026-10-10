package com.gotalk.app.presentation.auth.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gotalk.app.R
import com.gotalk.app.ui.theme.GoTalkTheme
import com.gotalk.app.ui.theme.GoTalkYellow

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

    SplashContent(logoAlpha = alphaAnim)
}

@Composable
private fun SplashContent(logoAlpha: Float = 1f) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GoTalkYellow),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(id = R.drawable.logo_gotalk),
            contentDescription = "GoTalk",
            modifier = Modifier
                .size(width = 288.dp, height = 90.dp)
                .alpha(logoAlpha)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    GoTalkTheme {
        SplashContent()
    }
}
