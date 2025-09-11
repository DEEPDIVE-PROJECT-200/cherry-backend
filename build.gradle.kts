plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ok"
version = "0.0.1-SNAPSHOT"
description = "cherry"

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

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // S3
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3:3.4.0")

    // Thymeleaf Extensions
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Database
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Development Tools
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.github.codemonstur:embedded-redis:1.4.3")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Mockito Agent
    mockitoAgent("org.mockito:mockito-core:5.18.0") { isTransitive = false }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

// QueryDSL 설정부
val querydslDir = "${layout.buildDirectory.get().asFile}/generated/querydsl"

sourceSets {
    main {
        java.srcDir(querydslDir)
    }
}

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(file(querydslDir))
}

// QueryDSL 추가, 자동 생성된 Q 클래스 gradle clean 으로 제거
tasks.named("clean") {
    doLast {
        delete(file(querydslDir))
    }
}
