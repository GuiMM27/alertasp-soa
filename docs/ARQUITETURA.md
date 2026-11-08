# Arquitetura mínima

## Estilo

Monólito modular em camadas. Um único processo Spring Boot expõe HTTP REST, aplica
regras de domínio e persiste os dados no PostgreSQL. Não há microserviços nem chamada
à API do CGE.

## Fluxo

1. `controller`: recebe HTTP, valida DTO e define status HTTP.
2. `service`: executa regras e coordena os repositórios.
3. `repository`: abstrai consultas e persistência usando Spring Data JPA.
4. `entity`: representa o domínio persistido.
5. `dto`: define contratos de entrada e saída; entidades JPA não são expostas.
6. `exception`: converte falhas esperadas em respostas HTTP consistentes.

## Entidades e invariantes

- `MonitoringPoint`: localização monitorada, coordenadas dentro dos intervalos válidos,
  nome/endereço/bairro/cidade obrigatórios e indicador de atividade.
- `FloodOccurrence`: pertence obrigatoriamente a um ponto, tem descrição, severidade,
  origem e datas; nasce como `REPORTED`.
- Transições de status são unidirecionais: `REPORTED -> CONFIRMED | RESOLVED`,
  `CONFIRMED -> IN_PROGRESS | RESOLVED`, `IN_PROGRESS -> RESOLVED`.
- `RESOLVED` é estado final. Tentativas inválidas retornam HTTP 409.
- Não há entidade `User`, login ou coleta de PII nesta entrega.

## Origem dos dados

A migration `V2` inclui três pontos e três ocorrências fictícias, todas com origem
`DEMONSTRATION`. Elas existem para permitir execução e demonstração reproduzíveis.
Não representam alagamentos reais nem dados fornecidos pelo CGE, Defesa Civil ou sensores.

## Persistência

Flyway é o único responsável por criar/alterar schema (`ddl-auto=validate`).
O PostgreSQL é o banco padrão. H2 aparece exclusivamente no perfil automatizado `test`.
