plugins {
    id("java")
}

group = "fr.mewtrpg"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.minestom:minestom-snapshots:1_21_4-44b34717ed")

    implementation("org.projectlombok:lombok:1.18.34")

    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    // https://mvnrepository.com/artifact/net.objecthunter/exp4j
    implementation("net.objecthunter:exp4j:0.4.8")
}

tasks.test {
    useJUnitPlatform()
}