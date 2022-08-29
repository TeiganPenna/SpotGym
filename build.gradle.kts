// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
}

plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("com.google.dagger.hilt.android") version "2.43.2" apply false
    id("io.gitlab.arturbosch.detekt") version "1.21.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0" apply false
}

task("clean") {
    delete(rootProject.buildDir)
}
