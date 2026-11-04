plugins {
    id("java")
}

group = "fr.mewtrpg"
version = "1.0-1.21.11"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("net.minestom:minestom:2026.04.13-1.21.11")

    implementation("org.projectlombok:lombok:1.18.46")

    annotationProcessor("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")

    // https://mvnrepository.com/artifact/net.objecthunter/exp4j
    implementation("net.objecthunter:exp4j:0.4.8")
}

tasks.test {
    useJUnitPlatform()
}
