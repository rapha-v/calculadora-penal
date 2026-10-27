package calculadora.motor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.datetime.LocalDate

class LivramentoTest {

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

        for ((tipo, reincidente, esperada) in casos) {
            assertEquals(esperada, fracaoDeLivramento(tipo, reincidente), "tipo=$tipo, reincidente=$reincidente")
        }
    }

    @Test
    fun hediondoComMorteEVedado() {
        // vedado para primário e para reincidente
        assertNull(fracaoDeLivramento(TipoCrime.HEDIONDO_COM_MORTE, false))
        assertNull(fracaoDeLivramento(TipoCrime.HEDIONDO_COM_MORTE, true))
    }

    @Test
    fun exigePenaDePeloMenosDoisAnos() {
        val inicio = LocalDate(2025, 1, 1)
        val casos = listOf( // pena, permite livramento?
            Pair(Pena(2, 0, 0), true),    //exatamente 2 anos: "igual ou superior"
            Pair(Pena(1, 12, 0), true),   //2 anos escritos com meses >= 12 (emDias daria 725)
            Pair(Pena(0, 24, 0), true),   //2 anos só em meses (emDias daria 720)
            Pair(Pena(1, 11, 30), false), //termina 1 dia antes dos 2 anos
            Pair(Pena(1, 0, 0), false),   //abaixo
            Pair(Pena(10, 0, 0), true),   //acima
        )

        for ((pena, esperado) in casos) {
            assertEquals(esperado, penaPermiteLivramento(inicio, pena), "pena=$pena")
        }
    }
}