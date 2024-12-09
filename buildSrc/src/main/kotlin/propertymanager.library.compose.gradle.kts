import propertymanager.buildlogic.configureCompose

plugins {
    id("com.android.library")

    id("propertymanager.code.lint")
}

android {
    configureCompose(this)
}
