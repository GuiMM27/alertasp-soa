-- Registros fictícios exclusivamente para demonstrar a API. Não representam alagamentos reais.
INSERT INTO monitoring_points (name, street, neighborhood, city, latitude, longitude, active)
VALUES
    ('DEMO - Marginal Pinheiros', 'Marginal Pinheiros', 'Cidade Jardim', 'São Paulo', -23.6100, -46.7000, TRUE),
    ('DEMO - Avenida 9 de Julho', 'Avenida Nove de Julho', 'Bela Vista', 'São Paulo', -23.5650, -46.6520, TRUE),
    ('DEMO - Rua da Consolação', 'Rua da Consolação', 'Consolação', 'São Paulo', -23.5540, -46.6600, TRUE);

INSERT INTO flood_occurrences (monitoring_point_id, description, severity, status, source, occurred_at, reported_at, updated_at)
VALUES
    (1, 'Registro demonstrativo de acúmulo de água para testar a consulta de ocorrências.', 'HIGH', 'REPORTED', 'DEMONSTRATION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Registro demonstrativo de atenção para testar filtros e paginação.', 'MEDIUM', 'CONFIRMED', 'DEMONSTRATION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Registro demonstrativo encerrado para testar o ciclo de status.', 'LOW', 'RESOLVED', 'DEMONSTRATION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
