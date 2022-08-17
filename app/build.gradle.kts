plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

android {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "com.spotgym.spot"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "33.0.0"
}

detekt {
    config = files("${project.rootDir}/detekt.yml")
}

dependencies {
    kapt("androidx.room:room-compiler:${Versions.ROOM}")
    implementation("androidx.core:core-ktx:${Versions.KTX}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}")
    implementation("androidx.room:room-runtime:${Versions.ROOM}")
    implementation("androidx.room:room-ktx:${Versions.ROOM}")
    annotationProcessor("androidx.room:room-compiler:${Versions.ROOM}")

    // Compose
    implementation("androidx.activity:activity-compose:${Versions.ACTIVITY_COMPOSE}")
    implementation("androidx.navigation:navigation-compose:${Versions.NAVIGATION_COMPOSE}")
    implementation("androidx.compose.ui:ui:${Versions.COMPOSE}")
    implementation("androidx.compose.material:material:${Versions.COMPOSE}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Versions.COMPOSE}")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.COMPOSE}")

    // Testing dependencies
    androidTestImplementation("androidx.arch.core:core-testing:${Versions.CORE_TESTING}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.TEST_EXT_JUNIT}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.ESPRESSO}")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.COMPOSE}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.COMPOSE}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Versions.COMPOSE}")
    testImplementation("junit:junit:${Versions.JUNIT}")
}
