# Guia de execução passo a passo — ALERTASP SOA

Este guia pressupõe Windows 10/11, PowerShell, JDK 21, Maven e Docker Desktop. Os nomes
podem ser adaptados se a equipe utilizar macOS/Linux.

## Etapa 0 — Antes de abrir o código

1. Confirme no Teams ou com o professor a versão de Java e Spring usada em aula.
2. Confirme se existe uma rubrica complementar à página 27 do PDF geral.
3. Mantenha Java 21 e Spring Boot 4.1.2 somente se o professor aceitar essa combinação.
4. Divida a revisão entre os integrantes, mas garanta que todos saibam explicar o fluxo
   completo: HTTP → Controller → Service → Repository → PostgreSQL.

O PDF geral não informa as versões de Java/Spring e não detalha uma exigência de IDE.

## Etapa 1 — Preparar ferramentas

Abra PowerShell e execute:

```powershell
java -version
mvn -version
docker --version
docker compose version
git --version
```

Confirme Java 21 e Maven 3.6.3 ou superior. Se algum comando não existir, instale a
ferramenta correspondente antes de continuar. Abra o Docker Desktop e aguarde ficar pronto.

## Etapa 2 — Abrir o projeto

1. Extraia o ZIP.
2. Abra a pasta `alertasp-soa` no IntelliJ IDEA ou VS Code.
3. Confirme que `pom.xml` está na raiz.
4. Aguarde o Maven importar e baixar as dependências.
5. Não mova `src` para dentro de outra pasta; o Maven espera o padrão `src/main/java`.

## Etapa 3 — Criar a configuração local do banco

```powershell
Copy-Item .env.example .env
```

O Docker Compose lê `.env`. O Spring Boot lê variáveis de ambiente do processo, mas não
lê `.env` automaticamente. Na configuração inicial, as senhas padrão do Compose e da
aplicação coincidem. Caso altere a senha em `.env`, no mesmo terminal em que executará
a aplicação rode:

```powershell
$env:DB_PASSWORD = "sua_senha_local"
```

Não envie `.env` para o GitHub.

## Etapa 4 — Subir PostgreSQL

```powershell
docker compose up -d postgres
docker compose ps
```

Aguarde o status `healthy`. O container se chama `alertasp-postgres`; o banco é `alertasp`
e o usuário é `alertasp`.

Para ver logs do banco:

```powershell
docker compose logs -f postgres
```

## Etapa 5 — Executar a aplicação

```powershell
mvn spring-boot:run
```

O Spring Boot inicia o servidor HTTP na porta 8080. No startup, Flyway procura as
migrações em `src/main/resources/db/migration` e aplica as que ainda não constam em
`flyway_schema_history`. Depois, Hibernate valida se as tabelas correspondem às entidades.

Não se deve trocar `ddl-auto=validate` por `create` ou `update`: isso esconderia a prova
de que as migrations gerenciam o schema.

## Etapa 6 — Verificar documentação

Abra no navegador:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

No Swagger UI devem aparecer os grupos de pontos de monitoramento e ocorrências. Abra
cada endpoint e confira parâmetros, bodies e respostas.

## Etapa 7 — Fazer a primeira leitura

No Swagger, abra `GET /api/v1/monitoring-points` e clique em **Try it out** → **Execute**.

Resultado esperado: HTTP 200, três itens em `content`, `totalElements` igual a 3 e dados
com origem demonstrativa quando associados às ocorrências.

## Etapa 8 — Registrar ocorrência

Execute `POST /api/v1/flood-occurrences` com:

```json
{
  "monitoringPointId": 1,
  "description": "Exemplo acadêmico de acúmulo de água na via.",
  "severity": "MEDIUM",
  "source": "DEMONSTRATION",
  "occurredAt": "2026-11-05T12:00:00-03:00"
}
```

Resultado esperado: HTTP 201 Created e corpo com `status` igual a `REPORTED`. A API
preenche `reportedAt` e `updatedAt`; o cliente não escolhe o status inicial.

## Etapa 9 — Confirmar persistência

Copie o ID retornado e faça:

```http
GET /api/v1/flood-occurrences/ID_RETORNADO
```

