package calculadora.api

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.ktor.client.request.get

class ApiTest {

    //testApplication sobe a API na memória, sem porta de rede; "client" faz os pedidos
    private fun pedir(json: String, verificar: suspend (io.ktor.client.statement.HttpResponse) -> Unit) =
        testApplication {
            application { modulo() }
            val resposta = client.post("/calcular") {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            verificar(resposta)
        }

    @Test
    fun calculaOCasoDaReferencia() = pedir(
        """{"anos":6,"dataDoFato":"2025-06-01","inicio":"2026-10-27","tipo":"COMUM"}"""
    ) {
        assertEquals(HttpStatusCode.OK, it.status)
        // o JSON inteiro: confere os valores E o formato que a página vai receber
        assertEquals(
            """{"legislacao":"PACOTE_ANTICRIME","progressaoSemiaberto":"2027-10-12",""" +
                """"progressaoAberto":"2028-08-01","livramento":"2028-10-26","terminoDaPena":"2032-10-26"}""",
            it.bodyAsText(),
        )
    }

    @Test
    fun regraDoMotorViraErro400() = pedir(
        """{"anos":6,"dataDoFato":"2025-06-01","inicio":"2025-05-01","tipo":"COMUM"}""" // início antes do fato
    ) {
        assertEquals(HttpStatusCode.BadRequest, it.status)
        assertTrue("anterior à data do fato" in it.bodyAsText()) // a mensagem do seu require
    }

    @Test
    fun dataInexistenteViraErro400() = pedir(
        """{"anos":6,"dataDoFato":"2025-02-30","inicio":"2026-10-27","tipo":"COMUM"}""" // 30 de fevereiro
    ) {
        assertEquals(HttpStatusCode.BadRequest, it.status)
    }

    @Test
    fun campoFaltandoViraErro400() = pedir(
        """{"anos":6,"inicio":"2026-10-27","tipo":"COMUM"}""" // sem dataDoFato
    ) {
        assertEquals(HttpStatusCode.BadRequest, it.status)
    }

    @Test
    fun entregaOSiteMarcadoParaUsarAApi() = testApplication { // testa se o site  por API funciona
        application { modulo() }
        val pagina = client.get("/")
        assertEquals(HttpStatusCode.OK, pagina.status)
        assertTrue("<meta name=\"modo\" content=\"api\">" in pagina.bodyAsText()) // a marca foi trocada
        assertEquals(HttpStatusCode.OK, client.get("/web.js").status)
    }

    @Test
    fun remicaoChegaAoMotor() = pedir(
        """{"anos":6,"dataDoFato":"2025-06-01","inicio":"2026-10-27","tipo":"COMUM","remicaoDias":30}"""
    ) {
        assertEquals(HttpStatusCode.OK, it.status)
        // 30 dias recuam a 1ª progressão de 12/10/2027 para 12/09/2027 (mesma data do teste da detração)
        assertTrue("\"progressaoSemiaberto\":\"2027-09-12\"" in it.bodyAsText())
    }
}