# Índice das evidências — ALERTASP / SOA

Capturas **reais** executadas localmente em 08/11/2026. Não representam informações de alagamentos em tempo real: os pontos e ocorrências são dados demonstrativos.

| Arquivo | Demonstração | Resultado | Observação |
|---|---|---|---|
| `01-get-pontos-parametros.jpg` | GET `/api/v1/monitoring-points` | Preparação | Complementa `02` (parâmetros preenchidos, sem resposta) |
| `02-get-pontos-200.png` | GET listagem de pontos | 200 OK; 3 registros | Capturado **antes** do POST que cadastrou o 4º ponto |
| `03-post-ponto-parametros.jpg` | POST `/api/v1/monitoring-points` | Preparação | Complementa `04` (payload enviado) |
| `04-post-ponto-201.jpg` | POST de ponto (DEMO — Avenida Paulista) | 201 Created; ID 4 | Demonstra criação de recurso e campo `Location` |
| `05-get-ponto-id4-200.jpg` | GET `/api/v1/monitoring-points/4` | 200 OK | Retorno do registro cadastrado |
| `06-get-ponto-inexistente-404.jpg` | GET de ponto inexistente | 404 Not Found | Tratamento correto do erro |
| `07-post-ocorrencia-201.jpg` | POST `/api/v1/flood-occurrences` | 201 Created | Ocorrência ID 4; status inicial `REPORTED` |
| `08-get-ocorrencia-200.png` | GET `/api/v1/flood-occurrences/4` | 200 OK | Consulta da ocorrência e do ponto relacionado |
| `09-postgresql-persistencia.png` | Consultas SQL às tabelas | 4 pontos; 4 ocorrências | Ocorrência ID 4 persistida com `CONFIRMED` |
| `10-flyway-v1-v2.jpg` | `flyway_schema_history` | V1 e V2 com `success = t` | Comprova registro de migrations |
| `11-patch-status-repetido-409.png` | PATCH de status repetido | 409 Conflict | Captura já enviada anteriormente: `CONFIRMED → CONFIRMED` inválida |

## Evidências que ainda precisam ser capturadas

- `12-testes-automatizados.png`: terminal com `mvn.cmd clean test`, **10 testes**, `BUILD SUCCESS`.
- `13-swagger-visao-geral.png`: Swagger UI com os sete endpoints visíveis.
- `14-docker-postgres-healthy.png`: `docker compose ps` com container `healthy`.
- Opcional, mas recomendada: `15-patch-status-atualizado-200.png` com um PATCH válido para `IN_PROGRESS` (na ocorrência ID 4, atualmente `CONFIRMED`) e retorno `200`.
- Opcional: `16-patch-transicao-invalida-409.png` com `IN_PROGRESS → REPORTED` proibido ou outro caso de transição inválida.

## Como publicar

Copie os arquivos para a pasta `evidencias/` na raiz do repositório e execute:

```bash
git add evidencias/
git commit -m "docs: adiciona evidencias reais de execucao da API"
git push
```

As imagens `01` e `03` mostram **preparação** (não provam o funcionamento isoladamente); as imagens `02` e `04` mostram o **resultado real**. São pares complementares, não duplicatas idênticas.
