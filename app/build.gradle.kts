import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}
val localProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            load(FileInputStream(file))
        }
    }

fun getLocalOrEnv(key: String): String? = localProperties.getProperty(key) ?: System.getenv(key)

android {
    namespace = "com.fagundes.myshowlist"
    compileSdk = 36

    signingConfigs {
        create("release") {
            val keystorePath = getLocalOrEnv("KEYSTORE_FILE")
            if (keystorePath != null) {
                storeFile = file(keystorePath)
                storePassword = getLocalOrEnv("KEYSTORE_PASSWORD")
                keyAlias = getLocalOrEnv("KEY_ALIAS")
                keyPassword = getLocalOrEnv("KEY_PASSWORD")
            }
        }
    }

    defaultConfig {
        applicationId = "com.fagundes.myshowlist"
        minSdk = 28
        targetSdk = 36
        // Overridable via -PversionCode / -PversionName for tag-based CI releases
        versionCode = providers.gradleProperty("versionCode").orNull?.toInt() ?: 1
        versionName = providers.gradleProperty("versionName").orNull ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val tmdbApiKey =
            localProperties.getProperty("TMDB_API_KEY") ?: ""

        if (tmdbApiKey.isEmpty()) {
            println("WARNING: TMDB_API_KEY not found in local.properties. TMDB API calls will fail.")
        }

        buildConfigField(
            "String",
            "TMDB_API_KEY",
            "\"$tmdbApiKey\"",
        )

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "room.schemaLocation" to
                            "$projectDir/schemas",
                    ),
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            val releaseSigningConfig = signingConfigs.getByName("release")
            if (releaseSigningConfig.storeFile != null) {
                signingConfig = releaseSigningConfig
            }
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "CINE VAULT (Dev)")
            buildConfigField("String", "TMDB_BASE_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "JIKAN_BASE_URL", "\"https://api.jikan.moe/v4/\"")
            buildConfigField("Boolean", "LOGGING_ENABLED", "true")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            resValue("string", "app_name", "CINE VAULT (Staging)")
            buildConfigField("String", "TMDB_BASE_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "JIKAN_BASE_URL", "\"https://api.jikan.moe/v4/\"")
            buildConfigField("Boolean", "LOGGING_ENABLED", "true")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "CINE VAULT")
            buildConfigField("String", "TMDB_BASE_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "JIKAN_BASE_URL", "\"https://api.jikan.moe/v4/\"")
            buildConfigField("Boolean", "LOGGING_ENABLED", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull == "true"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.navigation.compose)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle / ViewModel
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.compose)

    // Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Firebase products
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Coil
    implementation(libs.coil.compose)

    implementation(libs.androidx.core.splashscreen)

    // Tests
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

ktlint {
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    filter {
        exclude("**/build/**")
        exclude("**/src/androidTest/**")
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    ignoreFailures = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}
