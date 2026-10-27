package calculadora.motor

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LivramentoTest {

    private val pacote = Legislacao.PACOTE_ANTICRIME

    @Test
    fun fracoesBatemComTabelaDoEscritorio() {
        val casos = listOf( // tipo de crime, reincidente, fração esperada
            Triple(TipoCrime.COMUM, false, Fracao(1, 3)),
            Triple(TipoCrime.COMUM, true, Fracao(1, 2)),
            Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, false, Fracao(1, 3)),
            Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, true, Fracao(1, 2)),
            Triple(TipoCrime.HEDIONDO, false, Fracao(2, 3)),
            Triple(TipoCrime.HEDIONDO, true, Fracao(2, 3)),
        )
        // as frações do art. 83 são as mesmas em todas as leis
        for (lei in Legislacao.entries) {
            for ((tipo, reincidente, esperada) in casos) {
                assertEquals(esperada, fracaoDeLivramento(tipo, reincidente, lei), "lei=$lei, tipo=$tipo, reincidente=$reincidente")
            }
        }
    }

    @Test
    fun hediondoComMorteVedadoAPartirDoPacote() {
        for (reincidente in listOf(false, true)) {
            assertEquals(Fracao(2, 3), fracaoDeLivramento(TipoCrime.HEDIONDO_COM_MORTE, reincidente, Legislacao.LEI_11464))
            assertNull(fracaoDeLivramento(TipoCrime.HEDIONDO_COM_MORTE, reincidente, Legislacao.PACOTE_ANTICRIME))
            assertNull(fracaoDeLivramento(TipoCrime.HEDIONDO_COM_MORTE, reincidente, Legislacao.LEI_15358))
            assertNull(fracaoDeLivramento(TipoCrime.HEDIONDO_COM_MORTE, reincidente, Legislacao.LEI_15402))
        }
    }

    @Test
    fun exigePenaDePeloMenosDoisAnos() {
        val inicio = LocalDate(2025, 1, 1)
        val casos = listOf( // pena, permite livramento?
            Pair(Pena(2, 0, 0), true),    // exatamente 2 anos: "igual ou superior"
            Pair(Pena(1, 12, 0), true),   // 2 anos escritos com meses >= 12 (emDias daria 725)
            Pair(Pena(0, 24, 0), true),   // 2 anos só em meses (emDias daria 720)
            Pair(Pena(1, 11, 30), false), // termina 1 dia antes dos 2 anos
            Pair(Pena(1, 0, 0), false),   // abaixo
            Pair(Pena(10, 0, 0), true),   // acima
        )
        for ((pena, esperado) in casos) {
            assertEquals(esperado, penaPermiteLivramento(inicio, pena), "pena=$pena")
        }
    }

    @Test
    fun dataDoLivramentoNoCasoDaReferencia() {
        // início 27/10/2026, pena de 6 anos = 2190 dias
        val inicio = LocalDate(2026, 10, 27)
        val pena = Pena(6, 0, 0)
        assertEquals(LocalDate(2028, 10, 26), dataDoLivramento(inicio, pena, TipoCrime.COMUM, false, pacote))    // 1/3 = 730 d (a referência dá 729 por usar 33,3%)
        assertEquals(LocalDate(2029, 10, 26), dataDoLivramento(inicio, pena, TipoCrime.COMUM, true, pacote))     // 1/2 = 1095 d
        assertEquals(LocalDate(2030, 10, 26), dataDoLivramento(inicio, pena, TipoCrime.HEDIONDO, false, pacote)) // 2/3 = 1460 d
    }

    @Test
    fun dataDoLivramentoNaoCabe() {
        val inicio = LocalDate(2026, 10, 27)
        assertNull(dataDoLivramento(inicio, Pena(6, 0, 0), TipoCrime.HEDIONDO_COM_MORTE, false, pacote)) // vedado
        assertNull(dataDoLivramento(inicio, Pena(1, 0, 0), TipoCrime.COMUM, false, pacote))              // pena < 2 anos
    }
}