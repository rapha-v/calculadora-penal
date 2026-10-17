package calculadora.motor

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus //sem este, dá "Unresolved reference" no plus
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus

/**
 * Data em que o benefício é atingido: início + dias da fração, sem subtrair 1 dia.
 * Regra conferida com as 8 datas do exemplo da pág. 12 do manual VEC.
 * (O término da pena usa outra regra: calendário real, menos 1 dia.)
 */

fun dataDoBeneficio(inicio: LocalDate, dias: Int): LocalDate {
    require(dias>=0) {"Quantidade de dias não pode ser negativa"}
    return inicio.plus(dias, DateTimeUnit.DAY)
}

/**
 * Data em que a pena termina: início + pena pelo calendário real - 1.
 * Aplica-se o calendário real e não a convenção.
 * É diferente da data do benefício
 */
fun terminoDaPena(inicio: LocalDate, pena: Pena): LocalDate {
    
    val periodo = DatePeriod(years = pena.anos, months = pena.meses, days = pena.dias)

    val data = inicio.plus(periodo)                  //soma o período pelo calendário real
    return data.minus(1, DateTimeUnit.DAY)       //subtrai 1 dia de data conforme a regra
}