package calculadora.motor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
}