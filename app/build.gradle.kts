plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.budgettrackerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.budgettrackerapp"
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
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.navigation.runtime.android)
    implementation ("androidx.compose.material3:material3:1.3.2")
    implementation ("androidx.compose.ui:ui:1.8.0")
    implementation ("androidx.compose.foundation:foundation:1.8.0" )
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation(libs.androidx.navigation.compose)
    implementation("org.mindrot:jbcrypt:0.4")
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.adapters)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.constraintlayout:constraintlayout-compose-android:1.1.1")
    
    // ViewModel + LiveData
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Coroutines
    implementation(libs.coroutines.android)

    // Compose & Material3 (already added by you probably)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)

    // Jetpack Compose ViewModel integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0")

    // Jetpack Compose Navigation
    implementation("androidx.activity:activity-compose:1.7.0")

    // Jetpack Compose Coil
    implementation("io.coil-kt:coil-compose:2.2.2")

    // Graph dependency
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("androidx.compose.material:material-icons-extended:1.7.8")
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    
    // Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database-ktx")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    
    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Notification support
    implementation("androidx.core:core-ktx:1.12.0")
}