# Índice das evidências — ALERTASP / SOA

Capturas de **execução local**, feitas em 08/11/2026 para a FIAP Global Solution 2026.2. Os locais e as ocorrências apresentados são **demonstrativos**, não registros reais nem dados de monitoramento em tempo real.

## Evidências da API REST

| Arquivo | Demonstração |
|---|---|
| [`01-get-pontos-parametros.jpg`](01-get-pontos-parametros.jpg) | Parâmetros usados no `GET /api/v1/monitoring-points`; complementa a imagem 02. |
| [`02-get-pontos-200.png`](02-get-pontos-200.png) | Listagem de pontos com HTTP `200 OK` e **3 registros**, antes de cadastrar o quarto ponto. |
| [`03-post-ponto-parametros.jpg`](03-post-ponto-parametros.jpg) | JSON enviado no cadastro de um ponto; complementa a imagem 04. |
| [`04-post-ponto-201.jpg`](04-post-ponto-201.jpg) | Cadastro do ponto DEMO — Avenida Paulista; HTTP `201 Created`, ID 4 e cabeçalho `Location`. |
| [`05-get-ponto-id4-200.jpg`](05-get-ponto-id4-200.jpg) | Consulta do ponto ID 4 com HTTP `200 OK`. |
| [`06-get-ponto-inexistente-404.jpg`](06-get-ponto-inexistente-404.jpg) | Consulta de identificador inexistente com HTTP `404 Not Found`. |
| [`07-post-ocorrencia-201.jpg`](07-post-ocorrencia-201.jpg) | Registro da ocorrência ID 4, vinculada ao ponto ID 4; HTTP `201 Created` e status inicial `REPORTED`. |
| [`08-get-ocorrencia-200.png`](08-get-ocorrencia-200.png) | Consulta da ocorrência ID 4 com HTTP `200 OK` e ponto associado. |
| [`11-patch-status-repetido-409.png`](11-patch-status-repetido-409.png) | Bloqueio da transição `CONFIRMED → CONFIRMED` com HTTP `409 Conflict`. |
| [`15-patch-valido-200.png.jpg`](15-patch-valido-200.png.jpg) | Captura de atualização válida de status com HTTP `200 OK`. |

## Evidências de banco, migrações, documentação e testes

| Arquivo | Demonstração |
|---|---|
| [`09-postgresql-persistencia.png`](09-postgresql-persistencia.png) | Consultas SQL exibindo **4 pontos** e **4 ocorrências** persistidos; ocorrência ID 4 em `CONFIRMED` no momento do registro. |
| [`10-flyway-v1-v2.jpg`](10-flyway-v1-v2.jpg) | Histórico `flyway_schema_history` com migrations V1 e V2 bem-sucedidas (`success = t`). |
| [`12-testes-automatizados.png.jpg`](12-testes-automatizados.png.jpg) | Captura do terminal com a execução dos testes automatizados; verificar o resumo e `BUILD SUCCESS` na imagem. |
| [`13-swagger-endpoints.png.jpg`](13-swagger-endpoints.png.jpg) | Visão geral do Swagger UI com os endpoints da API documentados. |
| [`14-docker-healthy.png.jpg`](14-docker-healthy.png.jpg) | Captura de `docker compose ps`, mostrando a execução do container PostgreSQL. |

## Correspondência com a avaliação de SOA

| Critério | Evidências principais |
|---|---|
| Modelagem de domínio e regras de negócio (3,0) | 07, 08, 11 e 15; código em `entity/`, `enums/` e `service/`. |
| Conexão com banco de dados (3,0) | 04, 05, 09 e 14. |
| Migrações com Flyway (1,5) | 10; scripts `V1` e `V2` no repositório. |
| Documentação automática Swagger/OpenAPI (1,5) | 13; endpoints documentados nos controllers. |
| Testes automatizados (1,0) | 12; testes disponíveis em `src/test/java/`. |

**Observações:** As capturas 01 e 03 mostram somente a preparação da requisição e devem ser lidas junto às imagens 02 e 04. A imagem 02 foi tirada **antes** do cadastro do quarto ponto, por isso mostra três registros. Os nomes `*.png.jpg` correspondem aos arquivos publicados no GitHub neste momento; se forem renomeados para `*.jpg`, atualize os links deste índice.
