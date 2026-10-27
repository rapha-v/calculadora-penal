package calculadora.motor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

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
    

    @Test
    fun segundaProgressaoSobreOSaldo() {
        // início 27/10/2026, pena de 6 anos = 2190 dias
        val inicio = LocalDate(2026, 10, 27)
        val pena = Pena(6, 0, 0)
        val casos = listOf( // fração, data esperada da 2ª progressão
            Pair(Fracao(16, 100), LocalDate(2028, 8, 1)),  // 350 + 16% de 1840 (294): igual à referência
            Pair(Fracao(1, 6), LocalDate(2028, 8, 26)),    // 365 + 1/6 de 1825 (304)
            Pair(Fracao(40, 100), LocalDate(2030, 8, 28)), // 876 + 40% de 1314 (525)
            Pair(Fracao(70, 100), LocalDate(2032, 4, 10)), // 1533 + 70% de 657 (459)
        )
        for ((fracao, esperada) in casos) {
            assertEquals(esperada, dataDaSegundaProgressao(inicio, pena, fracao), "fracao=$fracao")
        }
    }
}
