plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.freefair.lombok") version "6.4.3"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
    implementation("org.springframework.kafka:spring-kafka:3.2.4")
    implementation("org.postgresql:postgresql:42.7.3")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("org.liquibase:liquibase-core")
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.testcontainers:postgresql:1.19.8")
    testImplementation("org.jeasy:easy-random-core:5.0.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
