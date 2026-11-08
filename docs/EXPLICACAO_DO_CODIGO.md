# Explicação dos arquivos principais

## `pom.xml`

- `spring-boot-starter-parent`: centraliza versões e configurações Maven do Spring Boot.
- `java.version`: define Java 21 para compilação.
- `starter-webmvc`: oferece Spring MVC, servidor embarcado e suporte a controllers REST.
- `starter-data-jpa`: fornece JPA/Hibernate e Spring Data Repository.
- `starter-validation`: ativa validações como `@NotBlank`, `@NotNull` e `@Size`.
- `postgresql`: driver JDBC para conectar ao PostgreSQL.
- `flyway-core` + `flyway-database-postgresql`: motor de migrações e suporte ao dialeto PostgreSQL.
- `springdoc-openapi-starter-webmvc-ui`: gera OpenAPI e disponibiliza Swagger UI.
- dependências `test`: JUnit/Mockito/MockMvc e H2 somente no ciclo de teste.

## `AlertaspApplication.java`

É o ponto de entrada. `@SpringBootApplication` combina configuração automática,
component scan e configuração Spring. O `main` inicializa o contexto e o servidor.

## `entity/`

`MonitoringPoint` é a entidade persistida em `monitoring_points`. `FloodOccurrence`
é persistida em `flood_occurrences`. A anotação `@ManyToOne` com `@JoinColumn`
materializa a relação: várias ocorrências podem apontar para o mesmo ponto.

Não usamos `@OneToMany` inverso no ponto porque o protótipo não precisa carregar todas
as ocorrências junto com cada ponto; isso evita consultas desnecessárias e serialização
circular. A relação está expressa no lado proprietário, `FloodOccurrence`.

## `enums/`

`Severity` evita textos livres para gravidade. `OccurrenceSource` identifica a origem
informada. `OccurrenceStatus` concentra a regra `canTransitionTo`, que determina quais
mudanças de estado são permitidas.

## `dto/`

Os DTOs definem o que a API aceita e devolve. As anotações de validação atuam antes da
regra de negócio. O DTO de criação não possui campo de status: assim o cliente não pode
criar uma ocorrência já marcada como confirmada ou resolvida.

## `controller/`

Cada controller representa um recurso REST. `@GetMapping`, `@PostMapping` e
`@PatchMapping` mapeiam método HTTP e caminho. `@Valid` dispara validação. O POST devolve
201 Created e um cabeçalho `Location` com o recurso recém-criado.

## `service/`

A camada de serviço concentra os casos de uso. `@Transactional` mantém cada operação
consistente; `readOnly=true` marca consultas. `FloodOccurrenceService` valida a existência
e atividade do ponto, define o status inicial, confere as transições e monta DTOs.

## `repository/`

As interfaces estendem `JpaRepository` para CRUD e `JpaSpecificationExecutor` para
consultas paginadas com filtros combináveis. O Spring Data implementa as consultas em
runtime; não precisamos escrever SQL de CRUD manualmente.

## `exception/`

Exceções específicas representam recurso ausente e violação de regra. `@RestControllerAdvice`
converte essas exceções em JSON e códigos HTTP consistentes: 404, 409 e 400.

## `resources/application.properties`

A conexão é configurada por variáveis de ambiente. O padrão usa Postgres local. A opção
`spring.jpa.hibernate.ddl-auto=validate` faz Hibernate verificar o schema sem criá-lo.
Flyway executa migrations localizadas em `db/migration`.

## `resources/db/migration/`

`V1` cria as tabelas, constraints e índices. `V2` adiciona exemplos. Flyway registra
versão, checksum e resultado em `flyway_schema_history`. Uma migration aplicada não deve
ser reescrita; mudanças futuras entram em `V3`, `V4` etc.

## Testes

`OccurrenceStatusTest` prova regras do enum sem subir Spring. `FloodOccurrenceServiceTest`
usa Mockito para substituir repositórios e testar casos do serviço. `ApiIntegrationTest`
sobe o contexto com H2, executa migrations e envia chamadas HTTP simuladas usando MockMvc.

## Configuração e transparência

`TimeConfiguration` injeta `Clock` para que o horário seja controlável nos testes.
`OpenApiConfiguration` informa título/versão da documentação. Os logs mostram IDs de
operações, sem registrar descrições de cidadãos ou dados pessoais.
