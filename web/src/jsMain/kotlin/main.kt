import calculadora.motor.*          //* importa tudo do motor: calcular, Entrada, Pena...
import kotlinx.browser.document     //o "document" do navegador, acessado pelo Kotlin
import kotlinx.datetime.LocalDate

fun main() {
    //teste
    val r = calcular(
        Entrada(
            pena = Pena(6, 0, 0),
            inicio = LocalDate(2026, 10, 27),
            dataDoFato = LocalDate(2025, 6, 1),
            tipo = TipoCrime.COMUM,
            reincidente = false,
        )
    )
    //procura o elemento id="saida" no HTML e troca o texto pelo resultado
    document.getElementById("saida")?.textContent = r.toString()
}