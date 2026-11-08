# Critérios de aceite da API

- [ ] Banco PostgreSQL inicia com `docker compose up -d postgres`.
- [ ] Em banco vazio, a aplicação aplica Flyway V1 e V2 sem intervenção manual.
- [ ] Hibernate valida o schema (`ddl-auto=validate`), sem criar tabelas automaticamente.
- [ ] GET de pontos retorna os três registros de demonstração e metadados de paginação.
- [ ] POST de ocorrência para um ponto ativo retorna 201 e status REPORTED.
- [ ] POST com ponto inexistente retorna 404.
- [ ] POST com entrada inválida retorna 400.
- [ ] PATCH permite uma transição prevista e bloqueia transição inválida com 409.
- [ ] GET de ocorrências aceita filtros por status, bairro e intervalo de datas.
- [ ] Swagger UI lista todos os endpoints.
- [ ] `mvn clean test` termina com sucesso.

## Evidência por critério FIAP

| Critério | Evidência objetiva |
|---|---|
| Domínio (3,0) | Entidades, vínculo JPA, DTOs, enums, validações e testes de transição |
| Banco (3,0) | PostgreSQL em execução; inserir ocorrência via API e consultá-la depois |
| Migrações (1,5) | Logs do Flyway e linhas V1/V2 em `flyway_schema_history` |
| Documentação (1,5) | Swagger UI e `/v3/api-docs` acessíveis |
| Testes (1,0) | Relatório Surefire em `target/surefire-reports/` |
