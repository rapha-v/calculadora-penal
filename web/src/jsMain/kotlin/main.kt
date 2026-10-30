import calculadora.motor.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.datetime.LocalDate
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.js.Date // o Date do JavaScript, só para saber a data de hoje
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
            val hoje = hoje() // lida uma vez: validação, tela e mensagem usam a mesma data
            val entrada = lerEntrada()
            // o motor não sabe que dia é hoje (não tem relógio), por isso esta regra fica no site
            require(entrada.dataDoFato <= hoje) { "a data do fato não pode estar no futuro" }
            val resultado = calcular(entrada)
            mostrar(saida, entrada, resultado, hoje)
            ultimoResultado = comoTexto(entrada, resultado, hoje)
            contato.hidden = false // mostra o formulário de contato
        } catch (erro: IllegalArgumentException) {
            saida.textContent = "Verifique os dados: ${erro.message}"
            saida.classList.add("erro")
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

private const val AVISO = "Estimativa: as datas podem variar alguns dias e não consideram remição nem os " +
    "requisitos subjetivos (como o comportamento), que são analisados pelo juiz."

/** Um ponto da linha do tempo: o nome do benefício e a data em que ele cabe. */
private data class Marco(val rotulo: String, val data: LocalDate)

/** Data de hoje pelo relógio de quem usa o site. */
private fun hoje(): LocalDate {
    val agora = Date()
    // PEGADINHA: no JavaScript, getMonth() vai de 0 (janeiro) a 11 (dezembro)
    return LocalDate(agora.getFullYear(), agora.getMonth() + 1, agora.getDate())
}

/** "já atingida" antes de hoje, "hoje" no próprio dia, null depois. */
private fun situacao(d: LocalDate, hoje: LocalDate): String? = when {
    d < hoje -> "já atingida" // LocalDate é Comparable: < e > comparam datas
    d == hoje -> "hoje"
    else -> null
}

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

/** As datas que existem neste resultado, em ordem cronológica. */
private fun linhaDoTempo(r: Resultado): List<Marco> = listOfNotNull(
    r.progressaoSemiaberto?.let { Marco("Progressão ao semiaberto", it) }, // null no regime semiaberto
    Marco("Progressão ao aberto", r.progressaoAberto),
    r.livramento?.let { Marco("Livramento condicional", it) },             // null quando não cabe
    Marco("Término da pena", r.terminoDaPena),
).sortedBy { it.data } // o livramento pode cair antes ou depois do aberto, depende do caso

/** Por que o livramento não cabe, ou null se ele cabe. */
private fun motivoSemLivramento(e: Entrada, r: Resultado): String? = when {
    r.livramento != null -> null
    fracaoDeLivramento(e.tipo, e.reincidente, r.legislacao) == null ->
        "vedado no hediondo com resultado morte"
    else -> "exige pena igual ou superior a 2 anos"
}

/** Monta o resultado na página. Só textContent, nunca innerHTML (proteção contra XSS). */
private fun mostrar(saida: Element, e: Entrada, r: Resultado, hoje: LocalDate) {
    saida.textContent = "" // apaga o cálculo anterior
    saida.classList.remove("erro")

    saida.appendChild(elemento("h2", texto = "Datas estimadas"))
    saida.appendChild(elemento("p", "lei", "Legislação aplicada: ${nomeDaLei(r.legislacao)}"))

    val lista = elemento("ol", "linha")
    for (marco in linhaDoTempo(r)) {
        val estado = situacao(marco.data, hoje)
        val item = elemento("li", if (estado != null) "marco atingido" else "marco")
        item.appendChild(elemento("span", "rotulo", marco.rotulo))
        item.appendChild(elemento("span", "data", formatar(marco.data)))
        estado?.let { item.appendChild(elemento("span", "situacao", it)) }
        lista.appendChild(item)
    }
    saida.appendChild(lista)

    motivoSemLivramento(e, r)?.let {
        saida.appendChild(elemento("p", "nota", "Livramento condicional: não cabe ($it)."))
    }
    if (e.detracaoDias > 0) {
        saida.appendChild(elemento("p", "nota", "Detração: ${e.detracaoDias} dias já descontados."))
    }
    saida.appendChild(elemento("p", "aviso", AVISO))
}

/** Cria um elemento HTML. "apply" configura o objeto e devolve ele mesmo. */
private fun elemento(tag: String, classe: String = "", texto: String = ""): Element =
    document.createElement(tag).apply {
        className = classe
        textContent = texto
    }

/** O mesmo resultado em texto puro, para a mensagem do WhatsApp. */
private fun comoTexto(e: Entrada, r: Resultado, hoje: LocalDate): String = buildList {
    add("Legislação aplicada: ${nomeDaLei(r.legislacao)}")
    for (marco in linhaDoTempo(r)) {
        val estado = situacao(marco.data, hoje)?.let { " ($it)" } ?: ""
        add("${marco.rotulo}: ${formatar(marco.data)}$estado")
    }
    motivoSemLivramento(e, r)?.let { add("Livramento condicional: não cabe ($it)") }
    if (e.detracaoDias > 0) add("Detração: ${e.detracaoDias} dias")
    add("")
    add(AVISO)
}.joinToString("\n")

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
    ).joinToString("\n") //separa os itens na mensagem
}