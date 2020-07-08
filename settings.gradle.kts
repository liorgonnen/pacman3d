pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")

        mavenCentral()

        maven("https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "pacman3d"

include("threejs_kt")
include("statsjs_kt")

