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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("net.datafaker:datafaker:2.4.1")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// Other dependencies
	implementation("com.h2database:h2:2.3.232")
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


