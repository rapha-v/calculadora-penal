plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    js {
        browser {                         // roda no navegador
            commonWebpackConfig {
                outputFileName = "web.js" //nome do JS final, que o index.html carrega
            }
        }
        binaries.executable()             // gera um programa (com main), não uma biblioteca
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":motor"))     // o site usa o motor
            implementation(libs.kotlinx.datetime) // para criar LocalDate aqui também
        }
    }
}