package calculadora.motor

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

/** Regime inicial fixado na sentença. */
enum class RegimeInicial { FECHADO, SEMIABERTO }

/** Dados informados pelo usuário. Detração em dias de prisão provisória (art. 42 do Código Penal) */
data class Entrada(
    val pena: Pena,
    val inicio: LocalDate,
    val dataDoFato: LocalDate,
    val tipo: TipoCrime,
    val reincidente: Boolean,
    val regimeInicial: RegimeInicial = RegimeInicial.FECHADO,
    val detracaoDias: Int = 0,
)

/** Resultado do cálculo. Um campo null significa "não se aplica". */
data class Resultado(
    val legislacao: Legislacao,
    val progressaoSemiaberto: LocalDate?, // null quando o regime inicial já é o semiaberto
    val progressaoAberto: LocalDate,
    val livramento: LocalDate?,           // null para vedado ou pena menor que 2 anos
    val terminoDaPena: LocalDate,
)

/**
 * Calcula todas as datas previstas a partir da entrada.
 * Detração: os dias de prisão provisória contam como pena cumprida, então todas as datas
 * partem do início recuado desses dias (a confirmar com o escritório).
 **/
fun calcular(e: Entrada): Resultado { //e vem da entrada
    val diasDaPena = e.pena.emDias(Convencao.PADRAO)
    require(e.detracaoDias in 0 until diasDaPena) { "Detração deve estar entre 0 e a pena" }

    val inicio = e.inicio.minus(e.detracaoDias, DateTimeUnit.DAY)
    require(inicio >= e.dataDoFato) { "O início do cumprimento (já descontada a detração) não pode ser anterior à data do fato" }

    val legislacao = legislacaoAplicavel(e.dataDoFato)
    val fracao = fracaoDeProgressao(e.tipo, e.reincidente, legislacao)
    val primeira = dataDoBeneficio(inicio, fracao.aplicarEm(diasDaPena))
    val livramento = dataDoLivramento(inicio, e.pena, e.tipo, e.reincidente, legislacao)
    val termino = terminoDaPena(inicio, e.pena)

    return when (e.regimeInicial) {
        RegimeInicial.FECHADO -> Resultado(
            legislacao = legislacao,
            progressaoSemiaberto = primeira,
            progressaoAberto = dataDaSegundaProgressao(inicio, e.pena, fracao),
            livramento = livramento,
            terminoDaPena = termino,
        )
        RegimeInicial.SEMIABERTO -> Resultado(
            legislacao = legislacao,
            progressaoSemiaberto = null,
            progressaoAberto = primeira,
            livramento = livramento,
            terminoDaPena = termino,
        )
    }
}