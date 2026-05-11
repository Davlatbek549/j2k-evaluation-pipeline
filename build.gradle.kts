plugins {
    kotlin("jvm") version "2.3.20"
    application
}

group = "com.Davlatbek.j2k"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}
application {
    mainClass.set("EvaluatorKt")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}