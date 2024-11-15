plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.sscompanionapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sscompanionapp"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase BoM and Cloud Messaging
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-messaging")

    // Glide library for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // RecyclerView - View list of images / videos
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // AWS SDK for S3
    implementation("com.amazonaws:aws-android-sdk-s3:2.45.0")
    implementation("com.amazonaws:aws-android-sdk-core:2.45.0")    

    // AWS Mobile Client dependency**
    implementation("com.amazonaws:aws-android-sdk-mobile-client:2.45.0")

    // Exclude ProfileInstaller to avoid hanging issues
    configurations.all {
        exclude(group = "androidx.profileinstaller", module = "profileinstaller")
    }
}