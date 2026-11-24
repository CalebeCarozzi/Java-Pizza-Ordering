# Java-Pizza-Ordering

Sistema desktop de pedidos de uma pizzaria que vende pizzas por centímetro quadrado. Projeto de Linguagem de Programação Orientada a Objetos II (LPOO2), desenvolvido a partir do trabalho de LPOO1, com interface Java Swing, persistência em MySQL por JDBC, padrão DAO e organização MVC.

## Integrantes

Em ordem alfabética:

| Nome | GRR |
| --- | --- |
| Calebe Rodrigues Carozzi | GRR20252155 |
| Dancleve Rafael Peres de Oliveira Nascimento | GRR20250960 |
| Rafael Blaskowski Demeterko | GRR20255502 |
| Rafael Maldonado Caetano | GRR20251258 |

## Funcionalidades

- Cadastro, edição, exclusão e consulta de clientes, com filtros por sobrenome e telefone.
- Cadastro de sabores classificados como simples, especial ou premium.
- Configuração do preço por cm² de cada tipo de pizza.
- Pedidos com vários itens, quantidades e até dois sabores por pizza.
- Pizzas quadradas, circulares ou triangulares equiláteras, com entrada por dimensão ou área.
- Cálculo de preços e armazenamento do preço unitário dos itens e do total do pedido.
- Consulta de pedidos e alteração dos estados: aberto, a caminho e entregue.
- Validações de entrada e tratamento de erros de persistência.

## Tecnologias e organização

- Java e Swing; ambiente de desenvolvimento configurado com JDK 25.
- MySQL 8.0.46 utilizado no desenvolvimento.
- JDBC com o driver disponível em `lib/`.
- SQL parametrizado, fechamento de recursos e transações para operações relacionadas.

```text
config/           Configuração da conexão com o banco
docs/             Arquivos de documentação
lib/              Driver JDBC do MySQL
sql/schema.sql    Estrutura do banco e preços iniciais
src/app/          Inicialização da aplicação (app.Main)
src/connection/   Fábrica de conexões JDBC
src/controller/   Coordenação das ações das telas
src/dao/          Interfaces DAO e fábrica de DAOs
src/dao/sql/      Implementações de persistência com SQL
src/exception/    Exceções da aplicação
src/model/        Entidades e cálculos do domínio
src/service/      Regras de negócio e validações
src/view/         Interface gráfica Swing
```

O fluxo principal é `View → Controller → Service → DAO → MySQL`. A classe abstrata `Forma` e suas subclasses `Quadrado`, `Circulo` e `Triangulo` implementam o cálculo polimórfico da área.

## Preparação do banco

1. Instale e inicie o MySQL Server. O Workbench pode ser utilizado para executar o SQL.
2. Abra `sql/schema.sql` no Workbench e execute o script completo, com um usuário que possa criar o banco e as tabelas.
3. O script cria o banco `PizzaOrdering`, as cinco tabelas e os preços iniciais:

| Tipo | Preço por cm² |
| --- | --- |
| Simples | R$ 0,05 |
| Especial | R$ 0,07 |
| Premium | R$ 0,10 |

O script pode ser executado novamente sem apagar registros ou substituir preços já configurados. Ele cria estruturas ausentes, mas não modifica a estrutura de tabelas preexistentes; não é um script de migração de versões antigas.

## Configuração da conexão

Copie `config/db.properties.example` para `config/db.properties` e ajuste os valores para a sua instalação:

```properties
db.url=jdbc:mysql://localhost:3306/PizzaOrdering
db.user=SEU_USUARIO
db.password=SUA_SENHA
```

Não coloque aspas nos valores. O arquivo local `config/db.properties` está no `.gitignore` para não publicar credenciais. O arquivo de exemplo deve permanecer sem senhas reais.

**Execute o programa com a raiz do projeto como diretório de trabalho**, pois a aplicação procura `config/db.properties` a partir desse diretório. O MySQL precisa estar em execução.

## Executar pela IDE

1. Abra ou importe o projeto Java na IDE e selecione o JDK 25.
2. Configure `src/` como pasta de código-fonte e a codificação como UTF-8.
3. Adicione o JAR de `lib/` às bibliotecas/classpath do projeto.
4. Prepare o banco e a configuração conforme as seções anteriores.
5. Execute a classe `app.Main`, usando a raiz do projeto como diretório de trabalho.

