import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
}

group = "io.github.ivanarroyo28"
version = "0.1.0-SNAPSHOT"

java {
    // Bytecode for Java 11: works on Android (minSdk 26+) and on any modern server.
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    // Library mode: every public declaration must say "public" explicitly,
    // so nothing becomes part of the API by accident.
    explicitApi()
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        // Only allow JDK 11 APIs, even when compiling with a newer JDK.
        freeCompilerArgs.add("-Xjdk-release=11")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "colombia-kt"
            from(components["java"])
            pom {
                name.set("colombia-kt")
                description.set("Kotlin utilities for Colombia: public holidays, business days, NIT, pesos and DIVIPOLA.")
                url.set("https://github.com/IvanArroyo28/colombia-kt")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("IvanArroyo28")
                        name.set("Ivan Arroyo")
                    }
                }
                scm {
                    url.set("https://github.com/IvanArroyo28/colombia-kt")
                    connection.set("scm:git:https://github.com/IvanArroyo28/colombia-kt.git")
                }
            }
        }
    }
}
