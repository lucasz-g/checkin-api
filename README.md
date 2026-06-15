# CheckIn API: Documentação Completa

## 1. Visão Geral

A **CheckIn API** evoluiu ao longo da disciplina de **Arquitetura Orientada a Serviços (SOA)**. A primeira entrega consistia em uma aplicação RESTful para gerenciar check‑ins de tarefas e o histórico de evolução dos usuários. Nesta segunda entrega ampliamos o escopo adicionando **autenticação baseada em JWT**, um módulo de **hábitos de saúde** e o consumo de um **serviço externo** para sugestões de hábitos. A estrutura continua separada em camadas (Controller, Service e Repository) e preserva as boas práticas de modularidade e manutenção. O foco agora vai além da persistência de dados: promove segurança, engajamento e bem‑estar dos usuários.

### Integrantes
Lucas Garcia - RM554070


Enzo Barbeli - RM554272


Enzzo Monteiro Barros Silva - RM552616


Felipe Santos - RM554249


Iago Diniz - 553776

### 1.1. Principais Características

As principais características desta versão são:

- **Arquitetura SOA**: código modular organizado em serviços independentes e reutilizáveis.
- **Autenticação e Autorização**: segurança stateless implementada com **Spring Security** e **JWT**. Senhas são armazenadas com **BCrypt** e os usuários recebem um token para acessar recursos protegidos.
- **Módulo de Hábitos de Saúde**: permite aos usuários registrar, listar e remover hábitos (por exemplo, beber água, praticar exercícios, etc.).
- **Consumo de Serviço Externo**: a API consome a [Bored API](https://bored-api.appbrewery.com/) para sugerir atividades recreativas que podem inspirar novos hábitos saudáveis.
- **Endpoints RESTful**: interface de comunicação padronizada com uso correto de métodos HTTP (GET, POST, PUT, DELETE).
- **Persistência de Dados**: conexão com banco de dados (H2 para desenvolvimento, PostgreSQL para produção) utilizando **Spring Data JPA**.
- **Controle de Migrações**: gerenciamento do esquema do banco de dados com **Flyway**. A versão 2 inclui migração para adicionar colunas de senha e papel aos usuários e criar a tabela de hábitos.
- **Documentação Interativa**: geração automática de documentação com **Springdoc/Swagger**.

## 2. Arquitetura do Projeto

O projeto está estruturado em pacotes que representam as camadas da arquitetura SOA:

- `com.checkin.api.controller`: Camada de apresentação, responsável por expor os endpoints REST e receber requisições.
- `com.checkin.api.service`: Camada de serviço, onde reside a lógica de negócio, validações e orquestração das operações. Os controllers dependem de contratos como `UsuarioServicePort`, `CheckinServicePort`, `HabitoServicePort` e `SugestaoServicePort`, permitindo polimorfismo, baixo acoplamento e troca de implementação sem alterar a camada web.
- `com.checkin.api.repository`: Camada de acesso a dados, responsável pela comunicação com o banco de dados através do Spring Data JPA.
- `com.checkin.api.model`: Contém as entidades JPA que mapeiam as tabelas do banco de dados.
- `com.checkin.api.dto`: Data Transfer Objects, utilizados para transferir dados entre as camadas e o cliente, evitando a exposição de entidades internas.
- `com.checkin.api.exception`: Classes de exceções customizadas e um handler global para tratamento de erros.
- `com.checkin.api.config`: Configurações de segurança, CORS e OpenAPI.

## 3. Endpoints da API

A seguir, a descrição dos endpoints disponíveis.

### 3.1. Autenticação

Antes de consumir os demais endpoints protegidos, é necessário registrar‑se e obter um token JWT.

- **`POST /api/auth/register`**: registra um novo usuário.
  - **Corpo da requisição**:
    ```json
    {
      "nome": "Lucas Garcia",
      "email": "lucas@example.com",
      "senha": "minhaSenhaSegura"
    }
    ```
  - **Resposta de sucesso (201 Created)**: retorna o usuário criado (sem a senha).

- **`POST /api/auth/login`**: autentica um usuário e retorna um token JWT.
  - **Corpo da requisição**:
    ```json
    {
      "email": "lucas@example.com",
      "senha": "minhaSenhaSegura"
    }
    ```
  - **Resposta de sucesso (200 OK)**:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
    }
    ```
  - Guarde esse token e envie nas próximas requisições no cabeçalho `Authorization: Bearer <token>`.

### 3.2. Check-in

- **`POST /api/checkin`**: Cria um novo check-in.
  - **Corpo da Requisição**:
    ```json
    {
      "usuarioId": 1,
      "tarefaConcluida": true,
      "diario": "Hoje o dia foi muito produtivo, consegui finalizar todas as minhas tarefas principais."
    }
    ```
  - **Resposta de Sucesso (201 Created)**:
    ```json
    {
      "id": 1,
      "usuarioId": 1,
      "usuarioNome": "Usuário de Teste",
      "tarefaConcluida": true,
      "diario": "Hoje o dia foi muito produtivo, consegui finalizar todas as minhas tarefas principais.",
      "dataCheckin": "2025-11-28T10:30:00"
    }
    ```

- **`GET /api/historico`**: Busca o histórico completo de check-ins de um usuário.
  - **Parâmetros de Query**:
    - `usuarioId` (Long, obrigatório): ID do usuário.
  - **Resposta de Sucesso (200 OK)**:
    ```json
    [
      {
        "data": "2025-11-28T10:30:00",
        "tarefaConcluida": true,
        "diario": "Dia produtivo."
      }
    ]
    ```

### 3.3. Usuários

- A criação de usuários é feita pelo endpoint público **`POST /api/auth/register`**, que criptografa a senha com BCrypt antes de persistir.
- **`GET /api/usuarios/{id}`**: Busca um usuário por ID.
- **`GET /api/usuarios`**: Lista todos os usuários.
- **`PUT /api/usuarios/{id}`**: Atualiza um usuário existente.
- **`DELETE /api/usuarios/{id}`**: Deleta um usuário.

### 3.4. Hábitos de Saúde

Os endpoints de hábitos permitem registrar, consultar e remover hábitos saudáveis. Todos exigem envio do token JWT no cabeçalho `Authorization: Bearer <token>`.

- **`POST /api/habitos/{usuarioId}`**: cria um novo hábito para o usuário indicado pelo `usuarioId`.
  - **Corpo da requisição**:
    ```json
    {
      "nome": "Beber água",
      "descricao": "Beber 2 litros de água por dia",
      "meta": "2 litros"
    }
    ```
  - **Resposta de sucesso (201 Created)**: retorna o hábito criado.

- **`GET /api/habitos/{usuarioId}`**: lista todos os hábitos do usuário.

- **`DELETE /api/habitos/{habitoId}/deletar`**: remove um hábito pelo seu ID.

### 3.5. Sugestões de Hábitos

Para incentivar os usuários a adotar novos hábitos, a API consome um serviço externo e retorna uma sugestão de atividade saudável.

- **`GET /api/sugestoes/habito`**: retorna uma sugestão de hábito. Não requer parâmetros. A resposta contém um campo `sugestao` com a descrição da atividade sugerida.

## 4. Como Executar a Aplicação

### 4.1. Pré-requisitos

- **Java 17** ou superior
- **Maven 3.8** ou superior
- **Docker** (opcional, para rodar o PostgreSQL em produção)

### 4.2. Passos para Execução

1. **Clone o repositório**:
   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd checkin-api
   ```

