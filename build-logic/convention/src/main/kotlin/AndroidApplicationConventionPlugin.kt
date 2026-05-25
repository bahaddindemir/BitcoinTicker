import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    extensions.configure<ApplicationExtension> {
      namespace = "com.bahaddindemir.bitcointicker"
      compileSdk = 36

      defaultConfig {
        applicationId = "com.bahaddindemir.bitcointicker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      }

      buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
      }

      buildTypes {
        getByName("debug") {
          buildConfigField(
            "String",
            "API_BASE_URL",
            "\"https://api.coingecko.com/api/v3/\""
          )
        }

        getByName("release") {
          isMinifyEnabled = false
          proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
          )

          buildConfigField(
            "String",
            "API_BASE_URL",
            "\"https://api.coingecko.com/api/v3/\""
          )
        }
      }

      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
      }
    }

    extensions.configure<KotlinAndroidProjectExtension> {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
      }
    }
  }
}