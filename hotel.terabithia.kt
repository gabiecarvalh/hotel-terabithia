import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor

object Config {
    const val HOTEL = "Terabithia"
    const val SENHA = "2678"
    const val TOTAL_QUARTOS = 20
    const val MAX_HOSPEDES = 15
    const val CAPACIDADE_LARANJA = 150
    const val EXTRAS_LARANJA = 70
    const val CAPACIDADE_COLORADO = 350
    const val VALOR_GARCOM_HORA = 10.50
    const val LITROS_CAFE_POR_PESSOA = 0.2
    const val LITROS_AGUA_POR_PESSOA = 0.5
    const val SALGADOS_POR_PESSOA = 7
    const val VALOR_CAFE_LITRO = 0.80
    const val VALOR_AGUA_LITRO = 0.40
    const val VALOR_SALGADOS_CENTO = 34.00
    const val LITROS_TANQUE = 42.0
}

data class Reserva(
    val hospede: String,
    val quarto: Int,
    val diarias: Int,
    val total: Double
)

data class Hospede(
    var nome: String,
    var dataCadastro: LocalDateTime
)

data class Evento(
    val auditorio: String,
    val empresa: String,
    val dia: String,
    val data: LocalDate,
    val horaInicio: Int,
    val horaFim: Int,
    val convidados: Int,
    val duracao: Int,
    val garcons: Int,
    val custoGarcons: Double,
    val custoBuffet: Double,
    val total: Double
)

data class Orcamento(
    val empresa: String,
    val total: Double
)

data class OpcaoAbastecimento(
    val posto: String,
    val combustivel: String,
    val precoLitro: Double,
    val total: Double
)

class Sistema {
    val quartosOcupados = BooleanArray(Config.TOTAL_QUARTOS)
    val reservas = mutableListOf<Reserva>()
    val hospedes = mutableListOf<Hospede>()
    val eventos = mutableListOf<Evento>()
    val orcamentos = mutableListOf<Orcamento>()
    val abastecimentos = mutableListOf<OpcaoAbastecimento>()
}

fun main() {
    val sistema = Sistema()
    val nome = autenticar()

    if (nome == null) {
        return
    }

    println()
    println("Bem-vindo ao Hotel ${Config.HOTEL}, $nome. É um imenso prazer ter você por aqui!")
    menuPrincipal(sistema, nome)
}

fun autenticar(): String? {
    println("Bem-vindo ao ${Config.HOTEL}")
    print("Nome do usuário: ")
    val nome = readlnOrNull()?.trim().orEmpty()

    var tentativas = 0

    while (tentativas < 3) {
        print("Senha: ")
        val senha = readlnOrNull()?.trim().orEmpty()
        tentativas++

        if (senha == Config.SENHA) {
            return nome
        }

        if (tentativas < 3) {
            println("Senha incorreta. Tentativa $tentativas de 3.")
        }
    }

    println("Número máximo de tentativas excedido. Sistema bloqueado.")
    return null
}

fun menuPrincipal(sistema: Sistema, nome: String) {
    while (true) {
        println()
        println("===== HOTEL ${Config.HOTEL} =====")
        println("1. Reservas de Quartos")
        println("2. Cadastro de Hóspedes")
        println("3. Eventos")
        println("4. Ar-Condicionado")
        println("5. Abastecimento")
        println("6. Relatórios Operacionais")
        println("7. Sair")
        print("Opção: ")

        when (readlnOrNull()?.trim()) {
            "1" -> reservas(sistema, nome)
            "2" -> cadastroHospedes(sistema)
            "3" -> eventos(sistema)
            "4" -> arCondicionado(sistema, nome)
            "5" -> abastecimento(sistema, nome)
            "6" -> relatorios(sistema)
            "7" -> {
                println("Muito obrigado e até logo, $nome.")
                return
            }
            else -> erroOpcao()
        }
    }
}

fun erroOpcao() {
    println("Opção inválida.")
}

fun erroValor(nome: String) {
    println("Valor inválido, $nome")
}

fun formatarMoeda(valor: Double): String {
    return String.format(Locale("pt", "BR"), "R$ %.2f", valor)
}

fun formatarDataHora(dataHora: LocalDateTime): String {
    return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
}

fun lerDouble(mensagem: String): Double? {
    print(mensagem)
    return readlnOrNull()?.trim()?.replace(',', '.')?.toDoubleOrNull()
}

fun lerInteiro(mensagem: String): Int? {
    print(mensagem)
    return readlnOrNull()?.trim()?.toIntOrNull()
}

