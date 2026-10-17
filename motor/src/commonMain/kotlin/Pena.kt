package calculadora.motor

data class Pena(val anos: Int, val meses: Int, val dias: Int) {
    init {
        //verificando se os dados sao negativos
        require(anos >= 0 && meses >= 0 && dias >= 0) { "Componentes não podem ser negativos" }
        require(anos+meses+dias >0) {"Não calcula pena zerada"}
    }
}

data class Convencao(val diasPorAno: Int, val diasPorMes: Int) {
    init{ //para nao esquecer: init roda toda vez que um objeto é criado
        //validação da classe Convenção
        require(diasPorAno > 0 && diasPorMes > 0) {"Requer que os dias sejam maior que zero para conversão"}
    }
    companion object{
        /**
         * Convenção usada para estimar a pena em dias.
         * 365 dias/ano: reproduz as 8 datas do exemplo do manual VEC (Vara de execuções criminais).
         * 30 dias/mês: HIPÓTESE não confirmada (o exemplo do VEC não tem meses).
         */
        val PADRAO= Convencao(diasPorAno = 365, diasPorMes = 30)
    }
}

fun Pena.emDias(c: Convencao): Int = anos*c.diasPorAno + meses*c.diasPorMes + dias