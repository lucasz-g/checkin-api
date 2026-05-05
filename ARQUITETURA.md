# Arquitetura e Boas Práticas da CheckIn API

## 1. Arquitetura Orientada a Serviços (SOA)

A **CheckIn API** foi desenvolvida seguindo os princípios da **Arquitetura Orientada a Serviços (SOA)**, que promove a modularidade, reutilização e independência dos componentes.

### 1.1. Separação de Camadas

O projeto está organizado em três camadas principais, cada uma com responsabilidades bem definidas:

#### **Camada de Apresentação (Controller)**

- **Responsabilidade**: Receber requisições HTTP, validar parâmetros de entrada e retornar respostas adequadas.
- **Localização**: `com.checkin.api.controller`
- **Princípios**:
  - **NÃO** deve conter lógica de negócio.
  - Deve apenas delegar as operações para a camada de serviço.
  - Utiliza anotações do Spring Web (`@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`).
  - Retorna DTOs (Data Transfer Objects) ao invés de entidades JPA.

**Exemplo**:
```java
@PostMapping("/checkin")
public ResponseEntity<CheckinResponseDTO> criarCheckin(@Valid @RequestBody CheckinRequestDTO requestDTO) {
    CheckinResponseDTO response = checkinService.criarCheckin(requestDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

#### **Camada de Serviço (Service)**

- **Responsabilidade**: Implementar a lógica de negócio, validações e orquestração das operações entre múltiplos repositories.
- **Localização**: `com.checkin.api.service`
- **Princípios**:
  - Contém as regras de negócio da aplicação.
  - Valida dados antes de persistir no banco de dados.
  - Coordena operações entre múltiplos repositories, se necessário.
  - Lança exceções customizadas (`ResourceNotFoundException`, `BusinessValidationException`).
  - Utiliza transações (`@Transactional`) para garantir consistência dos dados.

**Exemplo**:
```java
@Transactional
public CheckinResponseDTO criarCheckin(CheckinRequestDTO requestDTO) {
    Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    
    validarConteudoDiario(requestDTO.getDiario());
    
    Checkin checkin = new Checkin(usuario, requestDTO.getTarefaConcluida(), requestDTO.getDiario());
    Checkin checkinSalvo = checkinRepository.save(checkin);
    
    return new CheckinResponseDTO(checkinSalvo);
}
```

#### **Camada de Acesso a Dados (Repository)**

- **Responsabilidade**: Comunicação direta com o banco de dados através do Spring Data JPA.
- **Localização**: `com.checkin.api.repository`
- **Princípios**:
  - **NÃO** deve conter lógica de negócio.
  - Utiliza interfaces que estendem `JpaRepository`.
  - Define queries customizadas com `@Query` quando necessário.
  - Aproveita os métodos derivados do Spring Data JPA (ex: `findByEmail`, `existsByEmail`).

**Exemplo**:
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### 1.2. Benefícios da Arquitetura SOA

- **Modularidade**: Cada camada tem responsabilidades claras e pode ser modificada independentemente.
- **Reutilização**: Serviços podem ser reutilizados por múltiplos controllers.
- **Testabilidade**: Camadas podem ser testadas isoladamente com mocks.
- **Manutenibilidade**: Facilita a identificação e correção de bugs.
- **Escalabilidade**: Permite que a aplicação cresça de forma organizada.

## 2. Padrões e Boas Práticas Implementadas

### 2.1. REST (Representational State Transfer)

A API segue os princípios REST:

- **Recursos**: Entidades são representadas como recursos (`/api/usuarios`, `/api/checkin`).
- **Métodos HTTP**: Uso correto dos verbos HTTP:
  - `GET`: Buscar dados (idempotente).
  - `POST`: Criar novos recursos.
  - `PUT`: Atualizar recursos existentes.
  - `DELETE`: Remover recursos.
- **Códigos de Status HTTP**: Respostas utilizam códigos apropriados:
  - `200 OK`: Requisição bem-sucedida.
  - `201 Created`: Recurso criado com sucesso.
  - `204 No Content`: Recurso deletado com sucesso.
  - `400 Bad Request`: Dados inválidos.
  - `404 Not Found`: Recurso não encontrado.
  - `500 Internal Server Error`: Erro interno do servidor.

### 2.2. DTOs (Data Transfer Objects)

- **Objetivo**: Transferir dados entre as camadas sem expor as entidades JPA diretamente.
- **Benefícios**:
  - Controle sobre quais dados são expostos ao cliente.
  - Evita problemas de serialização (ex: lazy loading do Hibernate).
  - Permite validações específicas para entrada e saída de dados.

### 2.3. Tratamento de Exceções

O `GlobalExceptionHandler` centraliza o tratamento de erros:

- **ResourceNotFoundException**: Retorna `404 Not Found`.
- **BusinessValidationException**: Retorna `400 Bad Request`.
- **MethodArgumentNotValidException**: Retorna `400 Bad Request` com detalhes dos campos inválidos.
- **Exception**: Retorna `500 Internal Server Error` para erros não tratados.

### 2.4. Validação de Entrada

A API utiliza **Bean Validation** (JSR-380) para validar dados de entrada:

- Anotações como `@NotNull`, `@NotBlank`, `@Size`, `@Email` são usadas nos DTOs e entidades.
- Validações customizadas são implementadas na camada de serviço (ex: `validarConteudoDiario`).

### 2.5. Segurança

- **Validação contra SQL Injection**: O método `validarConteudoDiario` bloqueia padrões comuns de SQL Injection.
- **Validação contra XSS**: Bloqueia tags HTML e scripts maliciosos.
- **CORS**: Configurado para permitir requisições de origens confiáveis.

## 3. Controle de Migrações com Flyway

O **Flyway** é utilizado para gerenciar o schema do banco de dados de forma versionada:

- **Scripts de Migração**: Localizados em `src/main/resources/db/migration`.
- **Nomenclatura**: Seguem o padrão `V{versão}__{descrição}.sql` (ex: `V1__Create_tables.sql`).
- **Vantagens**:
  - Histórico de mudanças no banco de dados.
  - Aplicação automática de migrações ao iniciar a aplicação.
  - Controle de versão do schema junto com o código.

## 4. Interoperabilidade e Escalabilidade

### 4.1. Interoperabilidade

- **JSON**: Formato padrão para troca de dados, suportado por todas as plataformas.
- **REST**: Protocolo universal, permitindo integração com aplicações web, mobile e desktop.
- **Documentação OpenAPI**: Facilita a integração de clientes através de especificações padronizadas.

### 4.2. Escalabilidade

- **Stateless**: A API não mantém estado entre requisições, facilitando a escalabilidade horizontal.
- **Banco de Dados Relacional**: PostgreSQL suporta alta carga e pode ser escalado verticalmente ou com replicação.
- **Transações**: Uso de `@Transactional` garante consistência dos dados mesmo em ambientes de alta concorrência.

## 5. Estrutura de Diretórios

```
checkin-api/
├── src/
│   ├── main/
│   │   ├── java/com/checkin/api/
│   │   │   ├── CheckinApiApplication.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   └── config/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db/migration/
│   └── test/
├── pom.xml
├── README.md
├── TESTES.md
├── ARQUITETURA.md
└── run.sh
```

## 6. Próximos Passos

Para expandir a API, considere:

- **Autenticação e Autorização**: Implementar Spring Security com JWT.
- **Paginação**: Adicionar suporte a paginação nos endpoints de listagem.
- **Cache**: Utilizar Redis para cachear consultas frequentes.
- **Monitoramento**: Integrar com ferramentas como Prometheus e Grafana.
- **Testes Automatizados**: Criar testes unitários e de integração com JUnit e Mockito.
