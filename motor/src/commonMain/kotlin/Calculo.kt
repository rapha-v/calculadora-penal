package calculadora.motor

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

/** Regime inicial fixado na sentença. */
enum class RegimeInicial { FECHADO, SEMIABERTO }

/**
 * Dados informados pelo usuário.
 * Detração: dias de prisão provisória (art. 42 do Código Penal).
 * Remição: dias já remidos por trabalho ou estudo, declarados pelo juiz (arts. 126 e 128 da LEP).
 **/
data class Entrada(
    val pena: Pena,
    val inicio: LocalDate,
    val dataDoFato: LocalDate,
    val tipo: TipoCrime,
    val reincidente: Boolean,
    val regimeInicial: RegimeInicial = RegimeInicial.FECHADO,
    val detracaoDias: Int = 0,
    val remicaoDias: Int = 0
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
 * Detração e remição contam como pena cumprida, então todas as datas partem do início
 * recuado desses dias (a confirmar com o escritório).
 * Só a detração entra na validação contra a data do fato: a remição é ganha durante o
 * cumprimento, então o início recuado por ela pode cair antes do fato.
 **/
fun calcular(e: Entrada): Resultado { //e vem da entrada
    val diasDaPena = e.pena.emDias(Convencao.PADRAO)
    require(e.remicaoDias >= 0) {"A remição não pode ter dias negativos"}
    require(e.detracaoDias >= 0) {"A detração não pode ter dias negativos"}
    require((e.detracaoDias + e.remicaoDias) in 0 until diasDaPena) { "Detração somada à remição deve estar entre 0 e a pena" }

    val inicioComDetracao = e.inicio.minus(e.detracaoDias, DateTimeUnit.DAY)
    require(inicioComDetracao >= e.dataDoFato) { "O início do cumprimento (já descontada a detração) não pode ser anterior à data do fato" }
    val inicio = inicioComDetracao.minus(e.remicaoDias, DateTimeUnit.DAY)

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