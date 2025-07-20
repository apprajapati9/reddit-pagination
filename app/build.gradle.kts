plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "ca.apprajapati.redditcats"
    compileSdk = 36

    defaultConfig {
        applicationId = "ca.apprajapati.redditcats"
        minSdk = 24
        targetSdk = 36
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //=============== Nav3 =====================
    //Core nav3 api. Includes NavEntry, Entry provider, and DSL
    implementation(libs.androidx.navigation3.runtime)
    //provides classes to display content, including NavDisplay and Scene
    implementation(libs.androidx.navigation3.ui)
    //allows view models to be scoped to entries in the back stack
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    //support for adaptive layouts (sceneStrategies, Scenes and metadata definitions)
    //implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)


    // Coil - image loading from URL
    implementation(libs.coil.compose)
    implementation(libs.coil3.network) //must add this for network call using AsyncImage
}