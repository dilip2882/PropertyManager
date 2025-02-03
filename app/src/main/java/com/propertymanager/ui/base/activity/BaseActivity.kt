package com.propertymanager.ui.base.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.propertymanager.common.preferences.temp.AppPreferences
import com.propertymanager.domain.model.Role
import com.propertymanager.navigation.RoleNavigationHelper
import com.propertymanager.ui.base.delegate.ThemingDelegate
import com.propertymanager.ui.base.delegate.ThemingDelegateImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseActivity : AppCompatActivity(), ThemingDelegate {

    @Inject
    lateinit var themingDelegate: ThemingDelegateImpl

    @Inject
    lateinit var roleNavigationHelper: RoleNavigationHelper

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyAppTheme(this)

        // Check authentication and handle role-based navigation
        lifecycleScope.launch {
            val authToken = appPreferences.getAuthToken().first()
            if (authToken != null) {
                val userRole = roleNavigationHelper.determineUserRole()
                handleRoleBasedNavigation(userRole)
            }
        }

    }

    protected open fun handleRoleBasedNavigation(role: Role) {

    }

    override fun applyAppTheme(activity: Activity) {
        themingDelegate.applyAppTheme(activity)
    }
}
