package calculadora.motor

import kotlinx.datetime.LocalDate

    /**
    * Fração da pena total para o livramento condicional (art. 83 do CP), segundo a tabela da
    * documentação da Cespedes & Lourenço Advogados e o manual VEC (pág. 6).
    *
    * Violência ou grave ameaça segue a regra do crime comum (o manual só separa comum e hediondo).
    * Simplificação: pela lei, o hediondo exige também que o condenado não seja reincidente
    * específico em crime hediondo; a tabela do escritório não faz essa distinção.
    *
    * Retorna null quando o livramento é vedado: hediondo com resultado morte, para fatos a partir
    * do Pacote Anticrime (art. 112, VI, "a", e VIII, da LEP). Antes dele, segue a regra do hediondo.
    **/
fun fracaoDeLivramento(tipo: TipoCrime, reincidente: Boolean, legislacao: Legislacao): Fracao? = when (tipo) {
    TipoCrime.COMUM, TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA -> if (reincidente) Fracao(1, 2) else Fracao(1, 3)
    TipoCrime.HEDIONDO -> Fracao(2, 3)
    TipoCrime.HEDIONDO_COM_MORTE -> if(legislacao == Legislacao.LEI_11464) Fracao(2, 3) else null
}

        /**
        * Verifica se a pena permite livramento, de acordo com a exigência de pena igual
        * ou superior a dois anos (manual VEC, pág. 7)
        *Utiliza o calendário real com base no art. 10 do código penal
        */
fun penaPermiteLivramento(inicio: LocalDate, pena: Pena): Boolean {
    return terminoDaPena(inicio, pena) >= terminoDaPena(inicio, Pena(2,0,0))
}



    /**
    * Data prevista para o livramento condicional, ou null quando não cabe
    * (tipo de crime vedado pela legislação ou pena menor que 2 anos).
    * Fração sobre a pena total em dias (Convencao.PADRAO), conforme o manual VEC (pág. 6);
    * a data segue a regra de dataDoBeneficio (sem subtrair 1 dia).
    * Estimativa: os requisitos subjetivos do art. 83 do CP (comportamento etc.) não são verificados.
    */
fun dataDoLivramento(inicio: LocalDate, pena: Pena, tipo: TipoCrime, reincidente: Boolean, legislacao: Legislacao): LocalDate? {
    val fracao = fracaoDeLivramento(tipo, reincidente, legislacao) ?: return null
    if (!penaPermiteLivramento(inicio, pena)) return null
    val dias = fracao.aplicarEm(pena.emDias(Convencao.PADRAO))
    return dataDoBeneficio(inicio, dias)
}