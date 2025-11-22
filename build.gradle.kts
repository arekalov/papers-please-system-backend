plugins {
	kotlin("jvm") version "2.0.21"
	kotlin("plugin.spring") version "2.0.21"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

group = "com.arekalov"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	
	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


detekt {
	buildUponDefaultConfig = true
	allRules = false
	config.setFrom("$projectDir/detekt.yml")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
	reports {
		html {
			required.set(true)
			outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.html"))
		}
		txt {
			required.set(true)
			outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.txt"))
		}
		xml.required.set(false)
		sarif.required.set(false)
		md.required.set(false)
	}

	jvmTarget = "17"
}

tasks.named("check") {
	setDependsOn(dependsOn.filterNot { it.toString().contains("detekt") })
}
