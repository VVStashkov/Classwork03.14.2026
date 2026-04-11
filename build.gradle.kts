import java.util.Properties

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("war")
    // Плагин liquibase-gradle больше не используется
    // id("org.liquibase.gradle") version "2.2.2"
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val postgresVersion: String by project
val springSecurityVersion: String by project
val lombokVersion = "1.18.36"

repositories {
    mavenCentral()
}

// Собственная конфигурация для зависимостей Liquibase CLI
val liquibaseRuntime by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    description = "Runtime dependencies for Liquibase CLI"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("org.springframework.security:spring-security-taglibs:$springSecurityVersion")

    // Основные зависимости Liquibase (для работы приложения, если нужно)
    implementation("org.liquibase:liquibase-core:4.33.0")

    // Зависимости для CLI Liquibase (будут использоваться в задаче generateChangelog)
    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    liquibaseRuntime("info.picocli:picocli:4.6.3")

    // Spring Boot Starter Test уже включает JUnit Jupiter, Mockito, AssertJ и т.д.
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // Lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal.valueOf(0.1)
            }
        }
    }
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}

// Задача для генерации changelog'а на основе существующей БД
tasks.register<JavaExec>("generateChangelog") {
    group = "liquibase"
    description = "Generate changelog from existing database"
    classpath = liquibaseRuntime
    mainClass = "liquibase.integration.commandline.LiquibaseCommandLine"
    args = listOf(
        "--changeLogFile=src/main/resources/db/changelog/liquibase-output.xml",
        "--url=jdbc:postgresql://localhost:5432/scientific_forum",
        "--username=postgres",
        "--password=postgres",
        "--driver=org.postgresql.Driver",
        "generateChangeLog"
    )
}