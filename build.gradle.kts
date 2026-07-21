plugins {
    java
    id("org.sonarqube") version "7.2.2.6593"
    jacoco
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
    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.40.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testng:testng:7.10.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
    }
}

sonar {
    properties {
        property ("sonar.projectKey", "Constantine72_qa-auto-engineer-java-project-385")
        property ("sonar.organization", "constantine72")
        property ("sonar.coverage.jacoco.xmlReportPaths", "hexlet.code/reports/jacoco/test/jacocoTestReport.xml")
    }
}
