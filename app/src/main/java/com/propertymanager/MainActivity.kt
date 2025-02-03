package com.propertymanager

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.propertymanager.common.preferences.temp.AppPreferences
import com.propertymanager.common.system.dpToPx
import com.propertymanager.domain.model.biometrics.BiometricAuthState
import com.propertymanager.domain.model.Role
import com.propertymanager.navigation.MainNavigation
import com.propertymanager.navigation.SubGraph
import com.propertymanager.ui.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import propertymanager.feature.staff.settings.BiometricViewModel
import propertymanager.feature.staff.settings.ThemeViewModel
import propertymanager.presentation.theme.PropertyManagerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var appPreferences: AppPreferences

    // To be checked by splash screen. If true then splash screen will be removed.
    var ready = false

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val isLaunch = savedInstanceState == null

        // Prevent splash screen showing up on configuration changes
        val splashScreen = if (isLaunch) installSplashScreen() else null

        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: ThemeViewModel = hiltViewModel()
            val biometricViewModel: BiometricViewModel = hiltViewModel()

            val dynamicColor by viewModel.dynamicColor.collectAsState()
            val darkMode by viewModel.darkMode.collectAsState()

            val hasAuthenticated by biometricViewModel.hasAuthenticated.collectAsState()
            val biometricAuthState by biometricViewModel
                .biometricAuthState
                .collectAsState(BiometricAuthState.LOADING)
            val biometricAuthResult by biometricViewModel.authResult.collectAsState()

            LaunchedEffect(biometricAuthState) {
                if (biometricAuthState == BiometricAuthState.ENABLED) {
                    biometricViewModel.authenticate(this@MainActivity)
                }
            }

            biometricViewModel.handleBiometricAuth(biometricAuthResult, this)

            navController = rememberNavController()
            PropertyManagerTheme(
                darkTheme = darkMode,
                dynamicColor = dynamicColor
            ) {
                MainNavigation(
                    navController = navController!!,
                    appPreferences = appPreferences
                )
            }
        }

        val startTime = System.currentTimeMillis()
        splashScreen?.setKeepOnScreenCondition {
            val elapsed = System.currentTimeMillis() - startTime
            elapsed <= SPLASH_MIN_DURATION || (!ready && elapsed <= SPLASH_MAX_DURATION)
        }
        setSplashScreenExitAnimation(splashScreen)
    }

    /**
     * Sets custom splash screen exit animation on devices prior to Android 12.
     *
     * When custom animation is used, status and navigation bar color will be set to transparent and will be restored
     * after the animation is finished.
     */
    @Suppress("Deprecation")
    private fun setSplashScreenExitAnimation(splashScreen: SplashScreen?) {
        val root = findViewById<View>(android.R.id.content)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && splashScreen != null) {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT

            splashScreen.setOnExitAnimationListener { splashProvider ->
                // For some reason the SplashScreen applies (incorrect) Y translation to the iconView
                splashProvider.iconView.translationY = 0F

                val activityAnim = ValueAnimator.ofFloat(1F, 0F).apply {
                    interpolator = LinearOutSlowInInterpolator()
                    duration = SPLASH_EXIT_ANIM_DURATION
                    addUpdateListener { va ->
                        val value = va.animatedValue as Float
                        root.translationY = value * 16.dpToPx
                    }
                }

                val splashAnim = ValueAnimator.ofFloat(1F, 0F).apply {
                    interpolator = FastOutSlowInInterpolator()
                    duration = SPLASH_EXIT_ANIM_DURATION
                    addUpdateListener { va ->
                        val value = va.animatedValue as Float
                        splashProvider.view.alpha = value
                    }
                    doOnEnd {
                        splashProvider.remove()
                    }
                }

                activityAnim.start()
                splashAnim.start()
            }
        }
    }

    override fun handleRoleBasedNavigation(role: Role) {
        navController?.let { navController ->
            when (role) {
                Role.TENANT -> navController.navigate(SubGraph.Tenant)
                Role.LANDLORD -> navController.navigate(SubGraph.Landlord)
                Role.MANAGER -> navController.navigate(SubGraph.Staff)
            }
        }
    }
}

// Splash screen
private const val SPLASH_MIN_DURATION = 500 // ms
private const val SPLASH_MAX_DURATION = 5000 // ms
private const val SPLASH_EXIT_ANIM_DURATION = 400L // ms
