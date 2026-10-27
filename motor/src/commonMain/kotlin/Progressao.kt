package calculadora.motor

import kotlinx.datetime.LocalDate

    /**
    * Data da 2ª progressão (semiaberto → aberto): a mesma fração aplicada ao saldo da pena
    * (pena total − dias da 1ª progressão), contada a partir da data da 1ª progressão.
    * Regra conferida com a calculadora de referência indicada pelo escritório:
    * 6 anos a partir de 27/10/2026, 16% → 1ª em 12/10/2027, saldo 1840 d, 2ª em 01/08/2028.
    **/
fun dataDaSegundaProgressao(inicio: LocalDate, pena: Pena, fracao: Fracao): LocalDate {
    val diasTotal = pena.emDias(Convencao.PADRAO)
    val dias1 = fracao.aplicarEm(diasTotal)
    val saldo = diasTotal - dias1
    val dias2 = fracao.aplicarEm(saldo)
    return dataDoBeneficio(dataDoBeneficio(inicio, dias1), dias2)
}

        /**
        * Fração de progressão (art. 112 da LEP) pela legislação vigente na data do fato.
        * - LEI_11464: art. 2º, §2º, da Lei 8.072/90 (hediondos) e 1/6 da LEP para os demais.
        * - PACOTE_ANTICRIME: tabela da documentação da Cespedes & Lourenço Advogados.
        * - LEI_15358 e LEI_15402: só o que cada uma alterou; o resto vem da lei anterior.
        * "Reincidente" não distingue reincidência específica, como na tabela do escritório.
        **/
fun fracaoDeProgressao(tipo: TipoCrime, reincidente: Boolean, legislacao: Legislacao): Fracao = when (legislacao) {
    Legislacao.LEI_11464 -> when (tipo) {
        TipoCrime.COMUM, TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA -> Fracao(1,6)
        TipoCrime.HEDIONDO, TipoCrime.HEDIONDO_COM_MORTE -> if (reincidente) Fracao(3, 5) else Fracao(2, 5)
    }
    Legislacao.PACOTE_ANTICRIME -> when (tipo) {
        TipoCrime.COMUM -> if (reincidente) Fracao(20, 100) else Fracao(16, 100)
        TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA -> if (reincidente) Fracao(30, 100) else Fracao(25, 100)
        TipoCrime.HEDIONDO -> if (reincidente) Fracao(60, 100) else Fracao(40, 100)
        TipoCrime.HEDIONDO_COM_MORTE -> if (reincidente) Fracao(70, 100) else Fracao(50, 100)
    }
    Legislacao.LEI_15358 -> when (tipo) {
        TipoCrime.HEDIONDO -> if (reincidente) Fracao(80, 100) else Fracao(70, 100)
        TipoCrime.HEDIONDO_COM_MORTE -> if (reincidente) Fracao(85, 100) else Fracao(75, 100)
        TipoCrime.COMUM, TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA ->
            fracaoDeProgressao(tipo, reincidente, Legislacao.PACOTE_ANTICRIME)
    }
    Legislacao.LEI_15402 ->
        if (tipo == TipoCrime.COMUM && !reincidente) Fracao(1, 6) 
        else fracaoDeProgressao(tipo, reincidente, Legislacao.LEI_15358)
}