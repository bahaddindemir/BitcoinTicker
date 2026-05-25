plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.android)
  id("kotlin-parcelize")
  alias(libs.plugins.hilt)
  alias(libs.plugins.google.services)
  alias(libs.plugins.compose.compiler)
  id("bitcointicker.android.application")
}

dependencies {
  implementation(libs.core.ktx)
  implementation(libs.appcompat)
  implementation(libs.material)
  implementation(libs.constraint.layout)
  implementation(libs.recyclerview)

  testImplementation(libs.junit)
  androidTestImplementation(libs.junit.ext)
  androidTestImplementation(libs.espresso)
  androidTestImplementation(libs.compose.test)
  androidTestImplementation(platform(libs.androidx.compose.bom))

  // Compose
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.activity.compose)
  implementation(libs.viewmodel.compose)
  implementation(libs.compose.material3)
  implementation(libs.compose.animation)
  implementation(libs.compose.preview)
  implementation(libs.compose.ui)
  implementation(libs.coil.compose)
  implementation(libs.coil.network.okhttp)
  debugImplementation(libs.compose.ui.tooling)

  // Hilt
  implementation(libs.hilt.android)
  implementation(libs.hilt.lifecycle.viewmodel.compose)
  ksp(libs.hilt.compiler)

  // Networking
  implementation(libs.gson)
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter)
  implementation(libs.okhttp.logging)
  implementation(libs.chuck)

  // Navigation
  implementation(libs.navigation.compose)
  androidTestImplementation(libs.navigation.test)

  // Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.auth)
  implementation(libs.coroutines.play.services)

  // Room
  implementation(libs.room.ktx)
  implementation(libs.room.runtime)
  ksp(libs.room.compiler)

  // LiveData ViewModel
  implementation(libs.lifecycle.viewmodel)
  implementation(libs.lifecycle.livedata)

  // FireStore
  implementation(libs.firebase.firestore)
}
