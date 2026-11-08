# ALERTASP — API REST | FIAP Global Solution 2026.2

**Disciplina:** Arquitetura Orientada a Serviços (SOA) e Web Services  
**Tema:** CitizenTech — soluções tecnológicas para desafios das cidades  
**Repositório:** https://github.com/GuiMM27/alertasp-soa

## Sobre o projeto

O **ALERTASP** é um protótipo acadêmico de backend para organizar informações sobre pontos de monitoramento e ocorrências de alagamento na cidade de São Paulo. A API permite cadastrar e consultar pontos, registrar ocorrências vinculadas a eles, filtrar registros e acompanhar o status de atendimento.

A proposta está relacionada ao **ODS 11 (Cidades e Comunidades Sustentáveis)** e ao **ODS 13 (Ação Contra a Mudança Global do Clima)**, sem alegar impacto real mensurado.

> **Dados demonstrativos:** os registros iniciais e as ocorrências usadas nos testes são fictícios. O sistema não consulta sensores reais, CGE, Cemaden ou serviços meteorológicos e **não fornece alertas em tempo real**.

## Integrantes

- Enzo Almeida — RM 556900
- Gabriel de Mello — RM 554421
- Guilherme Moreira — RM 557290

## Funcionalidades

- **Pontos de monitoramento:** cadastro, listagem paginada, filtros por bairro e situação ativa, e consulta por ID.
- **Ocorrências de alagamento:** registro associado a um ponto, consulta por ID, listagem com filtros e atualização do status.
- **Regras de negócio:** uma ocorrência começa em `REPORTED` e só pode seguir transições de status permitidas; alterações inválidas retornam HTTP `409 Conflict`.
- **Persistência relacional:** PostgreSQL com entidades JPA e relacionamento entre pontos e ocorrências.
- **Migrações versionadas:** Flyway para criar as tabelas (`V1`) e carregar dados de demonstração (`V2`).
- **Documentação e qualidade:** OpenAPI/Swagger UI e testes automatizados.

**Fluxo de status:** `REPORTED → CONFIRMED → IN_PROGRESS → RESOLVED`. Também é permitido encerrar diretamente uma ocorrência em `REPORTED` ou `CONFIRMED` com `RESOLVED`. `RESOLVED` é um estado final.

## Tecnologias

- Java 21 (versão alvo do projeto)
- Spring Boot 4.1.2; Spring Web MVC; Spring Data JPA / Hibernate
- PostgreSQL 17 em Docker Compose
- Flyway para migrações
- springdoc-openapi 3.2.0 para Swagger/OpenAPI
- Maven, JUnit, Mockito e Spring Boot Test
- H2 para testes automatizados

## Como executar localmente (Windows)

**Pré-requisitos:** JDK compatível com Java 21, Maven, Git e Docker Desktop em execução. Execute os comandos na **raiz do projeto**.

### 1. Preparar o ambiente

Crie o arquivo de variáveis locais a partir do exemplo, caso ainda não exista:

**Git Bash:**

```bash
cp .env.example .env
```

**PowerShell:**

```powershell
Copy-Item .env.example .env
```

O Docker Compose lê `.env` automaticamente, mas o Spring Boot **não**. O `.env` é ignorado pelo Git. A senha de demonstração `alertasp_local_only` deve ser usada **apenas no desenvolvimento local**.

### 2. Iniciar o PostgreSQL

```bash
docker compose up -d postgres
docker compose ps
```

Confira se `alertasp-postgres` aparece como **healthy**. O projeto publica a porta **5433 no computador**, mapeada para a porta **5432 dentro do container**, para evitar conflito com outro PostgreSQL local.

Credenciais locais: banco `alertasp`, usuário `alertasp` e senha `alertasp_local_only`.

### 3. Configurar e iniciar a API

**Git Bash:**

```bash
export DB_URL=jdbc:postgresql://localhost:5433/alertasp
export DB_USERNAME=alertasp
export DB_PASSWORD=alertasp_local_only
mvn.cmd spring-boot:run
```

