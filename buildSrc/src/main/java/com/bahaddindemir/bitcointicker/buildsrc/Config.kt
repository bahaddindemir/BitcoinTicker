package com.bahaddindemir.bitcointicker.buildsrc

object Config {
    object AppConfig {
        const val appId = "com.bahaddindemir.bitcointicker"
        const val minSdkVersion = 23
        const val compileSdkVersion = 34
        const val targetSdkVersion = 34
        const val versionCode = 1
        const val versionName = "1"
        const val testRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    object Dependencies {
        const val gradle = "com.android.tools.build:gradle:${Versions.gradleVersion}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion}"
        const val navigationSafeArgs =
            "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.androidNavigation}"
        const val googleService = "com.google.gms:google-services:${Versions.googleService}"
    }

    object Environments {
        const val debugBaseUrl = "\"https://api.coingecko.com/api/v3/\""
        const val releaseBaseUrl = "\"https://api.coingecko.com/api/v3/\""
    }
}