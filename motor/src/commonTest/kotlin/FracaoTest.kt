package calculadora.motor

import kotlin.test.Test
import kotlin.test.assertEquals

class FracaoTest {

    @Test
    fun setentaPorCentoDe90Dias() {
        assertEquals(63, Fracao(70, 100).aplicarEm(90))  // com Double, daria 62
    }

    @Test
    fun umTercoTruncaParaBaixo() {
        assertEquals(486, Fracao(1, 3).aplicarEm(1460))  // 486,67 vira 486
    }
}