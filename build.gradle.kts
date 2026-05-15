plugins {
	java
	checkstyle
	jacoco
	id("com.github.spotbugs") version "6.0.18"
	id("org.springframework.boot") version "3.5.10"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "mx.edu.cetys"
version = "0.0.1-SNAPSHOT"
description = "Proyecto laboratrio de pruebas"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
		html.required = true
	}
}

repositories {
	mavenCentral()
}

extra["springModulithVersion"] = "1.4.7"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.modulith:spring-modulith-starter-core")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
	runtimeOnly("org.springframework.modulith:spring-modulith-observability")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.modulith:spring-modulith-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	runtimeOnly("com.h2database:h2")

}

dependencyManagement {
	imports {
		mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

checkstyle {
	toolVersion = "10.20.1"
	configFile = file("config/checkstyle/checkstyle.xml")
	isIgnoreFailures = true
	maxWarnings = Int.MAX_VALUE
}

tasks.withType<Checkstyle>().configureEach {
	reports {
		html.required = true
		xml.required = false
	}
}

spotbugs {
	toolVersion = "4.9.3"
	ignoreFailures = true
	showStackTraces = false
}

configurations.named("spotbugs") {
	resolutionStrategy.eachDependency {
		if (requested.group == "org.ow2.asm") {
			useVersion("9.9.1")
			because("SpotBugs 4.9.3 ships with an older ASM that cannot parse Java 25 (class file v69) bytecode.")
		}
	}
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
	reports.create("html") {
		required = true
	}
	reports.create("xml") {
		required = true
	}
}