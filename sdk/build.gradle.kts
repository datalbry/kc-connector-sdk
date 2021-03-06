plugins {
    id("datalbry.spring")
}

dependencies {
    api(libs.spring.web)
    api(project(":sdk-api"))

    implementation(libs.postgres)
    implementation(libs.spring.actuator)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.amqp)
    implementation(libs.jackson.kotlin)
    implementation(libs.datalbry.alxndria.client)
    implementation(libs.datalbry.precise.serialization.jackson)
    testImplementation(libs.bundles.testing)
}
