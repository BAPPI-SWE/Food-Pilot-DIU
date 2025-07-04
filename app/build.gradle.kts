import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.diu.foodpilot.user"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.diu.foodpilot.user"
        minSdk = 24
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
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation("androidx.navigation:navigation-compose:2.7.0") // Or the latest stable version
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ADD THIS LINE - IT IS THE FIX
    implementation("androidx.compose.material:material-icons-extended-android:1.6.8")

    // ... (all your other dependencies like firebase, navigation, etc.)

    // Add this line for image loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    // ...

    // ... (all your other dependencies are here)

    implementation("androidx.compose.material3:material3")
    // ADD THIS LINE
    implementation("androidx.compose.material:material:1.6.8")

    implementation("androidx.compose.material:material-icons-extended-android:1.6.8")
    // ... (the rest of your dependencies)
    // Add this line for Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("androidx.compose.material3:material3")
    // ... (the rest of your dependencies)

}
