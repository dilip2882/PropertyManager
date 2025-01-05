package com.propertymanager.ui.base.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propertymanager.ui.base.delegate.ThemingDelegate
import com.propertymanager.ui.base.delegate.ThemingDelegateImpl
import javax.inject.Inject

open class BaseActivity : AppCompatActivity(), ThemingDelegate {

    @Inject
    lateinit var themingDelegate: ThemingDelegateImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyAppTheme(this)
    }

    override fun applyAppTheme(activity: Activity) {

    }
}
