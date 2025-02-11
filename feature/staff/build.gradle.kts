plugins {
    id("propertymanager.library")
    id("propertymanager.library.compose")
    kotlin("android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization")
}

android {
    namespace = "propertymanager.feature.staff"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.presentation)
    implementation(projects.core.firebase)
    implementation(projects.core.network)
    implementation(project(":feature:auth"))
//    implementation(projects.i18n)

    // Hilt
    implementation(libs.hilt.android)
    implementation(project(":feature:auth"))
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.androidx.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)

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

    // Image loading
    implementation(platform(libs.coil.bom))
    implementation(libs.bundles.coil)
    implementation(libs.subsamplingscaleimageview) {
        exclude(module = "image-decoder")
    }
    implementation(libs.image.decoder)
    implementation(libs.coil.compose)

    // biometric auth
    implementation(androidx.biometricktx)

    implementation(kotlinx.immutables)
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
