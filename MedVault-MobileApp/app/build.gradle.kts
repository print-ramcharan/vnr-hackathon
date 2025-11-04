import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Read DEV_HOST_IP from local.properties at project root (falls back to 10.100.27.122)
val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localProps.load(localPropsFile.inputStream())
}
val devHostIp: String = localProps.getProperty("DEV_HOST_IP", "10.100.27.122")

android {
    namespace = "com.codewithram.medvault"
    compileSdk = 36

    

    defaultConfig {
        applicationId = "com.codewithram.medvault"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    // Expose backend (MedVault) API base so the mobile app talks only to the MedVault backend
    // The MedVault backend will proxy requests to model/summary microservices.
    buildConfigField("String", "MODEL_BASE", "\"http://${devHostIp}:8080/api/inference/\"")
    buildConfigField("String", "SUMMARY_BASE", "\"http://${devHostIp}:8080/api/summary/\"")
    buildConfigField("String", "BACKEND_BASE", "\"http://${devHostIp}:8080/api/\"")
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
        // Enable BuildConfig generation so custom buildConfigField entries work
        buildConfig = true
    }
}
dependencies {
    // Core Android libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Networking libraries: Retrofit + OkHttp + Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.play.services.cast.framework)
    // If you use Glide's annotation processor uncomment the following and apply kapt plugin
    // kapt("com.github.bumptech.glide:compiler:4.15.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}