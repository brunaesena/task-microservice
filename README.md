# Task Microservice - Gerenciamento de Tarefas

Este repositório contém o microsserviço de **Tarefas**, parte de um sistema distribuído com arquitetura de microserviços. Ele é responsável pelo CRUD completo de tarefas associadas a usuários, utilizando Java Spring Boot e banco de dados PostgreSQL.

---

## 📁 Estrutura Relacionada

Este projeto depende de outros repositórios/pastas no mesmo nível:

```
root/
├── frontend-angular-desafio   # Frontend Angular
├── infra                      # Infraestrutura Docker (banco de dados, containers)
├── task-microservice          # Microsserviço de tarefas (Spring Boot)
└── user-microservice          # Microsserviço de usuários (Spring Boot)
```

---

## 🚀 Como rodar o projeto localmente

### 1. Clone também o repositório `infra`

O microsserviço depende do ambiente de infraestrutura definido em outro repositório chamado `infra`. Ele deve estar no mesmo nível de pastas:

```bash
git clone https://github.com/brunaesena/task-microservice
git clone https://github.com/brunaesena/infra
```

### 2. Suba os containers com Docker Compose

Dentro da pasta `infra`, execute:

```bash
docker-compose up --build
```

Isso irá subir:
- Container `task-service` a partir da pasta `../task-microservice`
- Container `user-service`
- Banco de dados PostgreSQL

### 3. Acesse o microsserviço de tarefas

- Endpoint base: http://localhost:8082/api/tasks

---

## 📦 Tecnologias Utilizadas

- Java 17 + Spring Boot
- PostgreSQL
- Docker e Docker Compose
- JUnit 5
- Testcontainers

---

## 🧪 Rodando os Testes

O projeto possui testes com cobertura de:

- Camada de serviço (`TaskService`)
- Camada de controller (`TaskController`)
- Integração com banco real via Testcontainers ou PostgreSQL Docker

Para rodar os testes de integração:

```bash
# Dentro da pasta task-microservice
chmod +x test-run.sh
./test-run.sh
```

Este script executa:
- Subida do banco PostgreSQL de testes
- Execução dos testes com o perfil `test`
- Finalização e remoção do container

---

## 📋 Objetivo

O `task-microservice` foi desenvolvido para:

- Gerenciar tarefas com atributos como título, descrição, status, datas e associação a um usuário.
- Validar integrações com o microsserviço de usuários.
- Manter arquitetura limpa e testável com foco em boas práticas de microsserviços.

---

## 📃 Documentação - Swagger

- A documentação dos endpoints está disponível em `task-microservice-openapi.yaml` e pode ser visualizada com https://editor.swagger.io/
  
---

## 📌 Notas Finais

- Certifique-se de que as portas 8081, 8082, 5432 e 5433 estão livres no seu ambiente local.
- Esse microsserviço se comunica com o `user-microservice`, portanto é recomendado subir ambos via Docker.
- O frontend está configurado para consumir os microsserviços via HTTP.
    
---
