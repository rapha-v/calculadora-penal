import calculadora.motor.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.datetime.LocalDate
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement

// função que já existe no JavaScript do navegador; "external" só declara a assinatura para o Kotlin
external fun encodeURIComponent(texto: String): String

private const val WHATSAPP_ESCRITORIO = "5511989498044" // da documentação do escritório (a confirmar)
private var ultimoResultado = "" // var: é trocado a cada cálculo, para ir na mensagem

fun main() {
    // !! proposital: se o elemento não existir no HTML, é erro de programação
    val formulario = document.getElementById("formulario")!!
    val saida = document.getElementById("resultado")!!
    val contato = document.getElementById("contato") as HTMLElement

    formulario.addEventListener("submit", { evento ->
        evento.preventDefault() // impede o navegador de recarregar a página
        try {
            val entrada = lerEntrada()
            ultimoResultado = formatar(entrada, calcular(entrada))
            saida.textContent = ultimoResultado
            contato.hidden = false // mostra o formulário de contato
        } catch (erro: IllegalArgumentException) {
            saida.textContent = "Verifique os dados: ${erro.message}"
            contato.hidden = true // sem resultado válido, não há o que enviar
        }
    })

    // aplica a máscara a cada tecla digitada no número do processo
    campo("processo").addEventListener("input", {
        campo("processo").value = mascaraProcesso(campo("processo").value)
    })

    document.getElementById("formContato")!!.addEventListener("submit", { evento ->
        evento.preventDefault()
        val aviso = document.getElementById("avisoContato")!!
        try {
            val mensagem = montarMensagem()
            aviso.textContent = ""
            window.open("https://wa.me/$WHATSAPP_ESCRITORIO?text=${encodeURIComponent(mensagem)}", "_blank")
        } catch (erro: IllegalArgumentException) {
            aviso.textContent = "Verifique os dados: ${erro.message}"
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

// --- Contato ---

/** Formata até 20 dígitos no padrão do CNJ: NNNNNNN-DD.AAAA.J.TR.OOOO */
private fun mascaraProcesso(texto: String): String {
    val digitos = texto.filter { it.isDigit() }.take(20)              // só números, no máximo 20
    val separadores = mapOf(7 to '-', 9 to '.', 13 to '.', 14 to '.', 16 to '.') // posição → símbolo
    val resultado = StringBuilder()
    digitos.forEachIndexed { i, c ->
        separadores[i]?.let { resultado.append(it) } // antes do dígito i, põe o separador (se houver)
        resultado.append(c)
    }
    return resultado.toString()
}

private fun montarMensagem(): String {
    val nome = campo("nome").value.trim()
    val whatsapp = campo("whatsapp").value.filter { it.isDigit() }
    val email = campo("email").value.trim()
    val processo = campo("processo").value

    require(nome.isNotEmpty()) { "preencha o nome completo" }
    require(whatsapp.length in 10..11) { "o WhatsApp deve ter DDD e número (10 ou 11 dígitos)" }
    require(email.isEmpty() || campo("email").checkValidity()) { "e-mail inválido" }
    require(processo.isEmpty() || processo.length == 25) { "número do processo incompleto" }

    return listOfNotNull(
        "Olá! Vim pela Calculadora Penal e quero falar com um advogado.",
        "",
        "Nome: $nome",
        "WhatsApp: $whatsapp",
        if (email.isNotEmpty()) "E-mail: $email" else null,
        if (processo.isNotEmpty()) "Processo: $processo" else null,
        "",
        "Resultado do cálculo:",
        ultimoResultado,
    ).joinToString("\n")
}