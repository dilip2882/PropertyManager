package com.propertymanager.navigation.graphs

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.propertymanager.domain.model.Role
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.auth.presentation.AuthViewModel
import propertymanager.feature.auth.presentation.OtpScreen
import propertymanager.feature.auth.presentation.PhoneScreen
import propertymanager.feature.onboarding.OnboardingFormScreen
import propertymanager.feature.onboarding.OnboardingViewModel

fun NavGraphBuilder.authNavGraph(navController: NavController) {
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
            val selectedRole by viewModel.existingRole.collectAsState()

            OnboardingFormScreen(
                state = state,
                effect = effect,
                dispatch = { event -> viewModel.event(event) },
                onComplete = {
                    val destination = when (selectedRole) {
                        Role.TENANT.toString() -> Dest.TenantScreen
                        Role.LANDLORD.toString() -> Dest.LandlordScreen
                        Role.MANAGER.toString() -> Dest.StaffScreen
                        else -> Dest.TenantScreen
                    }
                    navController.navigate(destination) {
                        popUpTo(SubGraph.Auth) { inclusive = true }
                    }
                }
            )
        }
    }
}
