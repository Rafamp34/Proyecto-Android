plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "2.0.21"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.alaturing.umusicapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alaturing.umusicapp"
        minSdk = 31
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += listOf(
                "/META-INF/DEPENDENCIES",
                "/META-INF/INDEX.LIST",
                "/META-INF/io.netty.versions.properties"
            )
        }
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    // Media3 for audio playback
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coil for image loading
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.v300alpha01)
    implementation(libs.coil.network.okhttp.v300alpha01)

    // Retrofit for network calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    implementation(libs.camera.core)
    implementation(libs.camera.view)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.firebase.appdistribution.gradle)
    kapt(libs.hilt.android.compiler)

    // Navigation components
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // Core dependencies
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.okhttp.v491)
    implementation (libs.logging.interceptor.v491)
}

kapt {
    correctErrorTypes = true
}