plugins {
	`maven-publish`
	kotlin("jvm") version "2.4.21"
	kotlin("plugin.spring") version "2.4.21"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.7"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenLocal()
	mavenCentral()
}

dependencies {
	implementation(libs.bundles.core)
	implementation(libs.bundles.data)
	implementation(libs.bundles.kotlin)
	implementation(libs.bundles.spring)
	implementation(libs.bundles.test)
}

tasks.getByName<Jar>("jar") {
	enabled = true
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "githubPackages"
			url = uri("https://maven.pkg.github.com/bible-game/config")
			credentials {
				username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
				password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
			}
		}
	}
}

tasks.register("printTagVersion") {
	doLast {
		println(project.version.toString().split("-")[0])
	}
}