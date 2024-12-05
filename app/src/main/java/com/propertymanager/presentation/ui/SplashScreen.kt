package com.propertymanager.presentation.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.propertymanager.R
import com.propertymanager.presentation.navigation.Destinations
import com.propertymanager.presentation.ui.auth.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, viewModel: AuthViewModel) {
    val authValue = viewModel.isUserAuthenticated
    val scale = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.5f,
            animationSpec = tween(durationMillis = 1500, easing = {
                OvershootInterpolator(2f).getInterpolation(it)
            })
        )
        delay(3000L)
        if (authValue.value) {
            navController.navigate(Destinations.HomeScreen.route) {
                popUpTo(Destinations.SplashScreen.route) {
                    inclusive = true
                }
            }
        } else {
            navController.navigate(Destinations.PhoneScreen.route) {
                popUpTo(Destinations.SplashScreen.route) {
                    inclusive = true
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Splash Screen Logo",
        )
    }

}