2. **Compile o projeto com Maven**:
   O Maven irá baixar todas as dependências definidas no `pom.xml`.
   ```bash
   mvn clean install
   ```

3. **Execute a aplicação**:
   Por padrão, a API iniciará no perfil de **desenvolvimento**, utilizando o banco de dados em memória **H2**.
   ```bash
   mvn spring-boot:run
   ```
   A API estará disponível em `http://localhost:8080`.

   > **Nota sobre variáveis de ambiente**: por questões de segurança, recomenda‑se definir a chave secreta do JWT em produção através da variável `SECURITY_JWT_SECRET`. Outra variável opcional é `SUGESTAO_API_URL`, que permite trocar a API externa utilizada para sugestões.

### 4.3. Acessando a Documentação Swagger

Com a aplicação em execução, acesse a documentação interativa no navegador:

- **URL**: `http://localhost:8080/swagger-ui.html`

Nesta página, você pode visualizar todos os endpoints, seus parâmetros, e testá-los diretamente.
Para endpoints protegidos, clique em **Authorize** e informe o token no formato `Bearer <token>`.

### 4.4. Acessando o Console do H2

Para o ambiente de desenvolvimento, você pode acessar o console do banco de dados H2 para inspecionar os dados:

- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:checkindb`
- **User Name**: `sa`
- **Password**: (deixe em branco)

### 4.5. Rodando os Testes

Execute a suíte automatizada com:

```bash
mvn test
```

A suíte cobre testes unitários dos services com Mockito e testes de integração com `MockMvc` para registro, login, criptografia de senha, proteção de endpoints sem token e acesso autenticado com JWT.

## 5. Configuração para Produção

Para executar a API em um ambiente de produção, você precisa ativar o perfil `prod` e configurar as variáveis de ambiente para o banco de dados **PostgreSQL**.

1. **Ative o perfil de produção**:
   Você pode fazer isso de duas formas:

   - Via linha de comando:
     ```bash
     mvn spring-boot:run -Dspring-boot.run.profiles=prod
     ```

   - Ou editando o `application.properties`:
     ```properties
     spring.profiles.active=prod
     ```

2. **Configure as variáveis de ambiente do banco de dados**:
   A aplicação espera as seguintes variáveis de ambiente para se conectar ao PostgreSQL:
   - `DB_HOST`: Host do banco de dados (padrão: `localhost`)
   - `DB_PORT`: Porta do banco de dados (padrão: `5432`)
   - `DB_NAME`: Nome do banco de dados (padrão: `checkindb`)
   - `DB_USER`: Usuário do banco de dados (padrão: `postgres`)
   - `DB_PASSWORD`: Senha do banco de dados (padrão: `admin`)

   **Exemplo de execução com variáveis de ambiente**:
   ```bash
   export DB_HOST=meu-postgres.db.com
   export DB_NAME=checkin_prod
   export DB_USER=user_prod
   export DB_PASSWORD=senha_segura
   
   java -jar target/checkin-api-1.0.0.jar --spring.profiles.active=prod
   ```

## 6. Segurança

A segurança é um pilar fundamental desta API. Nesta versão as principais medidas são:

- **Autenticação e Autorização via JWT**: a aplicação é stateless e utiliza **JSON Web Tokens** para autenticar usuários. Após realizar o login, um token assinado é retornado e deve ser enviado no cabeçalho `Authorization` de cada requisição. As rotas sob `/api/auth/**`, o Swagger e o console do H2 continuam públicos.
- **Senhas Criptografadas**: as senhas são armazenadas no banco utilizando **BCrypt**, impossibilitando a recuperação da senha original.
- **Papéis de Usuário**: cada usuário possui um papel (`USER` ou `ADMIN`), possibilitando cenários de autorização diferenciados em futuras evoluções.
- **Validação de Entrada**: o `CheckinService` mantém o método `validarConteudoDiario` para bloquear padrões comuns de **SQL Injection** e **XSS** no diário de check‑ins.
- **CORS (Cross‑Origin Resource Sharing)**: configurado para permitir que origens confiáveis (como seu aplicativo móvel) acessem a API. Por padrão, qualquer origem é permitida em desenvolvimento.
- **Tratamento de Exceções**: o `GlobalExceptionHandler` captura exceções e retorna mensagens padronizadas, incluindo erros de autenticação (credenciais inválidas) com status 401.

## 7. Sprint 4 - SOA e WebServices

Esta versão contempla os principais critérios da Sprint 4:

- **Código limpo e SOLID**: controllers, services e repositories separados por responsabilidade; services expostos por interfaces `*ServicePort`.
- **Segurança stateless**: Spring Security configurado com `SessionCreationPolicy.STATELESS`, filtro JWT, BCrypt e respostas `401` para endpoints protegidos sem autenticação.
- **Regras em services**: validações de usuário, check-in, hábitos e fallback da API externa ficam encapsulados na camada de service.
- **Documentação automática**: SpringDoc/Swagger em `/swagger-ui.html`, com esquema Bearer JWT configurado.
- **Testes automatizados**: 19 testes cobrindo services e fluxo integrado de autenticação/autorização.
