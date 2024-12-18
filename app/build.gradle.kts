import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}


val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

val MAPS_API_KEY = localProperties.getProperty("MAPS_API_KEY") ?: ""
val CHAT_API_KEY: String? = project.findProperty("CHAT_API_KEY") as String?

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.example.project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.project"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "MAPS_API_KEY", "\"$MAPS_API_KEY\"")

            buildConfigField("String", "CHAT_API_KEY", "\"$CHAT_API_KEY\"")
        }
        release {
            buildConfigField("String", "MAPS_API_KEY", "\"$MAPS_API_KEY\"")
            buildConfigField("String", "CHAT_API_KEY", "\"$CHAT_API_KEY\"")

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

    kotlinOptions {
        jvmTarget = "1.8"
    }


    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
        buildConfig = true


    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"  // Use the appropriate version here
    }
    // Prevent duplicate file issue
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
    }

}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")

    implementation("com.google.firebase:firebase-storage")
    implementation("com.android.volley:volley:1.2.1")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.recyclerview)
    implementation("androidx.work:work-runtime-ktx:2.8.0")
    implementation(libs.play.services.maps)

    implementation(libs.firebase.database.ktx)
    implementation(libs.generativeai)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation("com.jjoe64:graphview:4.2.2")

    implementation("com.github.sundeepk:compact-calendar-view:3.0.0")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.libraries.places:places:4.0.0")


    implementation("com.squareup.retrofit2:retrofit:2.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")

    implementation("de.hdodenhof:circleimageview:2.2.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.rengwuxian.materialedittext:library:2.1.4")
    implementation("com.squareup.retrofit2:converter-gson:2.3.0")

    implementation("com.google.firebase:firebase-messaging:20.1.2")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Compose dependencies
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.compose.ui:ui:1.5.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation("com.google.auth:google-auth-library-oauth2-http:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("com.google.api-client:google-api-client:1.32.1") // Do API FCM

}