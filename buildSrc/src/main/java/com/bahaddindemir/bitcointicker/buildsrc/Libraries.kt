package com.bahaddindemir.bitcointicker.buildsrc

object Libraries {
    // Support
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appcompat}"

    // Networking
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.interceptor}"
    const val chuckLogging = "com.readystatesoftware.chuck:library:${Versions.chuckLogging}"

    // UI
    const val materialDesign = "com.google.android.material:material:${Versions.materialDesign}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
    const val navigationKtx = "androidx.navigation:navigation-runtime-ktx:${Versions.androidNavigation}"
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.androidNavigation}"
    const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.androidNavigation}"
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigationCompose}"
    const val navigationDynamicFeatures =
        "androidx.navigation:navigation-dynamic-features-fragment:${Versions.androidNavigation}"

    // Utils
    const val localization = "com.zeugmasolutions.localehelper:locale-helper-android:${Versions.localization}"
    const val multidex = "androidx.multidex:multidex:${Versions.multidex}"

    // Hilt
    const val hilt = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
    const val hiltDaggerCompiler = "com.google.dagger:hilt-compiler:${Versions.hiltVersion}"

    // Test
    const val jUnit = "junit:junit:${Versions.jUnit}"
    const val jUnitExt = "androidx.test.ext:junit:${Versions.jUnitExt}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val composeTest = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
    const val navigationTest = "androidx.navigation:navigation-testing:${Versions.androidNavigation}"

    // Jetpack Compose
    const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.viewModelCompose}"
    const val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
    const val composeAnimation ="androidx.compose.animation:animation:${Versions.compose}"
    const val composeUI = "androidx.compose.ui:ui-tooling:${Versions.compose}"

    // Firebase
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseAuth = "com.google.firebase:firebase-auth"

    // RX Java Android
    const val rxJava = "io.reactivex.rxjava3:rxjava:${Versions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava3:rxandroid:${Versions.rxAndroid}"

    // Room
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomRxJava = "androidx.room:room-rxjava3:${Versions.room}"

    // LiveData ViewModel
    const val lifeCycleView = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifeCycleLive = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"

    // FireStore
    const val fireStore = "com.google.firebase:firebase-firestore-ktx"

    // Hawk
    const val hawk = "com.orhanobut:hawk:${Versions.hawk}"

    // Glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
}