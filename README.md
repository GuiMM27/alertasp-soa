# ALERTASP — API SOA (FIAP Global Solution 2026.2)

API REST acadêmica para cadastro de pontos de monitoramento e acompanhamento de
ocorrências de alagamento em São Paulo. O projeto demonstra domínio, persistência
relacional, migrações versionadas, documentação OpenAPI e testes automatizados.

> **Transparência dos dados:** os registros criados pela migration `V2` são fictícios e
> têm origem `DEMONSTRATION`. Não são dados em tempo real, não foram obtidos do CGE e
> não representam a ocorrência de alagamentos reais nos endereços indicados.

## Ficha CitizenTech (resumo)

- **Nome:** ALERTASP.
- **Cidade:** São Paulo (capital). Os pontos do banco inicial são referências demonstrativas.
- **Cidadão:** pessoa que circula pela cidade e precisa entender onde há registros de
  alagamento, qual é o status e quando a informação foi atualizada.
- **Problema:** durante chuvas intensas, é difícil encontrar em uma experiência clara e
  localizada informações sobre vias com registros de alagamento, estado da ocorrência
  e momento da atualização.
- **Como funciona:** o backend mantém pontos monitorados e ocorrências vinculadas a eles,
  identificando status, severidade, origem e datas. A API permite consultar, filtrar,
  cadastrar e atualizar esses registros.
- **ODS:** ODS 11 e 13 como conexões principais; ODS 3 como complementar. A API não mede
  redução de acidentes, danos ou impactos climáticos; ela oferece organização da informação.
- **Limite do protótipo:** não consulta CGE, GeoSampa, INMET, Cemaden ou sensores reais.

## Integrantes

Preencher antes da entrega:

1. Enzo Almeida — RM556900
2. Gabriel de Mello — RM554421
3. Guilherme Moreira - RM557290


## Tecnologias e versões-base

- Java 21
- Spring Boot 4.1.2
- Maven 3.6.3 ou superior (recomendado: Maven 3.9.x)
- PostgreSQL 17 (Docker Compose)
- Spring Data JPA / Hibernate
- Flyway
- springdoc-openapi 3.2.0
- JUnit 6 / Mockito / Spring Boot Test

**Atenção à versão ensinada em aula:** o PDF geral da GS não informa a versão de Java ou
Spring utilizada nas aulas, e este repositório não contém a tarefa específica do Teams.
Antes de entregar, confirme com o professor se a turma deve usar Spring Boot 3.x. Se a
orientação for Boot 3.x, não troque somente a versão do parent: o starter MVC e a versão
do springdoc precisam ser ajustados em conjunto (Boot 3.5.x usa starter web e springdoc
2.x; Boot 4.x usa starter webmvc e springdoc 3.x).

## Pré-requisitos

1. JDK 21 instalado (`java -version`).
2. Maven 3.6.3+ instalado (`mvn -version`).
3. Docker Desktop instalado e aberto (`docker --version` e `docker compose version`).
4. Git instalado.

## Configuração local — Windows (Git Bash ou PowerShell)

### 1. Configurar as variáveis de ambiente

Na pasta raiz do repositório, crie o arquivo `.env` a partir do exemplo.

**Git Bash:**

```bash
cp .env.example .env
```

**PowerShell:**

```powershell
Copy-Item .env.example .env
```

O Docker Compose utiliza o arquivo `.env`, mas o Spring Boot não lê esse arquivo automaticamente.

A senha `alertasp_local_only` é utilizada exclusivamente no ambiente local de desenvolvimento. Em outros ambientes, configure credenciais próprias e não publique arquivos `.env`.

### 2. Iniciar o PostgreSQL

Este projeto utiliza PostgreSQL executado em um container Docker.

A configuração local utiliza:

- **Porta do computador:** `5433`
- **Porta interna do container:** `5432`
- **Banco de dados:** `alertasp`
- **Usuário:** `alertasp`
- **Senha local:** `alertasp_local_only`

A utilização da porta `5433` evita conflitos com outras instalações do PostgreSQL que possam utilizar a porta `5432` no computador.

Inicie o banco:

```bash
docker compose up -d postgres
```

Verifique se o container está funcionando:

```bash
docker compose ps
```

O container deverá aparecer com status `healthy`.

### 3. Configurar a conexão do Spring Boot

Como a porta externa do PostgreSQL é `5433`, é necessário configurar a URL de conexão antes de executar a aplicação.

**Git Bash:**

