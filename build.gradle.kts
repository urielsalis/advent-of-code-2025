plugins {
    kotlin("jvm") version "2.2.21"
    application
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