**PowerShell:**

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5433/alertasp"
$env:DB_USERNAME = "alertasp"
$env:DB_PASSWORD = "alertasp_local_only"
mvn spring-boot:run
```

Após a mensagem `Started AlertaspApplication`, acesse:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Especificação OpenAPI:** http://localhost:8080/v3/api-docs
- **API:** http://localhost:8080/api/v1/monitoring-points

Na primeira inicialização, o Flyway aplica as migrações pendentes. O Hibernate utiliza `ddl-auto=validate`, ou seja, **valida** o esquema do banco, sem criar tabelas automaticamente.

### 4. Executar os testes automatizados

**Git Bash:**

```bash
mvn.cmd clean test
```

**PowerShell:**

```powershell
mvn clean test
```

Os testes usam H2 e não precisam do PostgreSQL em execução. Verifique se o terminal mostra `BUILD SUCCESS`. Os testes contemplam regras de status, serviços e integração HTTP/OpenAPI.

### 5. Encerrar

Interrompa a API com `Ctrl + C` e, se desejar parar o banco, execute:

```bash
docker compose down
```

Esse comando preserva o volume com os dados locais. **Não utilize `docker compose down -v` se quiser preservar os registros.**

## Endpoints da API

| Método | Endpoint | Finalidade |
|---|---|---|
| `GET` | `/api/v1/monitoring-points` | Lista pontos; filtros `neighborhood`, `active`, `page`, `size`, `sort` |
| `GET` | `/api/v1/monitoring-points/{id}` | Consulta um ponto pelo ID |
| `POST` | `/api/v1/monitoring-points` | Cadastra um ponto |
| `GET` | `/api/v1/flood-occurrences` | Lista ocorrências; filtros de status, bairro e período, com paginação |
| `GET` | `/api/v1/flood-occurrences/{id}` | Consulta uma ocorrência pelo ID |
| `POST` | `/api/v1/flood-occurrences` | Registra uma ocorrência com status inicial `REPORTED` |
| `PATCH` | `/api/v1/flood-occurrences/{id}/status` | Atualiza o status conforme as regras do domínio |

Os principais códigos HTTP utilizados são `200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found` e `409 Conflict`. As respostas dos endpoints estão documentadas no Swagger.

### Exemplo: registrar uma ocorrência fictícia

`POST /api/v1/flood-occurrences`

```json
{
  "monitoringPointId": 1,
  "description": "DEMO - Acumulo de agua na via apos chuva intensa.",
  "severity": "MEDIUM",
  "source": "DEMONSTRATION",
  "occurredAt": "2026-11-08T04:47:37.065Z"
}
```

Resposta esperada: HTTP `201 Created`, com ID gerado automaticamente e status `REPORTED`. Em uma instalação nova, o ponto de ID `1` é inserido pela migration `V2`.

Para atualizar uma ocorrência que esteja em `REPORTED`, envie `PATCH /api/v1/flood-occurrences/{id}/status` com:

```json
{
  "status": "CONFIRMED"
}
```

Uma transição proibida (por exemplo, `CONFIRMED → REPORTED`) retorna HTTP `409 Conflict`.

## Banco de dados e migrações

As migrações encontram-se em `src/main/resources/db/migration/`:

- `V1__create_core_tables.sql` — tabelas, relacionamentos, restrições e índices.
- `V2__insert_demonstration_data.sql` — pontos e ocorrências fictícios para demonstração.

Para verificar as versões aplicadas no PostgreSQL:

```bash
docker exec alertasp-postgres psql -U alertasp -d alertasp -c "SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

Mudanças posteriores no esquema devem ser criadas em novas migrações (`V3__...sql`, por exemplo), sem alterar versões já executadas.

## Organização do repositório

```text
src/main/java/br/com/alertasp/soa/
  controller/    Endpoints REST e documentação OpenAPI
  service/       Regras de negócio
  repository/    Acesso aos dados
  entity/        Entidades JPA
  dto/           Contratos de entrada e saída
  enums/         Status, gravidade e origem
  exception/     Tratamento de erros
  config/        Configurações
src/main/resources/db/migration/   Migrações Flyway
src/test/java/                 Testes automatizados
docs/                          Documentação complementar
requests/                      Exemplos de requisições HTTP
evidencias/                    Capturas reais e índice de evidências
```

Documentação complementar: [`docs/ARQUITETURA.md`](docs/ARQUITETURA.md), [`docs/CRITERIOS_DE_ACEITE.md`](docs/CRITERIOS_DE_ACEITE.md) e [`docs/MATRIZ_RUBRICA.md`](docs/MATRIZ_RUBRICA.md).

## Evidências da execução

As capturas reais da API, do PostgreSQL, do Flyway, do Swagger, dos testes e do Docker estão em [`evidencias/`](evidencias/). Consulte o [índice de evidências](evidencias/INDICE_DAS_EVIDENCIAS.md) para identificar cada imagem e o critério técnico que demonstra.

## Limitações do protótipo

Este backend não realiza medições ou previsões de alagamento, não envia alertas reais e não valida a origem declarada dos relatos. Não possui autenticação/autorização para uso público. Uma implantação de produção exigiria integração com fontes oficiais, controle de acesso, auditoria e validação dos dados.

**Entrega:** repositório público com código completo, README, documentação Swagger e evidências da aplicação, conforme a atividade de SOA da FIAP Global Solution 2026.2.
