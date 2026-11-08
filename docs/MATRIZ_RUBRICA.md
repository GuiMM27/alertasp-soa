# Matriz de rastreabilidade da rubrica SOA (página 27 do briefing)

| Critério / peso oficial | Implementação no projeto | Evidência esperada |
|---|---|---|
| Estruturação do domínio — 3,0 | `entity/MonitoringPoint.java`, `entity/FloodOccurrence.java`, `enums/`, `dto/`, `service/` | Código, relacionamento N:1 em FloodOccurrence e testes de regras |
| Conexão com banco — 3,0 | `application.properties`, `docker-compose.yml`, `repository/` | PostgreSQL ativo; POST seguido de GET; dados persistidos após reiniciar a API |
| Migrações — 1,5 | `resources/db/migration/V1__...sql`, `V2__...sql` | Logs do Flyway e consulta à tabela `flyway_schema_history` |
| Documentação automática — 1,5 | `OpenApiConfiguration.java`, anotações nos controllers e dependência springdoc | Swagger UI renderizado e `/v3/api-docs` retornando JSON |
| Testes automatizados — 1,0 | `src/test/java/...` | `mvn clean test` com sucesso e relatório Surefire |

O briefing não informa nota parcial por subitem, quantidade mínima de testes ou framework
obrigatório. As classes e ferramentas acima são a decisão técnica do protótipo.