fun confirmar(mensagem: String): Boolean? {
    print(mensagem)
    return when (readlnOrNull()?.trim()?.uppercase()) {
        "S" -> true
        "N" -> false
        else -> null
    }
}

// ==================== RESERVAS ====================

fun reservas(sistema: Sistema, nomeUsuario: String) {
    println()
    println("[Reservas]")

    val diaria = lerDouble("Informe o valor da diária: ")
    val diarias = lerInteiro("Informe a quantidade de diárias (1-30): ")

    if (diaria == null || diaria <= 0 || diarias == null || diarias !in 1..30) {
        erroValor(nomeUsuario)
        return
    }

    print("Informe o nome do hóspede: ")
    val hospede = readlnOrNull()?.trim().orEmpty()

    if (hospede.isBlank()) {
        erroValor(nomeUsuario)
        return
    }

    val tipo = lerTipoQuarto(nomeUsuario) ?: return

    val quarto = escolherQuarto(sistema, nomeUsuario) ?: return
    val fator = when (tipo) {
        'S' -> 1.00
        'E' -> 1.35
        'L' -> 1.65
        else -> return
    }
    val descricaoTipo = when (tipo) {
        'S' -> "Standard"
        'E' -> "Executivo"
        'L' -> "Luxo"
        else -> ""
    }

    val subtotal = diaria * diarias * fator
    val taxaServico = subtotal * 0.10
    val total = subtotal + taxaServico

    println()
    println("Resumo:")
    println("Hóspede: $hospede")
    println("Quarto: $quarto ($descricaoTipo)")
    println("Subtotal: ${formatarMoeda(subtotal)}")
    println("Taxa de serviço (10%): ${formatarMoeda(taxaServico)}")
    println("Total: ${formatarMoeda(total)}")
    println()

    val confirmacao = confirmar("$nomeUsuario, confirma a reserva? (S/N): ")

    if (confirmacao == null) {
        erroOpcao()
        return
    }

    if (!confirmacao) {
        println("Reserva não efetuada.")
        return
    }

    sistema.quartosOcupados[quarto - 1] = true
    sistema.reservas.add(Reserva(hospede, quarto, diarias, total))

    println("Reserva efetuada com sucesso.")
    mostrarMapaQuartos(sistema)
}

fun lerTipoQuarto(nomeUsuario: String): Char? {
    print("Tipo de quarto (S/E/L): ")
    return when (readlnOrNull()?.trim()?.uppercase()) {
        "S" -> 'S'
        "E" -> 'E'
        "L" -> 'L'
        else -> {
            erroValor(nomeUsuario)
            null
        }
    }
}

fun escolherQuarto(sistema: Sistema, nomeUsuario: String): Int? {
    while (true) {
        val quarto = lerInteiro("Escolha um quarto (1-20): ")

        if (quarto == null || quarto !in 1..Config.TOTAL_QUARTOS) {
            erroValor(nomeUsuario)
            return null
        }

        if (sistema.quartosOcupados[quarto - 1]) {
            println("Quarto já está ocupado")
            mostrarQuartosLivres(sistema)
            continue
        }

        return quarto
    }
}

fun mostrarQuartosLivres(sistema: Sistema) {
    val livres = mutableListOf<Int>()

    for (i in 0 until Config.TOTAL_QUARTOS) {
        if (!sistema.quartosOcupados[i]) {
            livres.add(i + 1)
        }
    }

    if (livres.isEmpty()) {
        println("Não há quartos livres.")
    } else {
        println("Quartos livres: ${livres.joinToString(", ")}")
    }
}

fun mostrarMapaQuartos(sistema: Sistema) {
    println()
    println("Mapa de quartos:")

    for (linha in 0 until 4) {
        for (coluna in 0 until 5) {
            val numero = linha * 5 + coluna + 1
            val status = if (sistema.quartosOcupados[numero - 1]) 'O' else 'L'
            print("${numero.toString().padStart(2, '0')}:$status  ")
        }
        println()
    }
}

// ==================== HÓSPEDES ====================

fun cadastroHospedes(sistema: Sistema) {
    while (true) {
        println()
        println("[Cadastro de Hóspedes]")
        println("1. Cadastrar")
        println("2. Pesquisar por nome exato")
        println("3. Pesquisar por prefixo")
        println("4. Listar ordenado (A-Z)")
        println("5. Atualizar cadastro")
        println("6. Remover cadastro")
        println("7. Voltar")
        print("Opção: ")

        when (readlnOrNull()?.trim()) {
            "1" -> cadastrarHospede(sistema)
            "2" -> pesquisarExato(sistema)
            "3" -> pesquisarPrefixo(sistema)
            "4" -> listarHospedes(sistema)
            "5" -> atualizarHospede(sistema)
            "6" -> removerHospede(sistema)
            "7" -> return
            else -> erroOpcao()
        }
    }
}

