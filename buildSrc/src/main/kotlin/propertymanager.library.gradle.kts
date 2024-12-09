import propertymanager.buildlogic.configureAndroid
import propertymanager.buildlogic.configureTest

plugins {
    id("com.android.library")

    id("propertymanager.code.lint")
}

android {
    configureAndroid(this)
    configureTest()
}
