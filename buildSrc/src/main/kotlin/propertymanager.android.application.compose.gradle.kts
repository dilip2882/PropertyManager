import propertymanager.buildlogic.configureCompose

plugins {
    id("com.android.application")
    kotlin("android")

    id("propertymanager.code.lint")
}

android {
    configureCompose(this)
}
