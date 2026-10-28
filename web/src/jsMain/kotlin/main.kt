/** import calculadora.motor.*          * importa tudo do motor: calcular, Entrada, Pena...
import kotlinx.browser.document     o "document" do navegador, acessado pelo Kotlin
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
} */ //era teste

import calculadora.motor.*
import kotlinx.browser.document
import kotlinx.datetime.LocalDate
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement

fun main() {
    // !! aqui é proposital: se o formulário não existir no HTML, é erro de programação,
    // e é melhor o site quebrar na hora do que falhar em silêncio
    val formulario = document.getElementById("formulario")!!
    val saida = document.getElementById("resultado")!!

    formulario.addEventListener("submit", { evento ->
        evento.preventDefault() // impede o navegador de recarregar a página ao enviar
        try {
            val entrada = lerEntrada()
            saida.textContent = formatar(entrada, calcular(entrada))
        } catch (erro: IllegalArgumentException) {
            // os require do motor lançam essa exceção: mostramos a mensagem deles
            saida.textContent = "Verifique os dados: ${erro.message}"
        }
    })
}

// --- Leitura do formulário ---

private fun campo(id: String) = document.getElementById(id) as HTMLInputElement
private fun lista(id: String) = document.getElementById(id) as HTMLSelectElement
private fun numero(id: String) = campo(id).value.toIntOrNull() ?: 0 // vazio ou inválido vira 0

private fun data(id: String, nome: String): LocalDate {
    val texto = campo(id).value // o campo de data entrega "aaaa-mm-dd", ou "" se vazio
    require(texto.isNotEmpty()) { "preencha a $nome" }
    return LocalDate.parse(texto)
}

private fun lerEntrada() = Entrada(
    pena = Pena(numero("anos"), numero("meses"), numero("dias")),
    inicio = data("inicio", "data de início"),
    dataDoFato = data("dataDoFato", "data do fato"),
    tipo = TipoCrime.valueOf(lista("tipo").value),
    reincidente = campo("reincidente").checked,
    regimeInicial = RegimeInicial.valueOf(lista("regime").value),
    detracaoDias = numero("detracao"),
)

// --- Formatação do resultado ---

private fun formatar(d: LocalDate): String {
    val (ano, mes, dia) = d.toString().split("-") // "2027-10-12" → "12/10/2027"
    return "$dia/$mes/$ano"
}

private fun nomeDaLei(l: Legislacao) = when (l) {
    Legislacao.LEI_11464 -> "Lei 11.464/2007 (fatos de 29/03/2007 a 22/01/2020)"
    Legislacao.PACOTE_ANTICRIME -> "Pacote Anticrime (fatos de 23/01/2020 a 24/03/2026)"
    Legislacao.LEI_15358 -> "Lei 15.358/2026 (fatos de 25/03/2026 a 07/05/2026)"
    Legislacao.LEI_15402 -> "Lei 15.402/2026 (fatos a partir de 08/05/2026)"
}

private fun formatar(e: Entrada, r: Resultado): String {
    // livramento null: o motivo vem da fração (vedado) ou, se ela existe, da pena curta
    val livramento = r.livramento?.let { formatar(it) }
        ?: if (fracaoDeLivramento(e.tipo, e.reincidente, r.legislacao) == null)
            "não cabe (vedado no hediondo com resultado morte)"
        else "não cabe (exige pena igual ou superior a 2 anos)"

    return listOfNotNull( // listOfNotNull descarta os itens null
        "Legislação aplicada: ${nomeDaLei(r.legislacao)}",
        r.progressaoSemiaberto?.let { "Progressão ao semiaberto: ${formatar(it)}" }, // null no regime semiaberto
        "Progressão ao aberto: ${formatar(r.progressaoAberto)}",
        "Livramento condicional: $livramento",
        "Término da pena: ${formatar(r.terminoDaPena)}",
        "",
        "Estimativa: as datas podem variar alguns dias e não consideram remição nem os " +
            "requisitos subjetivos (como o comportamento), que são analisados pelo juiz.",
    ).joinToString("\n")
}