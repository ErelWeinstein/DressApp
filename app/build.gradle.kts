plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")  // This is needed for Firebase functionality
}

android {
    namespace = "com.example.dressapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dressapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {


    // Other dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Additional libraries
        implementation("com.github.bumptech.glide:glide:4.16.0")
        annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
        implementation("com.google.firebase:firebase-auth:22.3.1")
        implementation ("com.google.firebase:firebase-database:20.2.1")
        implementation(platform("com.google.firebase:firebase-bom:32.7.2")) // BOM for Firebase
        implementation("com.google.android.gms:play-services-auth:20.7.0")
        implementation ("com.google.firebase:firebase-database:20.2.1")
        implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.google.firebase:firebase-storage:20.2.1")


}