```bash
export DB_URL=jdbc:postgresql://localhost:5433/alertasp
export DB_USERNAME=alertasp
export DB_PASSWORD=alertasp_local_only
```

**Windows PowerShell:**

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5433/alertasp"
$env:DB_USERNAME = "alertasp"
$env:DB_PASSWORD = "alertasp_local_only"
```

Essas variáveis devem ser configuradas no terminal em que a aplicação será iniciada.

### 4. Executar a aplicação

Com o PostgreSQL funcionando e as variáveis de ambiente configuradas, inicie o Spring Boot.

**Git Bash:**

```bash
mvn.cmd spring-boot:run
```

**PowerShell:**

```powershell
mvn spring-boot:run
```

Quando aparecer a mensagem `Started AlertaspApplication`, a API estará disponível em:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Pontos de monitoramento:** http://localhost:8080/api/v1/monitoring-points
- **Ocorrências:** http://localhost:8080/api/v1/flood-occurrences

### 5. Migrations e persistência de dados

Na primeira inicialização, o Flyway executa automaticamente as migrations:

- `V1__create_core_tables.sql` — criação das tabelas principais.
- `V2__insert_demonstration_data.sql` — inserção dos registros demonstrativos.

O Hibernate utiliza `ddl-auto=validate` para validar a compatibilidade entre as entidades Java e as tabelas existentes, sem criar automaticamente o esquema do banco.

Os dados persistidos pelo PostgreSQL são mantidos no volume Docker configurado no projeto.

Para verificar o histórico das migrations:

```bash
docker exec alertasp-postgres psql -U alertasp -d alertasp -c "SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

### 6. Executar os testes automatizados

**Git Bash:**

```bash
mvn.cmd clean test
```

**PowerShell:**

```powershell
mvn clean test
```

Os testes automatizados utilizam um banco H2 configurado para testes e não exigem o PostgreSQL em execução.

A execução bem-sucedida deverá apresentar `BUILD SUCCESS`.

### 7. Encerrar o ambiente

Para interromper a aplicação Spring Boot, pressione `Ctrl + C` no terminal em que ela está sendo executada.

Para interromper o PostgreSQL sem excluir os dados:

```bash
docker compose down
```

**Atenção:** não utilize `docker compose down -v` se desejar preservar os registros armazenados no volume do banco.

## Comandos úteis

Executar todos os testes (usa H2 somente no perfil de teste):

```powershell
mvn test
```

Gerar o JAR executável:

```powershell
mvn clean package
```

Executar o JAR:

```powershell
java -jar target/alertasp-soa-1.0.0.jar
```

Parar o banco:

```powershell
docker compose down
```

Apagar também os dados persistidos do banco local (ação destrutiva):

```powershell
docker compose down -v
```

## Endereços locais

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints

| Método | Caminho | Descrição |
|---|---|---|
| GET | `/api/v1/monitoring-points` | Lista pontos; filtros `neighborhood`, `active`; paginação `page`, `size`, `sort` |
| GET | `/api/v1/monitoring-points/{id}` | Busca ponto por ID |
| POST | `/api/v1/monitoring-points` | Cadastra ponto |
| GET | `/api/v1/flood-occurrences` | Lista ocorrências; filtros `status`, `neighborhood`, `from`, `to`; paginação |
| GET | `/api/v1/flood-occurrences/{id}` | Busca ocorrência por ID |
| POST | `/api/v1/flood-occurrences` | Registra ocorrência (status inicial `REPORTED`) |
| PATCH | `/api/v1/flood-occurrences/{id}/status` | Atualiza status respeitando transições permitidas |

### Exemplo: cadastrar uma ocorrência

```http
POST /api/v1/flood-occurrences
Content-Type: application/json
```

```json
{
  "monitoringPointId": 1,
  "description": "Exemplo acadêmico de acúmulo de água na via.",
  "severity": "MEDIUM",
  "source": "DEMONSTRATION",
  "occurredAt": "2026-11-05T12:00:00-03:00"
}
```

Resposta esperada: HTTP `201 Created`, corpo contendo `status: REPORTED`, origem e
horários. O ID `1` é criado pela migration de demonstração em um banco vazio.

### Exemplo: atualizar status

```http
PATCH /api/v1/flood-occurrences/1/status
Content-Type: application/json
```

```json
{
  "status": "CONFIRMED"
}
```

Resposta esperada: HTTP `200 OK`. Uma transição inválida retorna `409 Conflict`.

### Exemplo: filtrar ocorrências

