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

    implementation("net.minestom:minestom-snapshots:bb7acc2e77")

    implementation("org.projectlombok:lombok:1.18.34")

    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.test {
    useJUnitPlatform()
}