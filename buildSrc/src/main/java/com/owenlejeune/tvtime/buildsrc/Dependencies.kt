package com.owenlejeune.tvtime.buildsrc

object Dependencies {

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.androidx}"
        const val ktxCore = "androidx.core:core-ktx:${Versions.core_ktx}"
    }

    object Compose {
        const val material3 = "androidx.compose.material3:material3:${Versions.compose_material3}"
        const val material = "androidx.compose.material:material:${Versions.compose}"
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
        const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val activity = "androidx.activity:activity-compose:${Versions.activity_compose}"
        const val accompanistSystemUi = "com.google.accompanist:accompanist-systemuicontroller:${Versions.compose_accompanist}"
        const val navigation = "androidx.navigation:navigation-compose:${Versions.compose_navigation}"
    }

    object Lifecycle {
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle_runtime}"
    }

    object Testing {
        const val junit = "junit:junit:${Versions.junit}"
        const val composeJunit = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
        const val androidXJunit = "androidx.test.ext:junit:${Versions.androidx_junit}"
        const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso_core}"
    }

    object BuildPlugins {
        const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val androidGradle = "com.android.tools.build:gradle:${Versions.gradle}"
        const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}"
    }
}