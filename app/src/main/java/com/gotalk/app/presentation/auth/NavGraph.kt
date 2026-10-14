package com.gotalk.app.presentation.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gotalk.app.presentation.auth.login.LoginScreen
import com.gotalk.app.presentation.auth.splash.SplashScreen
import com.gotalk.app.presentation.main.MainScaffold
import com.gotalk.app.presentation.register.RegisterStep1Screen
import com.gotalk.app.presentation.register.RegisterStep2Screen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val REGISTER_STEP1 = "register_step1"
    const val REGISTER_STEP2 = "register_step2"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = { destination ->
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterNext = { navController.navigate(Routes.REGISTER_STEP1) }
            )
        }

        composable(Routes.REGISTER_STEP1) {
            RegisterStep1Screen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.REGISTER_STEP2) }
            )
        }

        composable(Routes.REGISTER_STEP2) {
            RegisterStep2Screen(
                onBack = { navController.popBackStack() },
                onContinue = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            MainScaffold()
        }
    }
}