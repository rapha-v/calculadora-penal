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
}