fun cadastrarHospede(sistema: Sistema) {
    if (sistema.hospedes.size >= Config.MAX_HOSPEDES) {
        println("Máximo de cadastros atingido")
        return
    }

    print("Nome do hóspede: ")
    val nome = readlnOrNull()?.trim().orEmpty()

    if (nome.isBlank()) {
        println("Hóspede não encontrado")
        return
    }

    if (sistema.hospedes.any { it.nome == nome }) {
        println("Hóspede já cadastrado")
        return
    }

    sistema.hospedes.add(Hospede(nome, LocalDateTime.now()))
    println("Hóspede cadastrado com sucesso.")
}

fun pesquisarExato(sistema: Sistema) {
    print("Nome do hóspede: ")
    val nome = readlnOrNull()?.trim().orEmpty()
    val encontrado = sistema.hospedes.find { it.nome == nome }

    if (encontrado != null) {
        println("Hóspede ${encontrado.nome} foi encontrado")
    } else {
        println("Hóspede não encontrado")
    }
}

fun pesquisarPrefixo(sistema: Sistema) {
    print("Prefixo: ")
    val prefixo = readlnOrNull()?.trim().orEmpty()

    val resultados = sistema.hospedes
        .filter { it.nome.startsWith(prefixo) }
        .sortedBy { it.nome.lowercase() }

    if (resultados.isEmpty()) {
        println("Hóspede não encontrado")
        return
    }

    println("Resultados:")
    resultados.forEach { println(it.nome) }
}

fun listarHospedes(sistema: Sistema): List<Hospede> {
    val ordenados = sistema.hospedes.sortedBy { it.nome.lowercase() }

    if (ordenados.isEmpty()) {
        println("Nenhum hóspede cadastrado.")
        return ordenados
    }

    println("Hóspedes:")
    ordenados.forEachIndexed { indice, hospede ->
        println("[${indice + 1}] ${hospede.nome} - ${formatarDataHora(hospede.dataCadastro)}")
    }

    return ordenados
}

fun atualizarHospede(sistema: Sistema) {
    val ordenados = listarHospedes(sistema)
    if (ordenados.isEmpty()) return

    val indice = lerInteiro("Índice do hóspede que deseja atualizar: ")
    if (indice == null || indice !in 1..ordenados.size) {
        erroOpcao()
        return
    }

    val hospede = ordenados[indice - 1]
    print("Novo nome: ")
    val novoNome = readlnOrNull()?.trim().orEmpty()

    if (novoNome.isBlank()) {
        println("Hóspede não encontrado")
        return
    }

    val duplicado = sistema.hospedes.any { it !== hospede && it.nome == novoNome }
    if (duplicado) {
        println("Hóspede já cadastrado")
        return
    }

    hospede.nome = novoNome
    println("Operação realizada com sucesso")
}

fun removerHospede(sistema: Sistema) {
    val ordenados = listarHospedes(sistema)
    if (ordenados.isEmpty()) return

    val indice = lerInteiro("Índice do hóspede que deseja remover: ")
    if (indice == null || indice !in 1..ordenados.size) {
        erroOpcao()
        return
    }

    val hospede = ordenados[indice - 1]
    sistema.hospedes.remove(hospede)
    println("Operação realizada com sucesso")
}

// ==================== EVENTOS ====================

