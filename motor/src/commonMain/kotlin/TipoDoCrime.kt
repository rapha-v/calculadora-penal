package calculadora.motor

enum class TipoCrime {
    COMUM, VIOLENCIA_OU_GRAVE_AMEACA, HEDIONDO, HEDIONDO_COM_MORTE   
}

        /**
         * Fração de progressão de acordo com os percentuais da tabela da documentação da 
         * Cespedes & Lourenço Advogados (art. 112 da LEP)
         * Reincidente não distingue reincidência específica
         * Ou seja, reincidente em crime comum condenado por crime hediondo cai como reincidente
         */
fun fracaoDeProgressao(tipo: TipoCrime, reincidente: Boolean): Fracao = when (tipo) {
    TipoCrime.COMUM -> if (reincidente) Fracao(20, 100) else Fracao(16, 100)
    TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA -> if (reincidente) Fracao(30, 100) else Fracao(25, 100)
    TipoCrime.HEDIONDO -> if (reincidente) Fracao(60, 100) else Fracao(40, 100)
    TipoCrime.HEDIONDO_COM_MORTE -> if (reincidente) Fracao(70, 100) else Fracao(50, 100)
}
