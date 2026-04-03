# Projeto de Extensao

Este projeto tem como objetivo compor o programa de Extensão Curricular, com integração das matérias:
* Admnistração de Banco de Dados;
* Gestão da Qualidade Ambiental;
* Linguagem de Programação II;
* Estatística.

O seguinte trabalho consiste em uma Aplicacao Java desktop (Swing) que consulta dados de monitoramento de represas em um banco MySQL, com objetivo de analisar o volume nas represas, o impacto ambiental e social, e apresentar relatórios estatiscos sobre o tema.

## O que o projeto faz

- Exibe uma mensagem inicial (`Principal`).
- Consulta o banco de dados com `JOIN` entre `MONITORAMENTO` e `REPRESA` (`DataBase`).
- Mostra os dados em uma `JTable` dentro de um `JOptionPane`.

## Estrutura do projeto

```text
src/projetoextensao/
  Principal.java   -> ponto de entrada da aplicacao
  Conexao.java     -> configuracao de conexao JDBC com MySQL
  DataBase.java    -> execucao do SQL e montagem da tabela na interface
bin/               -> classes compiladas
```

## Requisitos

- JDK 10+ (recomendado usar o mesmo perfil do projeto: JavaSE-24).
- MySQL Server rodando localmente.
- Banco `monitoramento_represa` criado e com dados.
- Driver JDBC do MySQL.

## Driver necessario

O projeto usa JDBC via `DriverManager`, entao precisa do conector MySQL no classpath:

- `mysql-connector-j-9.6.0.jar` (ou versao compativel mais recente)
- Download oficial: https://dev.mysql.com/downloads/connector/j/

Sugestao: criar uma pasta `lib/` na raiz do projeto e colocar o JAR nela.

## Configuracao do banco

Em `src/projetoextensao/Conexao.java`:

- URL: `jdbc:mysql://localhost:3306/monitoramento_represa`
- Usuario: `root`
- Senha: `9651luke`

Se necessario, ajuste para o seu ambiente.

### Tabelas esperadas pela consulta

A query em `DataBase.java` espera:

- `MONITORAMENTO` com colunas:
  - `id`
  - `id_represa`
  - `data`
  - `chuva_mm`
  - `volume_util_percent`
  - `chuva_acumulada_mes_mm`
- `REPRESA` com colunas:
  - `id`
  - `nome`

## Como executar no Eclipse

1. Importe o projeto Java existente.
2. Configure o JDK do projeto (JavaSE-24 ou equivalente).
3. Adicione o driver:
   - `Project > Properties > Java Build Path > Libraries > Add External JARs`.
4. Execute `projetoextensao.Principal`.

## Como executar no terminal (PowerShell)

### Compilar

```powershell
javac -encoding UTF-8 -cp ".;lib\mysql-connector-j-9.6.0.jar" -d bin src\projetoextensao\*.java
```

### Executar

```powershell
java -cp "bin;lib\mysql-connector-j-9.6.0.jar" projetoextensao.Principal
```

## Observacoes importantes

- Sem o driver JDBC no classpath, a conexao com MySQL falha.
- Se o banco nao estiver em `localhost:3306`, ajuste a URL em `Conexao.java`.
- Usuario e senha estao no codigo-fonte; para producao, prefira variaveis de ambiente ou arquivo de configuracao.
