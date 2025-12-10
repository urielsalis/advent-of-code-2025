plugins {
    kotlin("jvm") version "2.2.21"
    application
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("tools.aqua:z3-turnkey:4.13.0.1")
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "9.2.1"
    }

    named<JavaExec>("run") {
        standardInput = System.`in`
        if (project.hasProperty("mainClass")) {
            mainClass.set(project.property("mainClass") as String)
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/detekt-config.yml")
    source.setFrom("src")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
    }
}
