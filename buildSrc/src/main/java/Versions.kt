import java.util.Locale

object Versions {
    // Sdk and tools
    const val COMPILE_SDK = 33
    const val MIN_SDK = 30
    const val TARGET_SDK = 30

    const val DETEKT_TWITTER = "0.0.26"

    // App dependencies
    const val ACCOMPANIST = "0.28.0"
    const val ACTIVITY_COMPOSE = "1.6.1"
    const val COMPOSE = "1.3.2"
    const val COMPOSE_MATERIAL = "1.3.1"
    const val COMPOSE_REORDERABLE = "0.9.2"
    const val COMPOSE_RUNTIME = "1.3.3"
    const val COMPOSE_UI = "1.3.3"
    const val HILT = "2.44.2"
    const val HILT_NAVIGATION = "1.0.0"
    const val KTX = "1.9.0"
    const val LIFECYCLE = "2.5.1"
    const val NAVIGATION_COMPOSE = "2.5.3"
    const val ROOM = "2.5.0"

    // Test dependencies
    const val ANDROID_TEST_RUNNER = "1.5.2"
    const val ASSERTJ = "3.24.1"
    const val CORE_TESTING = "2.1.0"
    const val ESPRESSO = "3.5.1"
    const val JUNIT_JUPITER_ANDROID = "1.3.0"
    const val JUNIT_JUPITER = "5.9.1"
    const val KOTLINX_COROUTINES = "1.6.4"
    const val MOCKK = "1.13.3"
    const val TEST_EXT_JUNIT = "1.1.5"

    fun isStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA")
            .any { version.toUpperCase(Locale.getDefault()).contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        return stableKeyword || regex.matches(version)
    }
}
