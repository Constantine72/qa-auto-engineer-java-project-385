plugins {
    java
    id("org.sonarqube") version "7.2.2.6593"
    checkstyle
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.24.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testng:testng:7.10.2")
    testImplementation("io.qameta.allure:allure-junit5:2.24.0")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

sonar {
    properties {
        property ("sonar.projectKey", "Constantine72_qa-auto-engineer-java-project-385")
        property ("sonar.organization", "constantine72")
        property ("sonar.coverage.jacoco.xmlReportPaths", "hexlet.code/reports/jacoco/test/jacocoTestReport.xml")
    }
}



