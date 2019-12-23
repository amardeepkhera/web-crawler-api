import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


buildscript {
    val springBootVersion: String by project
    val kotlinVersion: String by project

    extra.apply {
        set("springBootVersion", springBootVersion)
        set("kotlinVersion", kotlinVersion)
    }


    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
    }
}

plugins {
    kotlin("jvm").version(extra.get("kotlinVersion") as String)
    id("org.jetbrains.kotlin.plugin.spring").version(extra.get("kotlinVersion") as String)
    id("org.springframework.boot").version(extra.get("springBootVersion") as String)
    id("io.spring.dependency-management").version(extra.get("pluginsSpringDependencyManagement") as String)
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.41"
}

group = "qantas"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
}


dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.github.microutils:kotlin-logging:1.6.22")

    implementation("org.jsoup:jsoup:1.12.1")

    implementation("com.github.ben-manes.caffeine:caffeine:2.7.0")

    implementation("io.springfox:springfox-swagger2:2.8.0")
    implementation("io.springfox:springfox-swagger-ui:2.8.0")
    implementation("javax.xml.bind:jaxb-api:2.1")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation(group = "com.github.tomakehurst", name = "wiremock", version = "2.25.1")

}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}