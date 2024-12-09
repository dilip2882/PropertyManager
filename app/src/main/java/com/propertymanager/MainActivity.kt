package com.propertymanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.propertymanager.navigation.Destination
import propertymanager.feature.auth.presentation.PhoneScreen
import com.propertymanager.ui.theme.PropertyManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import propertymanager.feature.auth.presentation.AuthViewModel
import propertymanager.feature.auth.presentation.OtpScreen
import propertymanager.presentation.components.AuthAppBar
import propertymanager.presentation.components.HomeAppBar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PropertyManagerTheme {
                val navController = rememberNavController()

                Scaffold(
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Destination.PhoneScreen,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Destination.PhoneScreen>(
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Start,
                                    animationSpec = tween(700)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.End,
                                    animationSpec = tween(700)
                                )
                            }
                        ) {
                            val viewModel: AuthViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsState()
                            val effect by viewModel.effect.collectAsState(initial = null)

                            val onNavigateToOtpScreen: (String) -> Unit = { phoneNumber ->
                                navController.navigate(Destination.OtpScreen(phoneNumber))
                            }

                            PhoneScreen(
                                state = state,
                                effect = effect,
                                dispatch = { event -> viewModel.event(event) },
                                onNavigateToOtpScreen = onNavigateToOtpScreen
                            )
                        }

                        composable<Destination.OtpScreen>(
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Start,
                                    animationSpec = tween(700)
                                )
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.End,
                                    animationSpec = tween(700)
                                )
                            }
                        ) {
                            val args = it.toRoute<Destination.OtpScreen>()
                            val viewModel: AuthViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsState()
                            val effect by viewModel.effect.collectAsState(initial = null)

                            OtpScreen(
                                state = state,
                                effect = effect,
                                dispatch = { event -> viewModel.event(event) }
                            )
                        }
                    }
                }
            }
        }
    }
}
