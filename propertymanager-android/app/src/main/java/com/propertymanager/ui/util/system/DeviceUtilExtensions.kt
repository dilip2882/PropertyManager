package com.propertymanager.ui.util.system

import android.os.Build
import com.google.android.material.color.DynamicColors
import com.propertymanager.common.system.DeviceUtil

val DeviceUtil.isDynamicColorAvailable by lazy {
    DynamicColors.isDynamicColorAvailable() || (DeviceUtil.isSamsung && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
}
