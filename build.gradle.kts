plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "net.zeotrope"
version = "0.0.1-SNAPSHOT"
description = "item_cache.webservice"
java.sourceCompatibility = JavaVersion.VERSION_21

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["flywayDbVersion"] = "11.12.0"
extra["mockkVersion"] = "1.14.5"
extra["postgresqlJdbcDriverVersion"] = "42.7.8"
extra["postgresqlR2dbcVersion"] = "1.1.1.RELEASE"
extra["springBootVersion"] = "3.5.8"
extra["springMockkVersion"] = "4.0.2"
extra["redisTestContainersVersion"] = "2.2.4"
extra["testContainersVersion"] = "1.21.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator:${property("springBootVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-webflux:${property("springBootVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-cache:${property("springBootVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:${property("springBootVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:${property("springBootVersion")}")

    // reactive postgresql
    implementation("org.postgresql:r2dbc-postgresql:${property("postgresqlR2dbcVersion")}")

    implementation("org.springframework.plugin:spring-plugin-core:3.0.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.25.1")
    implementation("org.apache.logging.log4j:log4j-api:2.25.1")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.5.0")

    // Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Docker Compose Development
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // Flyway database migration
    implementation("org.flywaydb:flyway-core:${property("flywayDbVersion")}")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:${property("flywayDbVersion")}")
    runtimeOnly("org.postgresql:postgresql:${property("postgresqlJdbcDriverVersion")}")

    // Required for Netty DNS resolution on macOS to prevent R2DBC hangs
    runtimeOnly("io.netty:netty-resolver-dns-native-macos::osx-aarch_64")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${property("springBootVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${property("springBootVersion")}")
    testImplementation("org.springframework.boot:spring-boot-testcontainers:${property("springBootVersion")}")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("io.mockk:mockk:${property("mockkVersion")}")
    testImplementation("com.ninja-squad:springmockk:${property("springMockkVersion")}")
    testImplementation("org.testcontainers:junit-jupiter:${property("testContainersVersion")}")
    testImplementation("org.testcontainers:postgresql:${property("testContainersVersion")}")
    testImplementation("org.testcontainers:r2dbc:${property("testContainersVersion")}")
    testImplementation("com.redis:testcontainers-redis:${property("redisTestContainersVersion")}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
