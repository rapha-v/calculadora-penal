package calculadora.motor

import kotlinx.datetime.LocalDate

        /**
        * Fração da pena total para o livramento condicional, segundo a tabela da
        * documentação da Cespedes & Lourenço Advogados (art. 83 do Código Penal) e o manual VEC (pág. 6).
        *
        * Violência ou grave ameaça segue a regra do crime comum (o manual só separa comum e hediondo).
        * Simplificação: pela lei, o hediondo exige também que o condenado não seja reincidente
        * específico em crime hediondo; a tabela do escritório não faz essa distinção.
        *
        * Retorno null quando o livramento é vedado (hediondo com resultado morte)
        **/
fun fracaoDeLivramento(tipo: TipoCrime, reincidente: Boolean): Fracao? = when (tipo) {
    TipoCrime.COMUM, TipoCrime.VIOLENCIA_OU_GRAVE_AMEACA -> if (reincidente) Fracao(1, 2) else Fracao(1, 3)
    TipoCrime.HEDIONDO -> Fracao(2, 3)
    TipoCrime.HEDIONDO_COM_MORTE -> null
}


        /**
        * Verifica se a pena permite livramento, de acordo com a exigência de pena igual
        * ou superior a dois anos (manual VEC, pág. 7)
        * Utiliza o calendário real com base no art. 10 do Código Penal
        **/
fun penaPermiteLivramento(inicio: LocalDate, pena: Pena): Boolean {
    return terminoDaPena(inicio, pena) >= terminoDaPena(inicio, Pena(2,0,0)) 
}