package calculadora.motor

/**
 * Fração exata, representada por dois inteiros (ex.: 16/100, 1/3).
 * Correcao: Não usamos Double porque 0.7 * 90 em ponto flutuante dá 62.999..., e não 63. 
 */
data class Fracao(val numerador: Int, val denominador: Int) {

    // O bloco init roda na criação do objeto: impede que exista uma fração inválida
    init {
        require(denominador > 0) { "Denominador deve ser positivo" }
        require(numerador >= 0) { "Numerador não pode ser negativo" }
        require(numerador <= denominador) { "Fração não pode passar de 100%" }
        //uma fraçao de pena nunca será negativa
    }

    /** Aplica a fração a uma quantidade de dias, truncando o resultado. */
    fun aplicarEm(dias: Int): Int {
        require(dias >= 0) { "Quantidade de dias não pode ser negativa" }
        return (dias.toLong() * numerador / denominador).toInt()
    }
}