Para a entrega em Eclipse ou NetBeans, importe os fontes como projeto Java e configure o JDK e a biblioteca JDBC conforme indicado acima. A configuração do IntelliJ não é necessária para executar o código.

## Compilar e executar pelo terminal

No PowerShell do Windows, dentro da raiz do projeto, com `java`, `javac` e `jar` do JDK 25 disponíveis no PATH:

```powershell
New-Item -ItemType Directory -Force build/classes | Out-Null
$fontes = @(Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
javac -encoding UTF-8 -cp "lib/*" -d build/classes $fontes
java -cp "build/classes;lib/*" app.Main
```

Para conferir a conexão sem abrir a interface:

```powershell
java -cp "build/classes;lib/*" connection.TesteConexao
```

O resultado esperado informa a conexão realizada e o banco `PizzaOrdering`.

## Gerar um JAR executável

Após compilar com sucesso, execute na raiz do projeto:

```powershell
New-Item -ItemType Directory -Force build/lib | Out-Null
New-Item -ItemType Directory -Force build/config | Out-Null
Copy-Item lib/*.jar build/lib/
Copy-Item config/db.properties.example build/config/db.properties.example
$driver = Get-ChildItem lib -Filter 'mysql-connector-j-*.jar' | Select-Object -First 1
if ($null -eq $driver) { throw 'Driver JDBC não encontrado em lib/.' }
$manifesto = "Manifest-Version: 1.0`nMain-Class: app.Main`nClass-Path: lib/$($driver.Name)`n`n"
Set-Content -Path build/MANIFEST.MF -Value $manifesto -Encoding ascii
jar --create --file build/PizzaOrdering.jar --manifest build/MANIFEST.MF -C build/classes .
```

Para executar o pacote, copie `build/config/db.properties.example` para `build/config/db.properties`, preencha as credenciais e utilize:

```powershell
Set-Location build
java -jar PizzaOrdering.jar
```

Distribua `PizzaOrdering.jar` junto das pastas `lib/` e `config/`, do script `sql/schema.sql` e destas instruções. A máquina de destino precisa do Java 25 ou compatível e de acesso a um MySQL configurado. O JAR não inclui o servidor nem os dados do banco.

## Regras de uso

1. Cadastre um cliente e pelo menos um sabor.
2. Confira os preços por cm².
3. Abra a tela de pedidos, busque o cliente por telefone e selecione-o.
4. Configure a forma, a medida ou área, os sabores e a quantidade; adicione os itens.
5. Consulte o total e finalize a edição. Os itens já são persistidos durante as operações; finalizar mantém o pedido aberto.
6. Utilize a tela de status para alterar o pedido para a caminho ou entregue.

Somente pedidos abertos podem ter seus itens editados. O sistema permite um pedido aberto por cliente. Excluir um cliente também exclui seus pedidos e itens. Sabores utilizados em pedidos não podem ser excluídos enquanto houver referências a eles.

| Forma/modo de entrada | Intervalo permitido |
| --- | --- |
| Quadrado por lado | 10 a 40 cm |
| Círculo por raio | 7 a 23 cm |
| Triângulo equilátero por lado | 20 a 60 cm |
| Entrada por área | 100 a 1600 cm² |

Com dois sabores, o preço da pizza utiliza a média aritmética dos preços por cm² dos respectivos tipos, multiplicada pela área. Cada item guarda seu preço unitário, de modo que alterações posteriores na tabela de preços não reajustam automaticamente os itens já salvos. Editar um item recalcula seu preço pelas condições atuais.

## Verificação rápida

- Cadastre, edite e filtre um cliente.
- Cadastre sabores e monte uma pizza quadrada de lado 20 cm: com preço simples de R$ 0,05/cm², o valor unitário deve ser R$ 20,00.
- Confira pedidos com dois sabores, edição de quantidades e remoção de itens.
- Altere o estado de um pedido e confira o bloqueio da edição de itens fora do estado aberto.
- Feche e abra o programa para verificar a persistência dos dados.

O teste de integração abaixo cria registros próprios, verifica inclusão, edição, remoção, totais e estado de um pedido, e remove os registros de teste ao terminar:

```powershell
java -cp "build/classes;lib/*" dao.TestePedidoDAO
```

Execute esse comando a partir da raiz do projeto, após compilar e configurar a conexão.
