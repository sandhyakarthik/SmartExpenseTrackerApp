plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.sandhya.expensetracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sandhya.expensetracker"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
       // kotlinCompilerExtensionVersion = "1.5.8" //removed for 16KB alignment issue
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}
kapt {
    correctErrorTypes =true
}//added
dependencies {

    implementation("androidx.navigation:navigation-compose:2.7.7") //added
    implementation ("com.google.android.material:material:1.11.0") //added
    //removing for 16KB apk alignment issue and replacing with below one instead
    //implementation("androidx.compose.foundation:foundation-android:1.7.0")//added
    implementation("androidx.compose.foundation:foundation")//added newly for 16KB alignment issue
    implementation ("androidx.room:room-runtime:2.6.1")//added
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")//added
    implementation("com.google.dagger:hilt-android:2.51")//added
    kapt("com.google.dagger:hilt-compiler:2.51")//added
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")//added
    // Ensure you are using a recent version of Material 3
    implementation("androidx.compose.material3:material3:1.2.1")//added
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.core:core-ktx:1.9.0")//need to update  it for 16KB alignment issue
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    //implementation(platform("androidx.compose:compose-bom:2023.03.00"))//removing for 16KB apk alignement issue
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))//added for 16KB apk alignement issue
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    //implementation("androidx.compose.material3:material3")//removing for 16KB apk alignement issue
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // For testing Flows/Coroutines
    testImplementation("io.mockk:mockk:1.13.8") // Optional: Used to create a fake repository easily
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

