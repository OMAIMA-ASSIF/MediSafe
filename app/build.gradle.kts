import org.gradle.kotlin.dsl.compileOnly

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {

    namespace = "com.example.medisafe"
    compileSdk = 35   // ← correction : 36 n'est pas stable, on reste sur 35

    defaultConfig {
        applicationId = "com.example.medisafe"
        minSdk = 24
        targetSdk = 35   // ← aligne avec compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true   // ← indispensable pour les layouts XML
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
}

dependencies {
    // AndroidX de base
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Navigation Component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // ViewModel + LiveData
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    implementation(libs.material)
    // Firebase (via BOM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // WorkManager (notifications)
    implementation(libs.work.runtime)

    // SharedPreferences avancées
    implementation(libs.preference)

    // Glide (images)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Lottie (animations)
    implementation(libs.lottie)

    // MPAndroidChart (graphiques)
    implementation(libs.mpandroidchart)

    // CircularProgressBar
    implementation(libs.circularprogressbar)


    // Tests (tu avais déjà)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")


}
