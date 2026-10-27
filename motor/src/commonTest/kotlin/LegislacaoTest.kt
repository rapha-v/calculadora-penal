package calculadora.motor

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LegislacaoTest {

    @Test
    fun escolheLeiPelaDataDoFato() {
        // testa o último dia de cada lei e o primeiro da seguinte (as bordas)
        val casos = listOf( // data do fato, lei esperada
            Pair(LocalDate(2007, 3, 29), Legislacao.LEI_11464),
            Pair(LocalDate(2020, 1, 22), Legislacao.LEI_11464),
            Pair(LocalDate(2020, 1, 23), Legislacao.PACOTE_ANTICRIME),
            Pair(LocalDate(2026, 3, 24), Legislacao.PACOTE_ANTICRIME),
            Pair(LocalDate(2026, 3, 25), Legislacao.LEI_15358),
            Pair(LocalDate(2026, 5, 7), Legislacao.LEI_15358),
            Pair(LocalDate(2026, 5, 8), Legislacao.LEI_15402),
        )

        for ((data, esperada) in casos) {
            assertEquals(esperada, legislacaoAplicavel(data), "data=$data")
        }
    }

    @Test
    fun rejeitaFatoAntesDe2007() {
        assertFailsWith<IllegalArgumentException> { legislacaoAplicavel(LocalDate(2007, 3, 28)) }
    }
}