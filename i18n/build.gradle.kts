import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import propertymanager.buildlogic.generatedBuildDir
import propertymanager.buildlogic.tasks.getLocalesConfigTask

plugins {
    id("propertymanager.library")
    kotlin("multiplatform")
    alias(libs.plugins.moko)
}

kotlin {
    androidTarget()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.moko.core)
            }
        }
    }
}

val generatedAndroidResourceDir = generatedBuildDir.resolve("android/res")

android {
    namespace = "propertymanager.i18n"

    sourceSets {
        val main by getting
        main.res.srcDirs(
            "src/commonMain/resources",
            generatedAndroidResourceDir,
        )
    }

    lint {
        disable.addAll(listOf("MissingTranslation", "ExtraTranslation"))
    }
}

multiplatformResources {
    resourcesPackage.set("propertymanager.i18n")
}

tasks {
    val localesConfigTask = project.getLocalesConfigTask(generatedAndroidResourceDir)
    preBuild {
        dependsOn(localesConfigTask)
    }

    withType<KotlinCompile> {
        compilerOptions.freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
        )
    }
}
