package calculadora.motor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith //importações do kotlin

class PenaTest {

    @Test
    fun anosParaDias() {
        assertEquals(1460, Pena(4, 0, 0).emDias(Convencao.PADRAO))
    }

    @Test
    fun anoseMesesParaDias() {
        assertEquals(785, Pena(1, 14, 0).emDias(Convencao.PADRAO))
    }

    @Test
    fun conversaoComFracao() {
        assertEquals(486, Fracao(1, 3).aplicarEm(Pena(4, 0, 0).emDias(Convencao.PADRAO)))
    }

    @Test
    fun casoNegativoExcecao() {
        assertFailsWith<IllegalArgumentException> {Pena(-1, 0, 0)}
    }

    @Test
    fun casoZeradoExcecao() {
        assertFailsWith<IllegalArgumentException> {Pena(0, 0, 0)}
    }

    @Test
    fun convencaoErrada() { //Se o require falhar/for alterado
        assertFailsWith<IllegalArgumentException> {Convencao(0,30)}
    }
}