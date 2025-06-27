# 📄 Documentação Técnica — "Ganho de Capital CLI"

## Visão Geral
  
Solução para o cálculo de impostos sobre ganho de capital em operações de compra e venda de ativos financeiros. Trata-se de uma aplicação CLI (Command Line Interface) que lê as transações de compra e venda, processa os dados e retorna os impostos aplicados a cada transação.
  
## Formatos de Entrada e Saída:
  
### Entrada
O programa espera como entrada uma lista de objetos em formato JSON com os seguintes campos:  
- **operation**: indica se trata-se de uma transação de compra ou venda | valores possíveis: "buy", "sell";
- **unit-cost**: preço operado do ativo | valores >= 0.0);
- **quantity**: quantidade de ativos negociados | valores >= 0);
  
*p.s.*: É possível enviar mais de uma lista de operações, separadas por linhas. O programa aguarda uma linha vazia para saber que trata-se do fim do arquivo. Cada linha será processada individualmente (checar *Regras de Negócio*)
  
*e.g.*
```
[{"operation":"buy", "unit-cost":10.00, "quantity": 100}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}]
[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":20.00, "quantity": 5000}, {"operation":"sell", "unit-cost":5.00, "quantity": 5000}]


```
  
### Saída
O programa retorna como saída uma lista de objetos em formato JSON com o seguinte campo:  
- **tax**: informa a quantidade de imposto a ser pago na respectiva transação de compra ou venda de ativos.
  
*e.g.*
```
[{"tax": 0.00}, {"tax": 0.00}, {"tax": 0.00}]
[{"tax": 0.00}, {"tax": 10000.00}, {"tax": 0.00}]
```
  
  
## Regras de Negócio:

Cada linha de transações recebida pelo programa será processada individualmente, sem que as informações enviadas em uma linhas interfiram no resultado das outras.
  
Assume-se que cada linha de transações contém as mesmas na ordem que ocorreram. Essa hipótese é essencial no resultado pois ela define o preço médio ponderado das compras quando uma venda é realizada.

As regras de cálculo estão resumidas a seguir:
- Não há imposto em operações de compra, apenas nas de venda;
- O percentual de imposto pago é de 20% sobre o lucro obtido na operação.
- Para determinar se a operação resultou em lucro ou prejuízo, o programa calcula o preço médio ponderado das compras utilizando a fórmula, então quando você compra ações você deve recalcular o preço médio ponderado utilizando essa fórmula:

$$
\text{nova\_media\_ponderada} =
\frac{
(\text{qtd\_atual} \times \text{media\_atual}) + (\text{qtd\_comprada} \times \text{valor\_compra})
}{
\text{qtd\_atual} + \text{qtd\_comprada}
}
$$

- Lucros ocorrem quando você vende ativos a um valor maior do que o preço médio ponderado de compra;
- Prejuízos ocorrem quando você vende ativos a um valor menor do que o preço médio ponderado de compra;
- De acordo com as normativas de declaração de imposto atuais, o programa utiliza o prejuízo de transações passado para deduzir o lucro de transações futuras, de maneira vantajosa ao declarante, até que todo o prejuízo seja deduzido;
- Não há nenhum imposto (e nem dedução a partir de prejuízo) se o valor total da transação (custo unitário da ação x quantidade) for menor ou igual a R$ 20.000,00. No entanto, o prejuízo em transações de valor total inferior podem ser acumulados para deduções futuras.

## Como utilizar:

Há duas maneiras de executar o programa: 
- através da geração da imagem (necessário possuir o Docker instalado na máquina);
- através da compilação do programa (necessário possuir o Maven versão 3+ e a JRE v.21+ instalada na máquina);


### Execução através da imagem:
Baixar o projeto (.zip ou clone), abrir o diretório raiz do programa e executar a seguinte linha de comando através de um terminal apropriado
```
docker build -t ganho-capital-cli .
```

Após a conclusão utilizar o arquivo case7.txt para testar o resultado do programa através da seguinte linha de comando:
```
docker run -i ganho-capital-cli < case7.txt
```

Depois de terminar a utilização, recomenda-se executar a linha de comando abaixo para remover containers parados:
```
docker rmi -f ganho-capital-cli
```

### Execução através da compilação do programa:
Baixar o projeto (.zip ou clone), abrir o diretório raiz do programa e executar a seguinte linha de comando através de um terminal apropriado
```
mvn clean package
```

Após a conclusão, você deve perceber que foi criado um diretório target na raiz do programa. Mover o arquivo case7.txt para este diretório e, dentro dele executar
```
java -jar ganho-capital-1.0.0.jar < case7.txt
```

