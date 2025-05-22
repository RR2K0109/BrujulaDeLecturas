import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Elimina la siguiente línea de ESTE ARCHIVO (app/build.gradle.kts)
    // id("com.google.gms.google-services") version "4.4.2" apply false

    id("com.google.gms.google-services") // Esta es la correcta para aplicar el plugin al módulo app
}

android {
    buildFeatures {
        viewBinding = true
        compose = true // Si ya lo tenías en otro bloque buildFeatures, muévelo aquí
        buildConfig = true // <--- AÑADE O ASEGURA QUE ESTA LÍNEA ESTÉ PRESENTE Y SEA TRUE
    }

    namespace = "com.example.brujuladelecturas"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.brujuladelecturas"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Para acceder a la API Key desde el código de forma segura
        // Primero, lee la propiedad desde local.properties
        val apiKeyProperties = Properties() // Usa Properties() directamente
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            // Usa FileInputStream() directamente y envuélvelo en .use para seguridad
            FileInputStream(localPropertiesFile).use { inputStream ->
                apiKeyProperties.load(inputStream)
            }
        }

        buildConfigField("String", "GEMINI_API_KEY", "\"${apiKeyProperties.getProperty("GEMINI_API_KEY") ?: ""}\"")

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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.generativeai)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.google.android.material:material:1.12.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1")) // O la versión que uses
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("com.google.firebase:firebase-firestore-ktx") // Para Cloud Firestore

    implementation("com.github.bumptech.glide:glide:4.16.0") // Revisa la última versión de Glide

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0") // Revisa la última versión de GenAI

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0") // Verifica la última versión
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.0")   // Verifica la última versión

    implementation("androidx.viewpager2:viewpager2:1.1.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
}