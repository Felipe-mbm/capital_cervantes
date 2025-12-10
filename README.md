# Capital Cervantes 🏦

O **Capital Cervantes** é uma aplicação bancária desenvolvida em **Java 21** que roda via linha de comando (CLI). O projeto simula as operações essenciais de um banco, focando em boas práticas de arquitetura, separação de responsabilidades e persistência de dados com MySQL.

## 🚀 Tecnologias Utilizadas

O projeto utiliza as seguintes tecnologias e bibliotecas:

- **Java 21**: Linguagem base do projeto.
- **Maven**: Gerenciador de dependências e build.
- **MySQL**: Banco de dados relacional para persistência das contas.
- **JDBC (Java Database Connectivity)**: Para comunicação direta com o banco de dados.
- **HikariCP**: Pool de conexões para otimização de performance no acesso ao banco.

## ⚙️ Funcionalidades

A aplicação oferece um menu interativo com as seguintes opções:

1. **Listar Contas**: Visualiza todas as contas ativas ou busca uma conta específica pelo número.
2. **Abrir Conta**: Cria uma nova conta bancária solicitando dados do cliente (Nome, CPF, Email).
3. **Encerrar Conta**: Realiza o fechamento da conta (exclusão lógica), permitido apenas se o saldo for zero.
4. **Consultar Saldo**: Verifica o saldo atual de uma conta.
5. **Saque**: Retira valores da conta (com validação de saldo suficiente).
6. **Depósito**: Adiciona valores à conta.
7. **Transferência**: Transfere valores entre contas (origem -> destino) de forma atômica.

## 🗂 Estrutura do Projeto

A arquitetura está dividida em camadas para facilitar a manutenção:

- `src/main/java/CapitalCervantesApplication.java`: Ponto de entrada (Main) e interface com o usuário.
- `domain.conta`: Contém a lógica de negócios (`ContaService`), acesso a dados (`ContaDAO`) e a entidade (`Conta`).
- `domain.cliente`: Contém os dados do cliente (`Cliente`, `DadosCadastroCliente`).
- `domain.ConnectionFactory`: Gerencia a conexão com o banco de dados via HikariCP.

## 📝 Pré-requisitos e Configuração

### 1. Banco de Dados

Antes de executar a aplicação, é necessário criar o banco de dados e a tabela no MySQL. Execute o script abaixo no seu cliente MySQL:

```sql
CREATE DATABASE capital_cervantes;

USE capital_cervantes;

CREATE TABLE conta (
    numero INT PRIMARY KEY,
    saldo DECIMAL(10,2) NOT NULL,
    cliente_nome VARCHAR(255) NOT NULL,
    cliente_cpf VARCHAR(14) NOT NULL,
    cliente_email VARCHAR(255) NOT NULL,
    esta_ativa BOOLEAN DEFAULT TRUE
);
```

> **Nota:** A tabela inclui a coluna `esta_ativa` para suportar a exclusão lógica implementada no DAO.

### 2. Configuração de Conexão

Verifique o arquivo `src/main/java/domain/ConnectionFactory.java` para garantir que as credenciais do banco correspondam ao seu ambiente local.

Atualmente, o código está configurado da seguinte forma:

```java
config.setJdbcUrl("jdbc:mysql://localhost:3306/capital_cervantes");
config.setUsername("root");
config.setPassword(""); // Insira sua senha do MySQL aqui se necessário
```

Se o seu MySQL possui senha, altere o campo `setPassword`.

### 3. Compilação e Execução

Com o Maven instalado, navegue até a raiz do projeto e execute:

```bash
# Para compilar e baixar as dependências
mvn clean install

# Para rodar a aplicação (ou execute pela sua IDE preferida)
mvn exec:java -Dexec.mainClass="CapitalCervantesApplication"
```

## 🛡️ Regras de Negócio

- **Saldo Negativo:** Não são permitidos saques que deixem o saldo negativo.
- **Encerramento:** Contas com saldo diferente de zero não podem ser encerradas.
- **Exclusão Lógica:** Ao "deletar" uma conta, o sistema apenas a marca como inativa no banco de dados, preservando o histórico.

