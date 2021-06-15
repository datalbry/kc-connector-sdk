plugins {
    id("datalbry.kotlin")
    id("datalbry.publication")
    id("java-gradle-plugin")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(libs.junit.jupiter.api)
    testRuntime(libs.junit.jupiter.core)
    testRuntime(libs.junit.jupiter.engine)

    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.3.5.RELEASE")
    implementation("io.spring.gradle:dependency-management-plugin:1.0.9.RELEASE")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.4.32-1.0.0-alpha07")
    implementation("gradle.plugin.com.google.cloud.tools:jib-gradle-plugin:3.0.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

gradlePlugin {
    plugins {
        create("connector-sdk") {
            id = "io.datalbry.connector.sdk"
            implementationClass = "io.datalbry.connector.plugin.ConnectorPlugin"
        }
    }
}
