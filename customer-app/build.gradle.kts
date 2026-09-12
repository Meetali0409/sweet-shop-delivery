plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sweetshop.customer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sweetshop.customer"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")
        buildConfigField("String", "RAZORPAY_KEY_ID", "\"\"")
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH") ?: findProperty("KEYSTORE_PATH")?.toString()
            if (keystorePath != null) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: findProperty("KEYSTORE_PASSWORD")?.toString() ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: findProperty("KEY_ALIAS")?.toString() ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: findProperty("KEY_PASSWORD")?.toString() ?: ""
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"https://creative-dream-production-b15f.up.railway.app/api/v1/\"")
            buildConfigField("String", "RAZORPAY_KEY_ID", "\"rzp_test_placeholder\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val releaseUrl = System.getenv("API_BASE_URL") ?: "http://10.0.2.2:8080/api/v1/"
            buildConfigField("String", "API_BASE_URL", "\"$releaseUrl\"")
            buildConfigField("String", "RAZORPAY_KEY_ID", "\"${System.getenv("RAZORPAY_KEY_ID") ?: ""}\"")

            val keystorePath = System.getenv("KEYSTORE_PATH") ?: findProperty("KEYSTORE_PATH")?.toString()
            if (keystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Coil
    implementation(libs.coil.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Location
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // Razorpay
    implementation("com.razorpay:checkout:1.6.38")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


