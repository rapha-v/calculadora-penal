package calculadora.motor

import kotlinx.datetime.LocalDate

/** Leis que definem as frações de progressão e as vedações, pela data do fato. */
enum class Legislacao { LEI_8072, LEI_11464, PACOTE_ANTICRIME, LEI_15358, LEI_15402 }

private val INICIO_LEI_8072 = LocalDate(1990, 7, 26)
private val INICIO_LEI_11464 = LocalDate(2007, 3, 29)
private val INICIO_PACOTE_ANTICRIME = LocalDate(2020, 1, 23)
private val INICIO_LEI_15358 = LocalDate(2026, 3, 25) //a confirmar
private val INICIO_LEI_15402 = LocalDate(2026, 5, 8)

/**
 * Lei aplicável pela data do fato (crime), e não pela data de início da pena:
 * lei penal mais grave não retroage (art. 5º, XL, da Constituição).
 * Datas de vigência conferidas nos textos das leis (Planalto).
 * LEI_8072 vale de 26/07/1990 (publicação no DOU, art. 12) a 28/03/2007; fatos anteriores não são calculados.
 * Atenção: alguns crimes só viraram hediondos depois de 1990 (ex.: homicídio qualificado, Lei 8.930/1994);
 * o tipo informado deve ser o da época do fato.
 */
fun legislacaoAplicavel(dataDoFato: LocalDate): Legislacao {
    require(dataDoFato >= INICIO_LEI_8072) { "Fatos anteriores a 26/07/1990 não são calculados" }
    return when {
        dataDoFato >= INICIO_LEI_15402 -> Legislacao.LEI_15402
        dataDoFato >= INICIO_LEI_15358 -> Legislacao.LEI_15358
        dataDoFato >= INICIO_PACOTE_ANTICRIME -> Legislacao.PACOTE_ANTICRIME
        dataDoFato >= INICIO_LEI_11464 -> Legislacao.LEI_11464
        else -> Legislacao.LEI_8072
    }
}