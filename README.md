# Hotel Terabithia

Aplicação de console em Kotlin que simula rotinas operacionais de um hotel. O projeto reúne reservas, cadastro de hóspedes, organização de eventos e cálculos de custos em um menu interativo.

## Objetivo

Praticar lógica de programação, organização em funções, classes de dados, coleções e validação de regras de negócio.

## Funcionalidades

- **Reservas:** escolha de quarto, tipo de acomodação, cálculo de diárias e taxa de serviço, confirmação e controle de ocupação.
- **Hóspedes:** cadastro, pesquisa por nome ou prefixo, listagem, atualização e remoção.
- **Eventos:** seleção de auditório e cálculo de garçons, buffet e custo total.
- **Climatização:** cálculo e comparação de orçamentos.
- **Abastecimento:** comparação de opções de combustível e posto.
- **Relatórios:** consulta aos dados operacionais armazenados durante a execução.

## Tecnologias

- Kotlin para JVM.
- Bibliotecas padrão de Kotlin e Java, incluindo `java.time`.
- Entrada e saída pelo terminal.

## Como executar

Tenha Git, um JDK e o compilador Kotlin para JVM disponíveis no terminal. O [guia oficial do Kotlin](https://kotlinlang.org/docs/command-line.html) explica a instalação do compilador.

```bash
git clone https://github.com/gabiecarvalh/hotel-terabithia.git
cd hotel-terabithia
kotlinc hotel.terabithia.kt -include-runtime -d hotel-terabithia.jar
java -jar hotel-terabithia.jar
```

Na abertura, informe um nome. A senha fixa do exercício é `2678`.

O menu oferece reservas de quartos, cadastro de hóspedes, eventos, ar-condicionado, abastecimento e relatórios operacionais.

## Exemplo de uso

1. Abra **Reservas de Quartos**.
2. Informe diária de `100` e quantidade de `2` diárias.
3. Informe o nome de um hóspede fictício.
4. Escolha o tipo `S` e um quarto livre.
5. Confira subtotal de R$ 200,00 e total de R$ 220,00, incluindo a taxa de serviço de 10%.
6. Confirme a reserva e confira o mapa de ocupação.

Esse é um cenário esperado a partir da regra de cálculo do código e serve como roteiro para uma demonstração.

## Organização atual

O código está em `hotel.terabithia.kt` e reúne:

- `Config`: constantes usadas no exercício.
- Classes de dados como `Reserva`, `Hospede` e `Evento`.
- `Sistema`: coleções e estado da aplicação.
- Funções para os menus, operações e validação de entradas.

## Verificação manual

Execute os cenários abaixo e marque os resultados depois de conferir o comportamento:

- [ ] Confirmar uma reserva com valores válidos e conferir o total calculado.
- [ ] Tentar reservar um quarto ocupado e verificar a indicação de indisponibilidade.
- [ ] Informar zero, valor negativo ou texto no campo de diárias e verificar a validação.
- [ ] Cadastrar e pesquisar um hóspede fictício.
- [ ] Tentar cadastrar novamente o mesmo nome e verificar a validação.
- [ ] Cancelar uma reserva antes da confirmação e conferir que o quarto permanece livre.
- [ ] Consultar os relatórios após realizar operações.

Essa lista é um roteiro de execução manual. A versão atual não contém uma suíte automatizada de testes.

## Limitações da versão de estudo

- Os dados ficam em memória durante a execução.
- O acesso utiliza uma senha fixa definida no código.
- O código está concentrado em um único arquivo.

## Próximas melhorias

- [ ] Separar modelos, operações e interface de console em arquivos próprios.
- [ ] Adicionar um projeto Gradle para padronizar execução e testes.
- [ ] Criar testes automatizados para cálculos e regras de reservas.
- [ ] Implementar persistência de dados.

[Perfil de Gabriela de Carvalho](https://github.com/gabiecarvalh)
