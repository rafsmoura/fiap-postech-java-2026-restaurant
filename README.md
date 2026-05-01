# Restaurant Management API

**Tech Challenge — Fase 1 | Disciplinas integradas**

Backend REST para gestão de usuários (donos de restaurante e clientes) em um sistema compartilhado entre estabelecimentos, conforme especificação do desafio.

---

## Objetivo do trabalho

Entregar um **backend robusto em Spring Boot**, com persistência relacional, API versionada, tratamento de erros padronizado (**RFC 7807 / ProblemDetail**), documentação **Swagger/OpenAPI** e execução via **Docker Compose**, alinhado aos critérios de avaliação da fase.

---

## Escopo funcional

- Cadastro, consulta, atualização de perfil e exclusão de usuários.
- Dois tipos obrigatórios: **dono de restaurante** (`RESTAURANT_OWNER`) e **cliente** (`CLIENT`), com herança JPA em tabela única.
- **Troca de senha** em `PUT /api/v1/users/password` com **query** (`id`, `email`, `name` ou `nameContains`) + corpo JSON.
- **Demais dados** em `PUT /api/v1/users/update` com os mesmos parâmetros de localização (sem alterar senha nesse fluxo).
- Registro de **data da última alteração**; **e-mail e login únicos**; **busca por nome**; endereço estruturado (rua, número, cidade, CEP).
- **Validação de login** (`POST /api/v1/auth/login`) com verificação no banco e senha com **BCrypt** (Spring Security Crypto; JWT não obrigatório nesta fase).

---

## Arquitetura (resumo)

Camadas: **Web** (controllers MVC) → **Serviço** (regras e mapeamento DTO) → **Repositório** (Spring Data JPA) → **Domínio** (entidades). Erros centralizados em `GlobalExceptionHandler`. Contratos da API em **`/api/v1`**.

---

## Tecnologias

| Item | Versão / detalhe |
|------|------------------|
| Java | 21 |
| Spring Boot | 3.2 |
| Banco | MySQL 8 |
| API docs | springdoc-openapi (Swagger UI) |
| Build / container | Maven; Docker multi-stage |

---

## Como executar

### Com Docker Compose (recomendado)

Na pasta do projeto:

```bash
docker compose up --build
```

- API: **http://localhost:8080** (raiz redireciona ao Swagger).
- Se a porta **3306** já estiver em uso no computador (MySQL local), use por exemplo:  
  `DB_PORT=3307 docker compose up --build` (PowerShell: `$env:DB_PORT="3307"` antes do comando).

Variáveis úteis: `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `MYSQL_ROOT_PASSWORD`, `DB_PORT`, `APP_PORT`.

### Sem Docker

Exige **JDK 21**, **Maven** e **MySQL 8** acessível. Configure `src/main/resources/application.yml` ou variáveis `DB_*`. Para credenciais apenas na sua máquina, use o perfil **`local`**: copie `application-local.example.yml` para `application-local.yml` (arquivo ignorado pelo Git) e execute com `SPRING_PROFILES_ACTIVE=local`.

```bash
mvn spring-boot:run
```

---

## Documentação da API (Swagger)

Com a aplicação em execução:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html  
- **OpenAPI JSON:** http://localhost:8080/api-docs  

---

## Endpoints principais (`v1`)

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/api/v1/users` | Cadastro |
| `GET` | `/api/v1/users` | Lista; filtros (**no máximo um**): `id`, `name`, `email`, `search` (nome **ou** e-mail) |
| `GET` | `/api/v1/users/lookup` | **Um** usuário: exatamente um entre `id`, `email`, `name` (exato) ou `nameContains` (trecho) |
| `PUT` | `/api/v1/users/update` + query | Atualiza perfil: exatamente um entre `id`, `email`, `name`, `nameContains` (use `GET /lookup` antes para conferir) |
| `PUT` | `/api/v1/users/password` + query | Troca senha: mesmos quatro parâmetros de localização + corpo JSON |
| `DELETE` | `/api/v1/users` + query | Exclui: exatamente um entre `id`, `email`, `name`, `nameContains` |
| `POST` | `/api/v1/auth/login` | Valida login e senha |

---

## Modelo de dados (visão geral)

Tabela **`users`**: estratégia `SINGLE_TABLE`, discriminador **`dtype`** (`RESTAURANT_OWNER` ou `CLIENT`). Campos: nome, e-mail, login, hash de senha, `last_modified_at`, endereço embutido.

---

## Testes com Postman

Importar a coleção **`postman/Restaurant-Management.postman_collection.json`**. Definir `baseUrl` (ex.: `http://localhost:8080`). Executar primeiro o cenário de cadastro válido para preencher `userId` onde aplicável.

---

## Estrutura do repositório

```
.
├── restaurant-management/
│   ├── src/main/java/...     # Código-fonte
│   ├── src/main/resources/   # application.yml
│   ├── postman/              # Coleção JSON
│   ├── docker-compose.yml
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md             # Detalhes da API (se existir na pasta do módulo)
├── docs/
│   ├── evidencias.html       # Galeria de imagens + botão do relatório (PDF)
│   ├── images/               # Prints (Swagger, Postman, modelo, arquitetura, Docker)
│   └── relatorio/          # relatorio-tecnico.pdf (você adiciona)
└── README.md                 # Visão geral (este arquivo)
```

---

## Evidências e relatório

está colocado na pagina docs
---

*Projeto desenvolvido no contexto do Tech Challenge da fase.*
