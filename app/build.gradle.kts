plugins {
  id("com.android.application")
  id("com.google.devtools.ksp")
  id("kotlin-android")
  id("kotlin-kapt")
  id("kotlin-parcelize")
  id("dagger.hilt.android.plugin")
  id("androidx.navigation.safeargs")
  id("com.google.gms.google-services")
  id("bitcointicker.android.application")
}

dependencies {
  implementation(libs.core.ktx)
  implementation(libs.appcompat)
  implementation(libs.material)
  implementation(libs.constraint.layout)
  implementation(libs.recyclerview)
  implementation(libs.navigation.runtime)

  testImplementation(libs.junit)
  androidTestImplementation(libs.junit.ext)
  androidTestImplementation(libs.espresso)

  // Hilt
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)

  // Networking
  implementation(libs.gson)
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter)
  implementation(libs.okhttp.logging)
  implementation(libs.chuck)

  // Navigation
  implementation(libs.navigation.fragment)
  implementation(libs.navigation.ui)
  implementation(libs.navigation.dynamic)
  androidTestImplementation(libs.navigation.test)

  // Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.auth)

  // RX Java Android
  implementation(libs.rxjava)
  implementation(libs.rxandroid)

  // Room
  implementation(libs.room.ktx)
  implementation(libs.room.runtime)
  implementation(libs.room.rxjava)
  ksp(libs.room.compiler)

  // LiveData ViewModel
  implementation(libs.lifecycle.viewmodel)
  implementation(libs.lifecycle.livedata)

  // FireStore
  implementation(libs.firebase.firestore)
  implementation(libs.hawk)

  // Glide
  implementation(libs.glide)
  ksp(libs.glide.ksp)
}