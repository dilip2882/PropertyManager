package com.propertymanager.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import propertymanager.feature.auth.presentation.AuthViewModel
import propertymanager.feature.auth.presentation.OtpScreen
import propertymanager.feature.auth.presentation.PhoneScreen
import propertymanager.feature.landlord.presentation.LandlordScreen
import propertymanager.feature.staff.presentation.StaffScreen
import propertymanager.feature.tenant.presentation.TenantScreen
import propertymanager.presentation.home.HomeScreen
import propertymanager.presentation.onboarding.OnboardingFormScreen
import propertymanager.presentation.onboarding.OnboardingViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SubGraph.Auth) {

        // Authentication Subgraph
        navigation<SubGraph.Auth>(startDestination = Dest.PhoneScreen) {
            composable<Dest.PhoneScreen>(
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
                        navController.navigate(Dest.OtpScreen(phoneNumber))
                    }
                )
            }

            // OTP Screen
            composable<Dest.OtpScreen>(
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
                val args = it.toRoute<Dest.OtpScreen>()
                val viewModel: AuthViewModel = hiltViewModel()
                val state by viewModel.state.collectAsState()
                val effect by viewModel.effect.collectAsState(initial = null)

                OtpScreen(
                    state = state,
                    effect = effect,
                    dispatch = { event -> viewModel.event(event) },
                    onNavigateToPhoneScreen = { navController.navigate(Dest.PhoneScreen) },
                    onNavigateToOnboarding = {
                        navController.navigate(Dest.OnboardingFormScreen) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    phoneNumber = args.phoneNumber,
                )
            }

            // Onboarding Form Screen
            composable<Dest.OnboardingFormScreen>(
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
                    onComplete = { navController.navigate(Dest.HomeScreen) },
                )
            }

        }

        // Home Subgraph
        navigation<SubGraph.Home>(startDestination = Dest.HomeScreen) {
            // Home Screen
            composable<Dest.HomeScreen>(
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
                        navController.navigate(Dest.TenantScreen) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToLandlordScreen = {
                        navController.navigate(Dest.LandlordScreen)
                    },
                    onNavigateToManagerScreen = {
                        navController.navigate(Dest.StaffScreen)
                    },
                )
            }
        }

        // Tenant Subgraph
        navigation<SubGraph.Tenant>(startDestination = Dest.TenantScreen) {
            composable<Dest.TenantScreen> {
                TenantScreen()
            }
        }

        // Landlord Subgraph
        navigation<SubGraph.Landlord>(startDestination = Dest.LandlordScreen) {
            composable<Dest.LandlordScreen> {
                LandlordScreen()
            }
        }

        // Staff Subgraph
        navigation<SubGraph.Staff>(startDestination = Dest.StaffScreen) {
            composable<Dest.StaffScreen> {
                StaffScreen()
            }
        }
    }
}
