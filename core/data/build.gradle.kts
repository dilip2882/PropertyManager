plugins {
    id("propertymanager.library")
    kotlin("android")
    kotlin("plugin.serialization")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "propertymanager.common.data"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)

    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Data serialization (JSON, protobuf, xml, retrofit)
    implementation(kotlinx.bundles.serialization)

    implementation(kotlinx.reflect)
    implementation(kotlinx.immutables)
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.freeCompilerArgs.addAll(
            "-Xcontext-receivers",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
        )
    }
}
