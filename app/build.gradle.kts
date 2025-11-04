plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.airdry"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.airdry"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val githubPat = project.findProperty("HUB_PAT") ?: System.getenv("HUB_PAT")
        buildConfigField("String", "HUB_PAT", "\"$githubPat\"")


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler) // Use ksp for Room
    implementation(libs.androidx.room.ktx) // Kotlin Extensions and Coroutines support for Room

    // Navigation
    implementation(libs.androidx.navigation.compose)
   // implementation(libs.accompanist.navigation.animation)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation ( libs.androidx.runtime.livedata)

    implementation (libs.poi)
    implementation (libs.poi.ooxml)

    // Required for OOXML support (low overhead)
    implementation (libs.commons.collections4)
    implementation (libs.commons.compress)

    // Hilt + WorkManager
    implementation ("androidx.hilt:hilt-work:1.0.0")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")
    // WorkManager
    implementation ("androidx.work:work-runtime-ktx:2.7.1")

    // Retrofit + Gson
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)

    // Preferences
    implementation (libs.androidx.preference.ktx)
    implementation(libs.multiplatform.settings)
    implementation(libs.multiplatform.settings.no.arg)
    // a
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("io.ktor:ktor-client-core:2.3.11")
    implementation("io.ktor:ktor-client-cio:2.3.11")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    val ktorVersion = "2.3.11"
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("io.ktor:ktor-client-logging:2.3.11")
    implementation("org.slf4j:slf4j-api:2.0.13")

    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.material.icons.extended)

}