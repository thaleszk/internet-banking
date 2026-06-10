# Backend - Como Executar

## Pré-requisitos

* Docker Desktop instalado
* Docker Desktop em execução

---

## Executando o backend

Abra um terminal na pasta `backend` e execute:

```powershell
.\start-backend.ps1
```

O script irá:

* Construir as imagens necessárias;
* Subir todos os containers;
* Inicializar o ambiente.

---

## Caso o PowerShell bloqueie a execução

Execute o comando abaixo apenas uma vez:

```powershell
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

Confirme com:

```text
Y
```

Depois execute novamente:

```powershell
.\start-backend.ps1
```

---

## Verificar se os containers estão rodando

```bash
docker ps
```

---

## Parar o ambiente

```bash
docker compose down
```

---

## Atualizar após alterações no código

```bash
docker compose up -d --build
```

Ou, para reconstruir apenas um serviço:

```bash
docker compose up -d --build <nome-do-servico>
```

Exemplo:

```bash
docker compose up -d --build microservice-auth
```
