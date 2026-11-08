$ErrorActionPreference = 'Stop'

Write-Host '== Verificando Java =='
java -version

Write-Host '== Verificando Maven =='
mvn -version

Write-Host '== Verificando Docker =='
docker --version
docker compose version

Write-Host '== Subindo PostgreSQL =='
docker compose up -d postgres

Write-Host '== Aguardando saúde do banco =='
$healthy = $false
for ($i = 0; $i -lt 30; $i++) {
    $status = docker inspect --format='{{.State.Health.Status}}' alertasp-postgres 2>$null
    if ($status -eq 'healthy') { $healthy = $true; break }
    Start-Sleep -Seconds 2
}
if (-not $healthy) { throw 'PostgreSQL não ficou saudável. Verifique docker compose logs postgres.' }

Write-Host '== Executando testes =='
mvn clean test

Write-Host '== Verificação concluída. Inicie a API com: mvn spring-boot:run =='
