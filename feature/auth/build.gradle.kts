plugins {
    id("propertymanager.library")
    id("propertymanager.library.compose")
    kotlin("android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization")
}

android {
    namespace = "propertymanager.feature.auth"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.domain)
    api(projects.core.presentation)
    api(projects.i18n)
    api(project(":core:firebase"))

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.google.firebase.auth.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.androidx.compiler)

    // navigation
    implementation(androidx.navigation.compose)
    implementation(androidx.kotlinx.serialization.json)

    // Compose
    implementation(compose.activity)
    implementation(compose.foundation)
    implementation(compose.material3.core)
    implementation(compose.material.icons)
    implementation(compose.animation)
    implementation(compose.animation.graphics)
    debugImplementation(compose.ui.tooling)
    implementation(compose.ui.tooling.preview)
    implementation(compose.ui.util)

    implementation(kotlinx.immutables)

    implementation("com.hbb20:ccp:2.7.3")
}

tasks {
    // See https://kotlinlang.org/docs/reference/experimental.html#experimental-status-of-experimental-api(-markers)
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi",
            "-opt-in=coil3.annotation.ExperimentalCoilApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
        )
    }
}