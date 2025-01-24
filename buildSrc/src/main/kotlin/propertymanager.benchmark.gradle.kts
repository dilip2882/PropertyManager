import propertymanager.buildlogic.configureAndroid
import propertymanager.buildlogic.configureTest

plugins {
    id("com.android.test")
    kotlin("android")

    id("propertymanager.code.lint")
}

android {
    configureAndroid(this)
    configureTest()
}
