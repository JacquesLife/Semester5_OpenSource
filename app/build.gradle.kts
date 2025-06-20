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
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.jbcrypt)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.adapters)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.core.i18n)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.constraintlayout.compose.android)
    
    // ViewModel + LiveData
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Coroutines
    implementation(libs.coroutines.android)

    // Compose & Material3 (duplicate removed - already added above)

    // Jetpack Compose ViewModel integration
    implementation(libs.androidx.lifecycle.viewmodel.compose.v250)

    // Jetpack Compose Navigation
    implementation(libs.androidx.activity.compose.v170)

    // Jetpack Compose Coil
    implementation(libs.coil.compose)

    // Graph dependency
    implementation(libs.mpandroidchart)

    implementation(libs.androidx.material.icons.extended)
    implementation(platform(libs.firebase.bom))
    
    // Firebase dependencies
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database.ktx)


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    
    // WorkManager for background tasks
    implementation(libs.androidx.work.runtime.ktx)
    
    // Notification support
    implementation(libs.androidx.core.ktx.v1120)
}