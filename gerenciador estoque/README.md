# Gerenciador de Estoque

Aplicacao Java de linha de comando para cadastro de produtos e categorias, controle de estoque, vendas e analises.

## Requisitos

- Java 11 ou superior
- MySQL
- Driver JDBC MySQL incluído em `lib/`

## Banco de dados

Execute [`sql/schema.sql`](sql/schema.sql) no MySQL para criar o banco e as tabelas. O script cria somente a estrutura; ele nao inclui usuarios ou dados de exemplo.

Configure estas variaveis de ambiente antes de iniciar a aplicacao:

- `DB_URL`: URL JDBC, por exemplo `jdbc:mysql://localhost:3306/estoque_loja?useSSL=false&serverTimezone=America/Sao_Paulo`
- `DB_USER`: usuario do MySQL
- `DB_PASSWORD`: senha do MySQL

Nao coloque credenciais reais no codigo nem as envie ao GitHub.

## Compilar e executar no PowerShell

Na pasta raiz do projeto, configure as variaveis para a sessao atual:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/estoque_loja?useSSL=false&serverTimezone=America/Sao_Paulo"
$env:DB_USER = "seu_usuario_mysql"
$env:DB_PASSWORD = "sua_senha_mysql"
```

Compile e execute:

```powershell
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
New-Item -ItemType Directory -Force out | Out-Null
javac -cp "lib\mysql-connector-j-26.7.0.jar" -d out MainTeste.java $sources
java -cp "out;lib\mysql-connector-j-26.7.0.jar" MainTeste
```

## Primeiro acesso

O programa exige que já exista um usuário com cargo `GERENTE`, mas ainda não oferece um fluxo para criar o primeiro usuário. Crie essa conta por um procedimento administrativo seguro antes de fazer login; nao use credenciais de exemplo em um banco compartilhado ou publicado.