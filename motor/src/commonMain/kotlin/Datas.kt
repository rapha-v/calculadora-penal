package calculadora.motor

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus   //sem este, dá "Unresolved reference" no plus

/**
 * Data em que o benefício é atingido: início + dias da fração, sem subtrair 1 dia.
 * Regra conferida com as 8 datas do exemplo da pág. 12 do manual VEC.
 * (O término da pena usa outra regra: calendário real, menos 1 dia.)
 */

fun dataDoBeneficio(inicio: LocalDate, dias: Int): LocalDate {
    require(dias>=0) {"Quantidade de dias não pode ser negativa"}
    return inicio.plus(dias, DateTimeUnit.DAY)
}