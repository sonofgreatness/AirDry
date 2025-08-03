plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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

    implementation ("org.apache.poi:poi:5.2.3")
    implementation ("org.apache.poi:poi-ooxml:5.2.3")

    // Required for OOXML support (low overhead)
    implementation ("org.apache.commons:commons-collections4:4.4")
    implementation ("org.apache.commons:commons-compress:1.21")

    // Hilt + WorkManager
    implementation ("androidx.hilt:hilt-work:1.0.0")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")
    // WorkManager
    implementation ("androidx.work:work-runtime-ktx:2.7.1")



}