```http
GET /api/v1/flood-occurrences?status=REPORTED&neighborhood=Bela%20Vista&page=0&size=10
```

Intervalo de data/hora deve ser enviado em ISO-8601 com fuso, por exemplo:

```http
GET /api/v1/flood-occurrences?from=2026-11-01T00:00:00-03:00&to=2026-11-06T23:59:59-03:00
```

## Estrutura do projeto

```text
src/
  main/
    java/br/com/alertasp/soa/
      config/       # Clock e metadados OpenAPI
      controller/   # Endpoints REST
      dto/          # Contratos HTTP de entrada e saída
      entity/       # Entidades JPA
      enums/        # Status, severidade e origem
      exception/    # Erros de domínio e tratamento HTTP
      repository/   # Interfaces Spring Data JPA
      service/      # Regras de negócio e casos de uso
    resources/
      db/migration/ # V1 schema; V2 dados fictícios
      application.properties
      application-test.properties
  test/java/        # Testes unitários e integração HTTP
```

## Migrações Flyway

- `V1__create_core_tables.sql`: cria tabelas, chaves estrangeiras, checks e índices.
- `V2__insert_demonstration_data.sql`: adiciona dados explicitamente demonstrativos.

Para provar a migração, suba um banco vazio com `docker compose down -v`, inicie-o e
execute a aplicação. Consulte a tabela `flyway_schema_history` no PostgreSQL e registre
as duas versões aplicadas. Em equipes, não edite uma migration já aplicada: crie
`V3__...sql` para qualquer alteração posterior.

## Testes incluídos

- `OccurrenceStatusTest`: valida as transições possíveis e o caráter final de `RESOLVED`.
- `FloodOccurrenceServiceTest`: verifica status inicial, referência obrigatória a ponto
  existente e bloqueio de transição inválida.
- `ApiIntegrationTest`: sobe o contexto Spring com H2, executa migrations, consulta
  registros, testa criação de ocorrência, erro 404 e disponibilidade do OpenAPI.

H2 é utilizado apenas para automatizar testes sem exigir Docker. A configuração padrão
(`application.properties`) usa PostgreSQL. Os valores `source` (`CITIZEN_REPORT`,
`FIELD_AGENT`, `SENSOR`, `PUBLIC_DATA`) são rótulos informados pela requisição; esta versão
não autentica nem verifica a identidade/origem de quem os declarou. Para uso real, isso
exigiria autenticação, autorização e trilha de auditoria.

Esta API é um protótipo acadêmico sem autenticação. Não a exponha publicamente nem envie
informações pessoais; uma implantação real exigiria controles de acesso e proteção adicional.

## Evidências para a entrega

Salvar em `evidencias/` (criar a pasta) capturas reais feitas pela equipe:

1. Terminal com `docker compose ps` mostrando PostgreSQL saudável.
2. Terminal com `mvn test` concluído sem falhas.
3. Terminal/log mostrando Flyway aplicando V1 e V2.
4. PostgreSQL mostrando `monitoring_points`, `flood_occurrences` e `flyway_schema_history`.
5. Swagger UI aberto, com os sete endpoints documentados.
6. POST de ocorrência com resposta `201` e GET mostrando o registro persistido.
7. Tentativa de transição inválida com resposta `409`.

Não crie prints ilustrativos e os apresente como evidência real. As evidências devem ser
capturadas depois de executar a aplicação no computador de alguém da equipe.

## Checklist antes do envio

- [ ] Confirmar a versão de Java/Spring orientada pelo professor.
- [ ] Substituir os nomes/RMs dos integrantes neste README.
- [ ] Executar `docker compose up -d postgres`.
- [ ] Executar `mvn clean test` e guardar a saída real.
- [ ] Executar a aplicação e verificar Swagger UI.
- [ ] Demonstrar inserção e leitura no PostgreSQL.
- [ ] Mostrar `flyway_schema_history` com V1 e V2.
- [ ] Adicionar evidências reais ao repositório.
- [ ] Conferir se o repositório está público ou acessível ao avaliador.
- [ ] Enviar o link na tarefa de SOA no Teams/Portal até 19/11/2026 às 23h55.

## Referências técnicas

- Spring Boot system requirements: https://docs.spring.io/spring-boot/system-requirements.html
- Spring Boot build systems: https://docs.spring.io/spring-boot/reference/using/build-systems.html
- springdoc compatibility matrix: https://springdoc.org/faq.html
- Flyway documentation: https://documentation.red-gate.com/flyway
