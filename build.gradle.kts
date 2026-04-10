import java.util.Properties

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("war")
    // Плагин liquibase-gradle больше не используется
    // id("org.liquibase.gradle") version "2.2.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val postgresVersion: String by project
val springSecurityVersion: String by project
val lombokVersion = "1.18.36"

repositories {
    mavenCentral()
}

// Создаём собственную конфигурацию для зависимостей Liquibase
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

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.test {
    useJUnitPlatform()
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