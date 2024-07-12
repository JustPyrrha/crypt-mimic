plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.loom)
    `maven-publish`
}

base.archivesName = "Mimic"
group = "gay.pyrrha"
version = "0.1.0-alpha.2"

repositories {
    mavenCentral()
}

fabricApi {
    configureDataGeneration()
}

val enableEnhancedClassRedefinition: Boolean = if (System.getProperty("java.vendor") == "JetBrains s.r.o.") {
    println("JetBrains Runtime found, enabling Enhanced Class Redefinition (DCEVM)")
    true
} else {
    false
}

loom {
    runs {
        configureEach {
            if (enableEnhancedClassRedefinition) {
                vmArgs("-XX:+AllowEnhancedClassRedefinition")
            }
            runDir("runs/$name")
        }
    }
    mods {
        register("crypt-mimic") {
            sourceSet(sourceSets.main.get())
        }
    }
}

repositories {
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.fabric.mappings) { classifier("v2") })
    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    modImplementation(libs.kotlin.logging)
    include(libs.kotlin.logging)

    // Unfortunately, Ears cannot be supported without the full mod.
    modCompileOnly(libs.ears)
    modLocalRuntime(libs.ears)

    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }

    processResources {
        inputs.property("version", project.version.toString())
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version.toString()
                )
            )
        }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}" }
        }
    }
}

kotlin {
    jvmToolchain(21)
    explicitApi()
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
            )
        )
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
