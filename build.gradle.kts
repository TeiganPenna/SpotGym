// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
}

plugins {
    id("com.android.application") version "7.4.0" apply false
    id("com.android.library") version "7.4.0" apply false
    id("com.github.ben-manes.versions") version "0.42.0" apply false
    id("com.google.dagger.hilt.android") version "2.43.2" apply false
    id("de.mannodermaus.android-junit5") version "1.8.2.1" apply false
    id("io.gitlab.arturbosch.detekt") version "1.21.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false // cannot increase version, compose 1.3.2 only supports up to kotlin 1.7.20
    id("org.jmailen.kotlinter") version "3.13.0" apply false
}

task("clean") {
    delete(rootProject.buildDir)
}
