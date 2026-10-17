package calculadora.motor

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatasTest{

    private val inicio = LocalDate(2000, 8, 31)   // ano, mês, dia

    @Test
    fun umSextoBateComManualVec() {
        val dias = Fracao(1, 6).aplicarEm(Pena(4, 0, 0).emDias(Convencao.PADRAO)) + 30  //+30 dias de fuga
        assertEquals(LocalDate(2001, 5, 31), dataDoBeneficio(inicio, dias))
    }

    @Test
    fun umTercoBateComManualVec() {
        val dias = Fracao(1, 3).aplicarEm(Pena(4, 0, 0).emDias(Convencao.PADRAO)) + 30  //+30 dias de fuga
        assertEquals(LocalDate(2002, 1, 29), dataDoBeneficio(inicio, dias))
    }

    @Test
    fun tresQuartosBateComManualVec() {
        val dias = Fracao(3, 4).aplicarEm(Pena(4, 0, 0).emDias(Convencao.PADRAO)) + 30  //+30 dias de fuga
        assertEquals(LocalDate(2003, 9, 30), dataDoBeneficio(inicio, dias))
    }

    @Test
    fun zeroDiasDevolveAMesmaData() {
           assertEquals(inicio, dataDoBeneficio(inicio, 0))
    }

    @Test
    fun zDiasNegativosExcecao() {
            assertFailsWith<IllegalArgumentException> { dataDoBeneficio(inicio, -1) }
    }

    @Test
    fun terminoBateComExemploUmManualVec() {
        assertEquals(LocalDate(2004, 8, 30), terminoDaPena(inicio, Pena(4,0,0)))
    }

    @Test
    fun terminoBateComExemploDoisManualVec() {
        assertEquals(LocalDate(2011, 5, 9), terminoDaPena(LocalDate(2007, 2, 10), Pena(4,3,0)))
    }

}