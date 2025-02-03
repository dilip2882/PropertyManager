package com.propertymanager.ui.base.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.propertymanager.domain.model.Role
import com.propertymanager.navigation.RoleNavigationHelper
import com.propertymanager.ui.base.delegate.ThemingDelegate
import com.propertymanager.ui.base.delegate.ThemingDelegateImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseActivity : AppCompatActivity(), ThemingDelegate {

    @Inject
    lateinit var themingDelegate: ThemingDelegateImpl

    @Inject
    lateinit var roleNavigationHelper: RoleNavigationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyAppTheme(this)

        // role-based navigation
        lifecycleScope.launch {
            val userRole = roleNavigationHelper.determineUserRole()
            handleRoleBasedNavigation(userRole)
        }
    }

    protected open fun handleRoleBasedNavigation(role: Role) {
        // Override this in MainActivity to handle actual navigation
    }

    override fun applyAppTheme(activity: Activity) {
        themingDelegate.applyAppTheme(activity)
    }
}
