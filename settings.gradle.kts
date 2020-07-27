pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
rootProject.name = "pacman3d"

include("threejs_kt")
include("statsjs_kt")

