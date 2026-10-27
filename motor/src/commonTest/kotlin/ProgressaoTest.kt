package calculadora.motor

import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressaoTest {

    // Confere uma tabela inteira de uma lei: (tipo, reincidente, fração esperada)
    private fun confere(lei: Legislacao, casos: List<Triple<TipoCrime, Boolean, Fracao>>) {
        for ((tipo, reincidente, esperada) in casos) {
            assertEquals(esperada, fracaoDeProgressao(tipo, reincidente, lei), "lei=$lei, tipo=$tipo, reincidente=$reincidente")
        }
    }

    @Test
    fun lei11464() = confere(Legislacao.LEI_11464, listOf(
        Triple(TipoCrime.COMUM, false, Fracao(1, 6)),
        Triple(TipoCrime.COMUM, true, Fracao(1, 6)),
        Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, false, Fracao(1, 6)),
        Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, true, Fracao(1, 6)),
        Triple(TipoCrime.HEDIONDO, false, Fracao(2, 5)),
        Triple(TipoCrime.HEDIONDO, true, Fracao(3, 5)),
        Triple(TipoCrime.HEDIONDO_COM_MORTE, false, Fracao(2, 5)),
        Triple(TipoCrime.HEDIONDO_COM_MORTE, true, Fracao(3, 5)),
    ))

    @Test
    fun pacoteAnticrimeBateComTabelaDoEscritorio() = confere(Legislacao.PACOTE_ANTICRIME, listOf(
        Triple(TipoCrime.COMUM, false, Fracao(16, 100)),
        Triple(TipoCrime.COMUM, true, Fracao(20, 100)),
        Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, false, Fracao(25, 100)),
        Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, true, Fracao(30, 100)),
        Triple(TipoCrime.HEDIONDO, false, Fracao(40, 100)),
        Triple(TipoCrime.HEDIONDO, true, Fracao(60, 100)),
        Triple(TipoCrime.HEDIONDO_COM_MORTE, false, Fracao(50, 100)),
        Triple(TipoCrime.HEDIONDO_COM_MORTE, true, Fracao(70, 100)),
    ))

    @Test
    fun lei15358() = confere(Legislacao.LEI_15358, listOf(
        Triple(TipoCrime.COMUM, false, Fracao(16, 100)),
        Triple(TipoCrime.COMUM, true, Fracao(20, 100)),
        Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, false, Fracao(25, 100)),
        Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, true, Fracao(30, 100)),
        Triple(TipoCrime.HEDIONDO, false, Fracao(70, 100)),
        Triple(TipoCrime.HEDIONDO, true, Fracao(80, 100)),
        Triple(TipoCrime.HEDIONDO_COM_MORTE, false, Fracao(75, 100)),
        Triple(TipoCrime.HEDIONDO_COM_MORTE, true, Fracao(85, 100)),
    ))

    @Test
    fun lei15402() = confere(Legislacao.LEI_15402, listOf(
        Triple(TipoCrime.COMUM, false, Fracao(1, 6)),
        Triple(TipoCrime.COMUM, true, Fracao(20, 100)),
        Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, false, Fracao(25, 100)),
        Triple(TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA, true, Fracao(30, 100)),
        Triple(TipoCrime.HEDIONDO, false, Fracao(70, 100)),
        Triple(TipoCrime.HEDIONDO, true, Fracao(80, 100)),
        Triple(TipoCrime.HEDIONDO_COM_MORTE, false, Fracao(75, 100)),
        Triple(TipoCrime.HEDIONDO_COM_MORTE, true, Fracao(85, 100)),
    ))
}