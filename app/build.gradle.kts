import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.2.0-RC"
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp") version "2.2.0-RC-2.0.1"
    id("kotlin-parcelize")
}

val apiKey = project.findProperty("API_KEY") as? String ?: ""

android {
    namespace = "com.example.shopko"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shopko"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["apiKey"] = apiKey
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "META-INF/native-image/reflect-config.json"
            excludes += "META-INF/native-image/native-image.properties"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.libraries.places:places:3.3.0")
    implementation ("androidx.camera:camera-core:1.3.2")
    implementation ("androidx.camera:camera-camera2:1.3.2")
    implementation ("androidx.camera:camera-lifecycle:1.3.2")
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("com.google.mlkit:text-recognition:16.0.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.5.0"))
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine")
    implementation("org.mongodb:bson-kotlinx:5.5.0")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.11.0")
    implementation("org.litote.kmongo:kmongo-id:4.11.0")
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-okhttp:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation ("androidx.room:room-runtime:2.7.1")
    implementation ("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.10.2")
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.android.material:material:1.11.0")
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
}