A ocorrência deve estar presente. Para comprovar que é persistência real, pare e reinicie
somente a aplicação (não remova o volume do banco) e faça o GET novamente.

## Etapa 10 — Testar transição de status

Para uma ocorrência com `REPORTED`, envie:

```json
{ "status": "CONFIRMED" }
```

em `PATCH /api/v1/flood-occurrences/{id}/status`. Resultado esperado: HTTP 200.

Depois, tente pular diretamente de `CONFIRMED` para `REPORTED` ou de `RESOLVED` para
`CONFIRMED`. Resultado esperado: HTTP 409 Conflict. Esse teste evidencia uma regra real
do domínio, não apenas uma atualização de coluna.

## Etapa 11 — Testes automatizados

Pare a aplicação principal com Ctrl+C (se estiver no mesmo terminal) e execute:

```powershell
mvn clean test
```

Os testes usam o perfil `test`, que troca apenas o banco por H2 em memória e executa as
mesmas migrations. Isso evita depender de Docker para o ciclo automatizado. O perfil
padrão continua apontando para PostgreSQL.

Abra `target/surefire-reports/` para conferir os relatórios XML/TXT produzidos pelo Maven.

## Etapa 12 — Demonstrar as migrations

A prova mais clara é usar uma base vazia em ambiente local. Atenção: o comando seguinte
apaga permanentemente os dados armazenados no volume local:

```powershell
docker compose down -v
docker compose up -d postgres
mvn spring-boot:run
```

No log de inicialização, procure as versões V1 e V2 aplicadas. No banco, consulte:

```sql
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

Esperado: duas versões bem-sucedidas, `1` e `2`.

## Etapa 13 — Preparar evidências

Capture telas reais do próprio computador da equipe:

1. Docker/Container PostgreSQL saudável.
2. Log do Flyway aplicando V1 e V2.
3. SQL mostrando `flyway_schema_history`.
4. Swagger UI com os sete endpoints.
5. POST de uma ocorrência com HTTP 201.
6. GET posterior mostrando a mesma ocorrência.
7. Tentativa de transição indevida com HTTP 409.
8. Console do Maven com os testes aprovados.

Organize as imagens em `evidencias/`. Não monte telas falsas no PowerPoint nem use prints
de outra aplicação como se fossem execução do ALERTASP.

## Etapa 14 — Publicar no GitHub

Na raiz do projeto:

```powershell
git init
git add .
git status
git commit -m "feat: implementa API SOA do ALERTASP"
```

Crie um repositório vazio no GitHub (sem README/licença inicial para evitar conflito) e
use a URL fornecida pelo GitHub:

```powershell
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/alertasp-soa.git
git push -u origin main
```

Antes de enviar, confira no navegador se README, `pom.xml`, `src`, `docs`, `prompts.md`
evidências estão acessíveis. Não publique `.env` ou senhas.

## Como explicar em uma apresentação técnica

1. Controller representa os recursos REST e traduz HTTP para chamadas de serviço.
2. DTO protege o contrato da API e impede expor diretamente entidades JPA.
3. Service centraliza regras e transações.
4. Repository usa Spring Data JPA para consultar e persistir.
5. Entity expressa o domínio e o relacionamento.
6. Flyway versiona o banco; Hibernate valida o schema, mas não o cria.
7. OpenAPI gera a documentação lendo controllers e anotações.
8. Testes provam as regras e o comportamento HTTP.

## Problemas comuns

- **Connection refused:** verifique `docker compose ps` e `docker compose logs postgres`.
- **Senha inválida:** confira `.env` e a variável `$env:DB_PASSWORD` no terminal da API.
- **Porta 5432 ocupada:** pare o PostgreSQL local que usa a porta ou altere a publicação
  da porta no Compose e ajuste `DB_URL`.
- **Porta 8080 ocupada:** execute `$env:SERVER_PORT = "8081"` e reinicie.
- **Flyway checksum mismatch:** não edite migration já aplicada; crie uma próxima versão.
- **Schema validation error:** confira se a migration foi aplicada e se entidade/coluna/tipo
  estão coerentes; não resolva trocando `ddl-auto` para `update`.
- **Swagger não abre:** confirme que a aplicação iniciou sem erro e use primeiro
  `/v3/api-docs` para separar problema de documentação de problema de interface.
