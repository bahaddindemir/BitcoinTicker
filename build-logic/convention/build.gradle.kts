import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.bahaddindemir.bitcointicker.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradlePlugin)

}

gradlePlugin {
    plugins {
        register("androidApplicationConvention") {
            id = "bitcointicker.android.application"
            //implementationClass =
            //    "com.bahaddindemir.bitcointicker.buildlogic.AndroidApplicationConventionPlugin"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
    }
}
