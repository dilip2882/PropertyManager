plugins {
    id("propertymanager.library")
    kotlin("android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization")}

android {
    namespace = "com.propertymanager.network"

    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xcontext-receivers",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
        )
    }
}

dependencies {
    implementation(projects.i18n)
    implementation(projects.core.common)

    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.androidx.compiler)

    // Data serialization (JSON, protobuf, xml, retrofit)
    implementation(kotlinx.bundles.serialization)

    implementation(androidx.datastore)

    api(libs.preferencektx)

    // Sort
    implementation(libs.natural.comparator)

    // JavaScript engine
    implementation(libs.bundles.js.engine)

    testImplementation(libs.bundles.test)
}
