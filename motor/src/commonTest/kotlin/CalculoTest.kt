package calculadora.motor

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CalculoTest {

    // caso da calculadora de referência: 6 anos, início 27/10/2026, comum primário
    private val base = Entrada(
        pena = Pena(6, 0, 0),
        inicio = LocalDate(2026, 10, 27),
        dataDoFato = LocalDate(2025, 6, 1), // Pacote Anticrime: 16%
        tipo = TipoCrime.COMUM,
        reincidente = false,
    )

    @Test
    fun regimeFechado() {
        assertEquals(
            Resultado(
                legislacao = Legislacao.PACOTE_ANTICRIME,
                progressaoSemiaberto = LocalDate(2027, 10, 12),
                progressaoAberto = LocalDate(2028, 8, 1),
                livramento = LocalDate(2028, 10, 26),
                terminoDaPena = LocalDate(2032, 10, 26),
            ),
            calcular(base),
        )
    }

    @Test
    fun regimeSemiabertoTemSoUmaProgressao() {
        val r = calcular(base.copy(regimeInicial = RegimeInicial.SEMIABERTO))
        assertEquals(null, r.progressaoSemiaberto)
        assertEquals(LocalDate(2027, 10, 12), r.progressaoAberto) // 16% da pena total
    }

    @Test
    fun detracaoAntecipaTodasAsDatas() {
        val r = calcular(base.copy(detracaoDias = 30)) // início efetivo: 27/09/2026
        assertEquals(LocalDate(2027, 9, 12), r.progressaoSemiaberto)
        assertEquals(LocalDate(2028, 7, 2), r.progressaoAberto)
        assertEquals(LocalDate(2028, 9, 26), r.livramento)
        assertEquals(LocalDate(2032, 9, 26), r.terminoDaPena)
    }

    @Test
    fun fatoAntigoUsaLei11464() {
        // hediondo com morte em 2018: 2/5 na progressão e livramento permitido (2/3)
        val r = calcular(base.copy(dataDoFato = LocalDate(2018, 5, 10), tipo = TipoCrime.HEDIONDO_COM_MORTE))
        assertEquals(Legislacao.LEI_11464, r.legislacao)
        assertEquals(LocalDate(2029, 3, 21), r.progressaoSemiaberto)
        assertEquals(LocalDate(2030, 10, 26), r.livramento)
    }

    @Test
    fun rejeitaDetracaoInvalida() {
        assertFailsWith<IllegalArgumentException> { calcular(base.copy(detracaoDias = -1)) }
        assertFailsWith<IllegalArgumentException> { calcular(base.copy(detracaoDias = 2190)) }
    }

    @Test
    fun rejeitaInicioAntesDoFato() {
        //flagrante: início no próprio dia do fato é válido (não pode lançar exceção)
        calcular(base.copy(dataDoFato = LocalDate(2026, 10, 27)))
        // início um dia antes do fato: impossível
        assertFailsWith<IllegalArgumentException> {
            calcular(base.copy(dataDoFato = LocalDate(2026, 10, 28)))
        }
        // a detração recua o início para 27/09, antes do fato em 01/10: também impossível
        assertFailsWith<IllegalArgumentException> {
            calcular(base.copy(dataDoFato = LocalDate(2026, 10, 1), detracaoDias = 30))
        }
    }

    @Test
    fun remicaoAntecipaComoADetracao() {
        // os dois contam como pena cumprida: 30 dias de qualquer um dão as mesmas datas
        val comDetracao = calcular(base.copy(detracaoDias = 30)) // datas conferidas no teste da detração
        assertEquals(comDetracao, calcular(base.copy(remicaoDias = 30)))
        assertEquals(comDetracao, calcular(base.copy(detracaoDias = 10, remicaoDias = 20)))
    }

    @Test
    fun remicaoNaoEntraNaValidacaoDoFato() {
        // fato em 20/10, início em 27/10, 30 dias remidos: o início recuado (27/09) cai antes
        // do fato, mas é válido, porque a remição foi ganha cumprindo a pena (não pode lançar exceção)
        calcular(base.copy(dataDoFato = LocalDate(2026, 10, 20), remicaoDias = 30))
    }

    @Test
    fun rejeitaRemicaoInvalida() {
        assertFailsWith<IllegalArgumentException> { calcular(base.copy(remicaoDias = -1)) }
        // a soma igual à pena não deixa nada a cumprir
        assertFailsWith<IllegalArgumentException> {
            calcular(base.copy(detracaoDias = 1000, remicaoDias = 1190))
        }
        // detração negativa "escondida" na soma: -5 + 10 = 5 passaria só pela soma
        assertFailsWith<IllegalArgumentException> {
            calcular(base.copy(detracaoDias = -5, remicaoDias = 10))
        }
    }

    @Test
    fun fatoAntesDe2007UsaLei8072() {
        // hediondo com morte em 2005: 1/6 na progressão e livramento PERMITIDO (2/3);
        // a vedação do livramento só veio com o Pacote Anticrime (2020)
        val r = calcular(base.copy(dataDoFato = LocalDate(2005, 5, 10), tipo = TipoCrime.HEDIONDO_COM_MORTE))
        assertEquals(Legislacao.LEI_8072, r.legislacao)
        assertEquals(LocalDate(2027, 10, 27), r.progressaoSemiaberto) // 365 dias (1/6 de 2190)
        assertEquals(LocalDate(2028, 8, 26), r.progressaoAberto)      // +304 dias (1/6 do saldo de 1825)
        assertEquals(LocalDate(2030, 10, 26), r.livramento)           // 1460 dias (2/3), passa por 29/02/2028
    }
}