fun eventos(sistema: Sistema) {
    println()
    println("[Eventos]")

    val convidados = lerInteiro("Convidados: ")

    if (convidados == null || convidados < 0 || convidados > Config.CAPACIDADE_COLORADO) {
        println("Número de convidados inválido")
        return
    }

    val (auditorio, extras) = selecionarAuditorio(convidados)

    println("Auditório selecionado: $auditorio${if (extras > 0) " ($extras cadeiras adicionais)" else ""}")
    println()

    print("Dia: ")
    val dia = readlnOrNull()?.trim().orEmpty()

    val horaInicio = lerInteiro("Hora inicial: ")
    val duracao = lerInteiro("Duração: ")

    if (dia.isBlank() || horaInicio == null || duracao == null || duracao !in 1..12 || !diaValido(dia)) {
        println("Valor inválido.")
        return
    }

    val horaFim = horaInicio + duracao

    if (!horarioDisponivel(dia, horaInicio, horaFim)) {
        println("Auditório indisponível para o horário informado.")
        return
    }

    print("Empresa: ")
    val empresa = readlnOrNull()?.trim().orEmpty()

    if (empresa.isBlank()) {
        println("Valor inválido.")
        return
    }

    println("Auditório reservado para $empresa: $dia às ${horaInicio}hs")

    val garconsBase = ceil(convidados / 12.0).toInt()
    val reforco = floor(duracao / 2.0).toInt()
    val garcons = garconsBase + reforco
    val custoGarcons = garcons * duracao * Config.VALOR_GARCOM_HORA

    val cafeLitros = convidados * Config.LITROS_CAFE_POR_PESSOA
    val aguaLitros = convidados * Config.LITROS_AGUA_POR_PESSOA
    val salgados = convidados * Config.SALGADOS_POR_PESSOA

    val custoCafe = cafeLitros * Config.VALOR_CAFE_LITRO
    val custoAgua = aguaLitros * Config.VALOR_AGUA_LITRO
    val custoSalgados = salgados / 100.0 * Config.VALOR_SALGADOS_CENTO
    val custoBuffet = custoCafe + custoAgua + custoSalgados
    val total = custoGarcons + custoBuffet

    println()
    println("Garçons necessários: $garcons")
    println("Custo com garçons: ${formatarMoeda(custoGarcons)}")
    println()
    println("Buffet:")
    println("Café: ${formatarLitros(cafeLitros)} L")
    println("Água: ${formatarLitros(aguaLitros)} L")
    println("Salgados: $salgados un")
    println("Custo buffet: ${formatarMoeda(custoBuffet)}")
    println()
    println("Relatório técnico:")
    println("Auditório: $auditorio")
    println("Empresa: $empresa")
    println("Data: ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
    println("Hora início: ${horaInicio}h")
    println("Hora fim: ${horaFim}h")
    println("Convidados: $convidados")
    println("Garçons: $garcons")
    println("Duração: $duracao hora(s)")
    println("Custo garçons: ${formatarMoeda(custoGarcons)}")
    println("Custo buffet: ${formatarMoeda(custoBuffet)}")
    println("Total geral: ${formatarMoeda(total)}")

    val confirmacao = confirmar("Confirmar reserva? (S/N): ")

    if (confirmacao == null) {
        erroOpcao()
        return
    }

    if (!confirmacao) {
        println("Reserva não efetuada.")
        return
    }

    sistema.eventos.add(
        Evento(
            auditorio = auditorio,
            empresa = empresa,
            dia = dia,
            data = LocalDate.now(),
            horaInicio = horaInicio,
            horaFim = horaFim,
            convidados = convidados,
            duracao = duracao,
            garcons = garcons,
            custoGarcons = custoGarcons,
            custoBuffet = custoBuffet,
            total = total
        )
    )

    println("Reserva efetuada com sucesso.")
}

fun selecionarAuditorio(convidados: Int): Pair<String, Int> {
    return if (convidados <= Config.CAPACIDADE_LARANJA) {
        Pair("Laranja", 0)
    } else if (convidados <= Config.CAPACIDADE_LARANJA + Config.EXTRAS_LARANJA) {
        Pair("Laranja", convidados - Config.CAPACIDADE_LARANJA)
    } else {
        Pair("Colorado", 0)
    }
}

fun diaValido(dia: String): Boolean {
    return dia.lowercase() in setOf(
        "segunda", "terca", "quarta", "quinta", "sexta", "sabado", "domingo"
    )
}

fun horarioDisponivel(dia: String, inicio: Int, fim: Int): Boolean {
    if (inicio < 0 || inicio > 23 || fim <= inicio || fim > 23) {
        return false
    }

    return when (dia.lowercase()) {
        "segunda", "terca", "quarta", "quinta", "sexta" -> inicio >= 7 && fim <= 23
        "sabado", "domingo" -> inicio >= 7 && fim <= 15
        else -> false
    }
}

fun formatarLitros(valor: Double): String {
    return String.format(Locale("pt", "BR"), "%.1f", valor)
}

// ==================== AR-CONDICIONADO ====================

fun arCondicionado(sistema: Sistema, nomeUsuario: String) {
    println()
    println("[Ar-Condicionado]")
    sistema.orcamentos.clear()

    while (true) {
        print("Empresa: ")
        val empresa = readlnOrNull()?.trim().orEmpty()

        val valorAparelho = lerDouble("Valor por aparelho: ")
        val quantidade = lerInteiro("Quantidade de aparelhos: ")
        val percentualDesconto = lerDouble("Desconto (%): ")
        val minimoDesconto = lerInteiro("Mínimo para desconto: ")
        val deslocamento = lerDouble("Deslocamento: ")

        if (
            empresa.isBlank() ||
            valorAparelho == null || valorAparelho < 0 ||
            quantidade == null || quantidade <= 0 ||
            percentualDesconto == null || percentualDesconto < 0 ||
            minimoDesconto == null || minimoDesconto < 0 ||
            deslocamento == null || deslocamento < 0
        ) {
            erroValor(nomeUsuario)
            return
        }

        val bruto = valorAparelho * quantidade
        val desconto = if (quantidade >= minimoDesconto) {
            bruto * percentualDesconto / 100.0
        } else {
            0.0
        }
        val total = bruto - desconto + deslocamento

        sistema.orcamentos.add(Orcamento(empresa, total))
        println("O serviço de $empresa custará ${formatarMoeda(total)}")

        print("Deseja informar novos dados, $nomeUsuario? (S/N): ")
        when (readlnOrNull()?.trim()?.uppercase()) {
            "S" -> continue
            "N" -> break
            else -> {
                erroOpcao()
                return
            }
        }
    }

    if (sistema.orcamentos.size < 2) {
        println("É necessário informar ao menos duas empresas.")
        return
    }

    val menor = sistema.orcamentos.minByOrNull { it.total }!!
    val maior = sistema.orcamentos.maxByOrNull { it.total }!!
    val diferencaPercentual = if (menor.total == 0.0) {
        0.0
    } else {
        ((maior.total - menor.total) / menor.total) * 100.0
    }

    println("Menor orçamento: ${menor.empresa} por ${formatarMoeda(menor.total)}")
    println("Maior orçamento: ${maior.empresa} por ${formatarMoeda(maior.total)}")
    println("Diferença percentual: ${String.format(Locale("pt", "BR"), "%.2f", diferencaPercentual)}%")
}

// ==================== ABASTECIMENTO ====================

fun abastecimento(sistema: Sistema, nomeUsuario: String) {
    println()
    println("[Abastecimento]")
    sistema.abastecimentos.clear()

    val postos = listOf("Wayne Oil", "Stark Petrol")

    for (posto in postos) {
        val alcool = lerDouble("$posto - Preço do álcool: ")
        val gasolina = lerDouble("$posto - Preço da gasolina: ")

        if (alcool == null || gasolina == null || alcool < 0 || gasolina < 0) {
            erroValor(nomeUsuario)
            return
        }

        val etanolVantajoso = alcool <= gasolina * 0.70
        val combustivel = if (etanolVantajoso) "Álcool" else "Gasolina"
        val preco = if (etanolVantajoso) alcool else gasolina
        val total = preco * Config.LITROS_TANQUE

        sistema.abastecimentos.add(
            OpcaoAbastecimento(posto, combustivel, preco, total)
        )

        println("$posto: melhor opção = $combustivel | Total (42L) = ${formatarMoeda(total)}")
    }

    val ranking = sistema.abastecimentos.sortedBy { it.total }

    println()
    println("Ranking de menor para maior custo:")
    ranking.forEachIndexed { indice, opcao ->
        println("${indice + 1}. ${opcao.posto} - ${opcao.combustivel} - ${formatarMoeda(opcao.total)}")
    }

    val melhor = ranking.first()
    println()
    println("$nomeUsuario, é mais barato abastecer com ${melhor.combustivel.lowercase(Locale("pt", "BR"))} no posto ${melhor.posto}.")
}

// ==================== RELATÓRIOS ====================

fun relatorios(sistema: Sistema) {
    println()
    println("[Relatórios Operacionais]")

    val reservasConfirmadas = sistema.reservas.size
    val quartosOcupados = sistema.quartosOcupados.count { it }
    val taxaOcupacao = quartosOcupados.toDouble() / Config.TOTAL_QUARTOS * 100.0
    val hospedesCadastrados = sistema.hospedes.size
    val eventosConfirmados = sistema.eventos.size
    val receitaHospedagem = sistema.reservas.sumOf { it.total }
    val receitaEventos = sistema.eventos.sumOf { it.total }
    val receitaTotal = receitaHospedagem + receitaEventos

    println("----------------------------------------------")
    println("Reservas de quartos confirmadas : $reservasConfirmadas")
    println("Quartos ocupados                : $quartosOcupados/${Config.TOTAL_QUARTOS}")
    println("Taxa de ocupação                : ${String.format(Locale("pt", "BR"), "%.2f", taxaOcupacao)}%")
    println("Hóspedes cadastrados            : $hospedesCadastrados")
    println("Eventos confirmados             : $eventosConfirmados")
    println("Receita de hospedagem           : ${formatarMoeda(receitaHospedagem)}")
    println("Receita de eventos              : ${formatarMoeda(receitaEventos)}")
    println("Receita total                   : ${formatarMoeda(receitaTotal)}")
    println("----------------------------------------------")
}
