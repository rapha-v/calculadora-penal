plugins {
    //Aplica o plugin registrado na raiz (sem versão aqui: ela vem do catálogo)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()        //compila o motor para a JVM, que a API vai usar

    js {         //compila o motor para JavaScript, que o site vai usar
        nodejs() //os testes de JS rodam no Node.js, sem precisar abrir um navegador
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)  //datas, usada pelo próprio motor
        }
        commonTest.dependencies {
            implementation(kotlin("test"))  //biblioteca de testes multiplataforma
        }
    }
}