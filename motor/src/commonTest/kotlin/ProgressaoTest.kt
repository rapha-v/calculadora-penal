package calculadora.motor

import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressaoTest {

    @Test
    fun percentuaisBatemComTabelaDoEscritorio() {
        val casos = listOf( //lista do tipo de crime, reincidente e fração esperada
            Triple(TipoCrime.COMUM, false, Fracao(16, 100)),
            Triple(TipoCrime.COMUM, true, Fracao(20, 100)),
            Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, false, Fracao(25, 100)),
            Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, true, Fracao(30, 100)),
            Triple(TipoCrime.HEDIONDO, false, Fracao(40, 100)),
            Triple(TipoCrime.HEDIONDO, true, Fracao(60, 100)),
            Triple(TipoCrime.HEDIONDO_COM_MORTE, false, Fracao(50, 100)),
            Triple(TipoCrime.HEDIONDO_COM_MORTE, true, Fracao(70, 100)),
        )

        for ((tipo, reincidente, esperada) in casos) { //percorre a lista e pega as informações
            assertEquals(esperada, fracaoDeProgressao(tipo, reincidente), "tipo=$tipo, reincidente=$reincidente")
        }
    }
}