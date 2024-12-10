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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.propertymanager.navigation.Destination
import com.propertymanager.ui.theme.PropertyManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import propertymanager.feature.auth.presentation.AuthViewModel
import propertymanager.feature.auth.presentation.OtpScreen
import propertymanager.feature.auth.presentation.PhoneScreen
import propertymanager.feature.landlord.presentation.LandlordScreen
import propertymanager.feature.staff.presentation.StaffScreen
import propertymanager.feature.tenant.presentation.TenantScreen
import propertymanager.presentation.home.HomeScreen
import propertymanager.presentation.onboarding.OnboardingFormScreen
import propertymanager.presentation.onboarding.OnboardingViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PropertyManagerTheme {
                val navController = rememberNavController()

                Scaffold { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Destination.PhoneScreen,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        // Phone Screen
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
                            },
                        ) {
                            val viewModel: AuthViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsState()
                            val effect by viewModel.effect.collectAsState(initial = null)

                            PhoneScreen(
                                state = state,
                                effect = effect,
                                dispatch = { event -> viewModel.event(event) },
                                onNavigateToOtpScreen = { phoneNumber ->
                                    navController.navigate(Destination.OtpScreen(phoneNumber))
                                }
                            )
                        }

                        // OTP Screen
                        composable<Destination.OtpScreen>(
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
                            },
                        ) {
                            val args = it.toRoute<Destination.OtpScreen>()
                            val viewModel: AuthViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsState()
                            val effect by viewModel.effect.collectAsState(initial = null)

                            OtpScreen(
                                state = state,
                                effect = effect,
                                dispatch = { event -> viewModel.event(event) },
                                onNavigateToPhoneScreen = { navController.navigate(Destination.PhoneScreen) },
                                onNavigateToOnboarding = {
                                    navController.navigate(Destination.OnboardingFormScreen) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                    }
                                },
                                phoneNumber = args.phoneNumber,
                            )
                        }

                        // Onboarding Form Screen
                        composable<Destination.OnboardingFormScreen>(
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
                            },
                        ) {
                            val viewModel: OnboardingViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsState()
                            val effect by viewModel.effect.collectAsState(initial = null)

                            OnboardingFormScreen(
                                state = state,
                                effect = effect,
                                dispatch = { event -> viewModel.event(event) },
                                onComplete = { navController.navigate(Destination.HomeScreen) },
                            )
                        }

                        // Home Screen
                        composable<Destination.HomeScreen>(
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
                            },
                        ) {
                            HomeScreen(
                                onNavigateToTenantScreen = {
                                    navController.navigate(Destination.TenantScreen)
                                },
                                onNavigateToLandlordScreen = {
                                    navController.navigate(Destination.LandlordScreen)
                                },
                                onNavigateToManagerScreen = {
                                    navController.navigate(Destination.StaffScreen)
                                },
                            )
                        }

                        // Tenant Screen
                        composable<Destination.TenantScreen>(
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
                            },
                        ) {
                            TenantScreen()
                        }

                        // Landlord Screen
                        composable<Destination.LandlordScreen>(
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
                            },
                        ) {
                            LandlordScreen()
                        }

                        // Staff Screen
                        composable<Destination.StaffScreen>(
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
                            },
                        ) {
                            StaffScreen()
                        }
                    }

                }
            }
        }
    }
}
