/*
 * Copyright (c) 2022-2024 Universitat Politècnica de València
 * Authors: David de Andrés and Juan Carlos Ruiz
 *          Fault-Tolerant Systems
 *          Instituto ITACA
 *          Universitat Politècnica de València
 *
 * Distributed under MIT license
 * (See accompanying file LICENSE.txt)
 */

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Creates a variable called keystoreProperties and loads the keystore.properties file,
// which is NOT stored in the Control Version System
val keystoreProperties = File(rootDir, "keystore.properties").inputStream().use {
    Properties().apply { load(it)}
}

android {
    namespace = "upv.dadm.ex27_maps"
    compileSdk = 35

    defaultConfig {
        applicationId = "upv.dadm.ex27_maps"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Creates a place holder in the Manifest to access the key defined in the keystore.properties file
        manifestPlaceholders["GOOGLE_MAPS_KEY"] = keystoreProperties.getValue("GOOGLE_MAPS_KEY") as String

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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.gms.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}