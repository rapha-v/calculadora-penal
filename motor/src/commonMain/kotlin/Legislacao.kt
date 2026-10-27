package calculadora.motor

import kotlinx.datetime.LocalDate

/** Leis que definem as frações de progressão e as vedações, pela data do fato. */
enum class Legislacao { LEI_11464, PACOTE_ANTICRIME, LEI_15358, LEI_15402 }

private val INICIO_LEI_11464 = LocalDate(2007, 3, 29)
private val INICIO_PACOTE_ANTICRIME = LocalDate(2020, 1, 23)
private val INICIO_LEI_15358 = LocalDate(2026, 3, 25) 
private val INICIO_LEI_15402 = LocalDate(2026, 5, 8)

/**
 * Lei aplicável pela data do fato (crime), e não pela data de início da pena:
 * lei penal mais grave não retroage (art. 5º, XL, da Constituição).
 * Datas de vigência conferidas nos textos das leis (Planalto).
 * Limitação: fatos antes de 29/03/2007 não são calculados.
 */
fun legislacaoAplicavel(dataDoFato: LocalDate): Legislacao {
    require(dataDoFato >= INICIO_LEI_11464) { "Fatos anteriores a 29/03/2007 não são calculados" }
    return when {
        dataDoFato >= INICIO_LEI_15402 -> Legislacao.LEI_15402
        dataDoFato >= INICIO_LEI_15358 -> Legislacao.LEI_15358
        dataDoFato >= INICIO_PACOTE_ANTICRIME -> Legislacao.PACOTE_ANTICRIME
        else -> Legislacao.LEI_11464
    }
}