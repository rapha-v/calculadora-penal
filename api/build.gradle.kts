plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) // gera, na compilação, o código que converte classe ↔ JSON
    application                              // plugin do próprio Gradle: cria a tarefa "run"
}

application {
    //funções soltas do arquivo Main.kt viram uma classe chamada "MainKt" na JVM
    mainClass.set("calculadora.api.MainKt")
}

dependencies {
    implementation(project(":motor"))        // a mesma regra de cálculo, usa o mesmo motor
    implementation(libs.kotlinx.datetime)    // LocalDate, usado aqui também
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.ktor.server.test.host) //servidor de mentira para testes
    testImplementation(kotlin("test"))             //a mesma biblioteca de testes do motor
}