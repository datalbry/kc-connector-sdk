enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "connector-sdk"

includeBuild("gradle/convention")

include(
        "sdk",
        "sdk-api",
        "sdk-autoconfigure",
        "sdk-plugin",
        "sdk-test"
)

pluginManagement {
        plugins {
                val kotlinVersion = "1.5.31"
                val springBootVersion = "2.3.5.RELEASE"
                val springVersion = "1.0.9.RELEASE"

                kotlin("jvm") version kotlinVersion apply false
                kotlin("plugin.spring") version kotlinVersion apply false
                id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion apply false
                id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion apply false
                id("org.springframework.boot") version springBootVersion apply false
                id("io.spring.dependency-management") version springVersion apply false
                id("com.bmuschko.docker-spring-boot-application") version "6.6.1" apply false
        }
}
