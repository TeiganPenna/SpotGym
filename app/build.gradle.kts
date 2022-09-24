plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("de.mannodermaus.android-junit5")
    id("io.gitlab.arturbosch.detekt")
    kotlin("kapt")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "com.spotgym.spot"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = AppVersion.Code
        versionName = AppVersion.Name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += mapOf(
            "runnerBuilder" to "de.mannodermaus.junit5.AndroidJUnit5Builder"
        )
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
    implementation("androidx.hilt:hilt-navigation-compose:${Versions.HILT_NAVIGATION}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}")
    implementation("androidx.room:room-runtime:${Versions.ROOM}")
    implementation("androidx.room:room-ktx:${Versions.ROOM}")
    annotationProcessor("androidx.room:room-compiler:${Versions.ROOM}")
    implementation("com.google.dagger:hilt-android:${Versions.HILT}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.HILT}")

    // Compose
    implementation("androidx.activity:activity-compose:${Versions.ACTIVITY_COMPOSE}")
    implementation("androidx.navigation:navigation-compose:${Versions.NAVIGATION_COMPOSE}")
    implementation("com.google.accompanist:accompanist-navigation-animation:${Versions.ANIMATED_NAVIGATION}")
    implementation("androidx.compose.ui:ui:${Versions.COMPOSE}")
    implementation("androidx.compose.material:material:${Versions.COMPOSE}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Versions.COMPOSE}")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.COMPOSE}")

    // Testing dependencies
    androidTestImplementation("androidx.arch.core:core-testing:${Versions.CORE_TESTING}")
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.COMPOSE}")
//    androidTestImplementation("androidx.test.ext:junit:${Versions.TEST_EXT_JUNIT}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.ESPRESSO}")
    androidTestImplementation("androidx.test:runner:${Versions.ANDROID_TEST_RUNNER}")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:${Versions.JUNIT_JUPITER_ANDROID}")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:${Versions.JUNIT_JUPITER_ANDROID}")
    androidTestImplementation("org.assertj:assertj-core:${Versions.ASSERTJ}")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT_JUPITER}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.COMPOSE}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Versions.COMPOSE}")
    testImplementation("io.mockk:mockk:${Versions.MOCKK}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT_JUPITER}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT_JUPITER}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.JUNIT_JUPITER}")
    testImplementation("org.assertj:assertj-core:${Versions.ASSERTJ}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.KOTLINX_COROUTINES}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kapt {
    correctErrorTypes = true
}