**Observação**: seja qual for o método de execução utilizado, é possível alimentar o programa manualmente através do terminal, escrevendo manualmente o JSON de entrada. Para utilizá-lo desta maneira, basta remover o "< case7.txt" da linhas de comando.

## Decisões Técnicas e Arquiteturais

O desenvolvimento deste programa tomou decisões arquiteturais que priorizaram **Simplicidade** e a **Elegência**, facilitando manutenabilidade e extensão da solução.

### Modularização:

O programa utiliza de arquitetura de aplicação Ports and Adapters (Hexagonal), separando a camada de domínio da camada de interação com o usuário e segregando toda regra de negócio na camada de domínio.

### Inversão de dependência:

O desacoplamento entre as camadas de domínio (domain) e de interação com o usuário (port) é feito utilizando-se do princípio de inversão de dependência (DIP). A camada de alto nível (interação) deixa de depender da camada de baixo nível (domínio), e ambas passam a depender de uma abstração (Martin R.C., 2019). Assim, o programa torna-se facilmente extensível para proposição de novas soluções para o problema. 

### Princípio de Responsabilidade Única e Princípio Aberto Fechado:
As regras de conversão dos JSON são segregadas na camada de adapters, de modo a não se misturarem com as regras de negócio do domínio. Para este fim, o programa possui uma classe que aplica as extrações necessárias das Strings JSON apenas na medida do necessário para atender os requisitos, sendo assim uma classe aberta a extensão dos requisitos de negócio

Já o cálculo de preço médio usa dos padrões de Inversão de Dependência e Injeção da dependência. A solução não depende da implementação da calculadora de preço médio, e a implementação desta calculadora é injetada pela classe de mais alto nível (camada de interação) na camada de mais baixo nível (camada de solução). 

A definição do método de cálculo utilizado é feito através de uma classe configuração do programa, que fica na camada de infraestrutura.

Todas essas decisões arquiteturais tiveram por objetivo apresentar uma classe de implementação da solução mais limpa (pois não há responsabilidades nela além de atender os requisitos de negócio) e o programa mais aberto a extensões sem a necessidade de alterações nas estruturas que já atendem os requisitos (pois há desacoplamento das dependências) 

### Performance e utilização de memória

Baseado nos pilares de simplicidade de elegância, o programa foi desenvolvido visando baixo consumo de memória e alta velocidade de processamento. Para este fim, foram tomadas as seguintes decisões:
- Utilizou-se ao máximo variáveis de tipos primitivos com menor consumo de bytes, nos limites da razoabilidade dos requisitos funcionais (tipos double foram utilizados por atenderem a precisão de segunda casa decimal);
- Não foram adicionadas dependências externas no escopo de runtime do programa. Todo o processamento de JSON foi feito diretamente por análise Regex, evitando overhead de memória pelos tipos usados em toolkits como Gson ou Jackson, além de diminuir o tempo de build;
- Tipos primários de baixo custo char, short e boolean são utilizados na medida do possível;
- Tipos referência String e BigDecimal são utilizados estritamente quando necessários.

### Complexidade da solução

A solução entrega complexidade de tempo O(n), que é a mínima possível para a natureza do problema solucionado. Não é possível aplicar um paradigma divide-and-conquer neste caso, porque os subproblemas não poderiam ser resolvidos indpendentemente, uma vez que há dependência da ordem para o cálculo do imposto (Cormen T. H., 2022).

Ademais, a complexidade de memória também é O(n) pois o buffer de saída precisa ser proporcional ao buffer de entrada, e totalmetne acumulado antes de ser apresentado.


## Qualidade de código

O Suite de testes aplicados ao programa garante testes integrados, end-to-end, que fornecem à interface de interação 10 arquivos (src/test/resources/in) de diferentes cenários de cálculo de imposto de renda. Para cada um desses 10 arquivos é fornecido um gabarito (src/test/resources/out).

O Suite de testes captura o System.out de saída, e valida na medida que os seguintes critérios são atendidos:
- O número de linhas na saída da solução é igual ao número de linhas esperado no gabarito;
- Os valores das chave "tax" da solução e do gabarito são comparados 1 a 1, em respectiva ordem, e a diferença entre os valores na mesma ordem não devem ser maiores do que 0.01;
- O número de chaves-valor "tax" da solução é igual ao número de chaves-valor "tax" esperado em cada linha do gabarito.


## Referências:
MARTIN, Robert C. Arquitetura limpa: o guia do artesão para estrutura e design de software. Tradução de Starlin Alta Editora e Consultoria. Rio de Janeiro (RJ): Alta Books, 2019.
CORMEN, Thomas H.; LEISERSON, Charles Eric; RIVEST, Ronald L.; STEIN, Clifford. Introduction to algorithms. 4. ed. Cambridge, Massachusetts: The MIT Press, 2022.