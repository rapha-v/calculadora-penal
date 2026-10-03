plugins {
    // "apply false": baixa e registra o plugin aqui, mas NÃO aplica na raiz.
    // Cada módulo (motor, web, api) aplica no próprio build.gradle.kts.
    alias(libs.plugins.kotlin.multiplatform) apply false
}