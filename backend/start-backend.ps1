$composeFile = Join-Path $PSScriptRoot "docker-compose.yml"

if (-not (Test-Path $composeFile)) {
    Write-Error "Arquivo docker-compose.yml não encontrado em $PSScriptRoot"
    exit 1
}

$dockerComposeCommand = Get-Command "docker-compose" -ErrorAction SilentlyContinue

if ($dockerComposeCommand) {
    & docker-compose -f $composeFile up -d
    exit $LASTEXITCODE
}

if (-not (Get-Command "docker" -ErrorAction SilentlyContinue)) {
    Write-Error "Docker não encontrado. Instale o Docker Desktop ou configure o comando docker no PATH."
    exit 1
}

& docker compose -f $composeFile up -d
exit $LASTEXITCODE
