package com.propertymanager.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.propertymanager.presentation.navigation.Destinations
import com.propertymanager.presentation.ui.SplashScreen
import com.propertymanager.presentation.ui.auth.AuthViewModel
import com.propertymanager.presentation.ui.auth.OtpScreen
import com.propertymanager.presentation.ui.auth.PhoneScreen
import com.propertymanager.presentation.ui.main.HomeScreen
import com.propertymanager.presentation.ui.main.SettingsScreen
import com.propertymanager.presentation.ui.theme.PropertyManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PropertyManagerTheme {
                val navController = rememberNavController()
                val authViewModel = hiltViewModel<AuthViewModel>()
                PropertyManagerApp(navController, authViewModel)
            }
        }
    }
}

@Composable
fun PropertyManagerApp(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = Destinations.SplashScreen.route) {
        composable(route = Destinations.PhoneScreen.route) {
            PhoneScreen(navController = navController, viewModel = authViewModel)
        }
        composable(route = Destinations.OtpScreen.route + "/{phoneNumber}") { backStackEntry ->
            OtpScreen(
                navController = navController,
                viewModel = authViewModel,
                phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            )
        }
        composable(route = Destinations.SplashScreen.route) {
            SplashScreen(navController = navController, viewModel = authViewModel)
        }
        composable(route = Destinations.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = Destinations.SettingsScreen.route) {
            SettingsScreen(navController)
        }
    }
}
