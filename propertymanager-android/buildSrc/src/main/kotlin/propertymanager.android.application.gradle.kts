import propertymanager.buildlogic.AndroidConfig
import propertymanager.buildlogic.configureAndroid
import propertymanager.buildlogic.configureTest

plugins {
    id("com.android.application")
    kotlin("android")

    id("propertymanager.code.lint")
}

android {
    defaultConfig {
        targetSdk = AndroidConfig.TARGET_SDK
    }
    configureAndroid(this)
    configureTest()
}
