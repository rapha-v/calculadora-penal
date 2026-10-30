package calculadora.api

import calculadora.motor.*
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import io.ktor.server.application.Application

/**O JSON que chega. Campos com valor padrão podem faltar no JSON. */
@Serializable
data class Pedido(
    val anos: Int,
    val meses: Int = 0,
    val dias: Int = 0,
    val dataDoFato: String,              // "aaaa-mm-dd"
    val inicio: String,
    val tipo: TipoCrime,                 // enum vira o nome: "COMUM", "HEDIONDO"...
    val reincidente: Boolean = false,
    val regimeInicial: RegimeInicial = RegimeInicial.FECHADO,
    val detracaoDias: Int = 0,
)

/** O JSON que sai. null = não se aplica, como no Resultado do motor. */
@Serializable
data class Resposta(
    val legislacao: Legislacao,
    val progressaoSemiaberto: String?,
    val progressaoAberto: String,
    val livramento: String?,
    val terminoDaPena: String,
)

@Serializable
data class Erro(val erro: String)

// LocalDate.parse lança DateTimeFormatException, que é filha de IllegalArgumentException
fun Pedido.paraEntrada() = Entrada(
    pena = Pena(anos, meses, dias),
    inicio = LocalDate.parse(inicio),
    dataDoFato = LocalDate.parse(dataDoFato),
    tipo = tipo,
    reincidente = reincidente,
    regimeInicial = regimeInicial,
    detracaoDias = detracaoDias,
)

fun Resultado.paraResposta() = Resposta(
    legislacao = legislacao,
    progressaoSemiaberto = progressaoSemiaberto?.toString(), // LocalDate.toString() = "aaaa-mm-dd"
    progressaoAberto = progressaoAberto.toString(),
    livramento = livramento?.toString(),
    terminoDaPena = terminoDaPena.toString(),
)

/** Toda a configuração da API. Separada do main para os testes poderem usá-la. */
fun Application.modulo() {
    install(ContentNegotiation) { json() } //ensina o Ktor a ler e escrever JSON

    routing {
        post("/calcular") {
            try {
                val pedido = call.receive<Pedido>()              // JSON → Pedido
                call.respond(calcular(pedido.paraEntrada()).paraResposta())
            } catch (e: IllegalArgumentException) {             // regra do motor ou data inválida
                call.respond(HttpStatusCode.BadRequest, Erro(e.message ?: "dados inválidos"))
            } catch (e: BadRequestException) {                  // JSON malformado ou campo faltando
                call.respond(HttpStatusCode.BadRequest, Erro("JSON inválido ou campo obrigatório faltando"))
            }
        }
    }
}

fun main() {
    // host 127.0.0.1 (endereço loopback): só o seu próprio computador consegue acessar (ninguém da rede)
    // wait = true: o programa fica rodando até Ctrl+C
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") { modulo() }.start(wait = true)
}