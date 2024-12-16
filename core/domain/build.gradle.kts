plugins {
    id("propertymanager.library")
    kotlin("android")
    kotlin("plugin.serialization")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "propertymanager.core.domain"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(projects.core.common)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    ksp(libs.hilt.compiler)

    // Data serialization (JSON, protobuf, xml, retrofit)
    implementation(kotlinx.bundles.serialization)
    
    implementation(platform(kotlinx.coroutines.bom))
    implementation(kotlinx.bundles.coroutines)
    implementation(kotlinx.bundles.serialization)

    implementation(libs.unifile)

    compileOnly(libs.compose.stablemarker)

    testImplementation(libs.bundles.test)
    testImplementation(kotlinx.coroutines.test)
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.freeCompilerArgs.addAll(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xcontext-receivers",
        )
    }
}
