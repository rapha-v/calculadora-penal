// Repositórios de onde o Gradle baixa os PLUGINS (inclusive o do Kotlin)
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// Repositórios de onde os módulos baixam as BIBLIOTECAS (ex.: kotlinx-datetime)
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "calculadora-penal"
include("motor")   // o Gradle passa a reconhecer a pasta "motor" como um módulo