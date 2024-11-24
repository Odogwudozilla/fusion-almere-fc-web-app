import org.gradle.internal.os.OperatingSystem

plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
}

springBoot {
    mainClass.set("com.fusionalmerefc.FusionAlmereFcApplication") // replace with your main class fully qualified name
}

group = "com.fusionalmerefc"
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
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation") // Validation starter

    // Lombok
    implementation("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.h2database:h2:2.1.214") // Downgraded H2 for stability

    // Validation dependencies (if validation starter is insufficient)
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // Other libraries
    implementation("net.datafaker:datafaker:2.4.1")
}


tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register("buildFrontend", Exec::class) {
    workingDir = file("../frontend")
    
    // Detect OS and set command
    val npmCommand = if (OperatingSystem.current().isWindows) "npm.cmd" else "npm"
    
    commandLine(npmCommand, "install")
    commandLine(npmCommand, "run", "build")
}

tasks.register("copyFrontendBuild", Copy::class) {
    dependsOn("buildFrontend")
    from("../frontend/build")
    into("../backend/src/main/resources/static/")
}

// Ensure copyFrontendBuild runs before processResources
tasks.named("processResources") {
    dependsOn("copyFrontendBuild")
}


