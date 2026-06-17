# Sistema Odonto - Backend

API REST para gestao de uma clinica odontologica, incluindo agenda de consultas,
cadastros, autenticacao JWT, relatorios, controle de estoque, notificacoes em
tempo real e upload de arquivos em consultas.

Projeto Final do Programa Trainee da Wise Systems.

## Sobre o Projeto

O backend do Sistema Odonto foi desenvolvido com Java 21 e Spring Boot 3.5.
A API e consumida por um frontend Angular e usa MySQL como banco relacional.

O sistema trabalha com dois perfis principais:

- `ADMIN`: gerencia usuarios e possui acesso amplo ao sistema.
- `DENTISTA`: acessa as rotas operacionais permitidas e visualiza apenas as
  consultas vinculadas ao seu usuario.

## Tecnologias

- Java 21
- Spring Boot 3.5.14
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security
- JWT com `jjwt`
- Refresh token com rotacao
- MySQL
- Bean Validation
- WebSocket / STOMP
- Lombok
- Maven Wrapper
- Docker / Docker Compose

## Funcionalidades Principais

- Login com JWT.
- Refresh token com rotacao, logout com revogacao e apenas um refresh token ativo por usuario.
- CRUD de usuarios com permissao exclusiva para `ADMIN`.
- CRUD de pacientes.
- CRUD de dentistas com vinculo a especialidades.
- CRUD de especialidades.
- CRUD de consultas.
- Cancelamento de consultas com motivo obrigatorio.
- Relatorios filtrados por paciente, dentista, usuario, especialidade, status e periodo.
- Dashboard de consultas.
- Finalizacao automatica de consultas vencidas.
- Controle de materiais.
- Movimentacoes de estoque: `ENTRADA`, `SAIDA` e `AJUSTE`.
- Dashboard de estoque.
- Painel administrativo com estatisticas e logs.
- Rota de perfil do usuario autenticado.
- Upload, listagem, download e exclusao de arquivos em consultas.
- Notificacoes em tempo real via WebSocket.
- Tratamento padronizado de erros.

## Regras de Negocio Atendidas

- Nao permite conflito de horario para o mesmo dentista.
- Nao permite agendamento em datas passadas.
- Horario final da consulta deve ser depois do horario inicial.
- Cancelamento exige motivo.
- Dentista pode ter varias especialidades.
- Especialidade pode pertencer a varios dentistas.
- Apenas `ADMIN` gerencia usuarios.
- `ADMIN` visualiza todas as consultas.
- `DENTISTA` visualiza apenas as consultas vinculadas ao seu usuario.
- Nao permite movimentacao de estoque com quantidade invalida.
- Nao permite saida de estoque acima da quantidade disponivel.

## Como Rodar com Docker

Esta e a forma recomendada para avaliacao, pois sobe banco MySQL e backend com
um unico comando.

Pre-requisitos:

- Docker Desktop instalado e em execucao.

Na raiz deste repositorio, execute:

```bash
docker compose up --build -d
```

Servicos expostos:

```text
Backend: http://127.0.0.1:8080
MySQL:   localhost:3306
Banco:   sistema_gestao_consultas
```

Para parar:

```bash
docker compose down
```

Para apagar os volumes e recriar banco/uploads do zero:

```bash
docker compose down -v
docker compose up --build -d
```

O Docker Compose usa:

- `mysql:8.0`
- `sistema-gestao-consultas-backend/Dockerfile`
- `src/main/resources/db/schema.sql`
- `src/main/resources/db/data.sql`
- volume `mysql_data` para persistencia do banco
- volume `backend_uploads` para arquivos enviados

## Usuarios de Teste

Os dados iniciais sao carregados pelo `data.sql` quando o banco Docker e criado
pela primeira vez.

```text
ADMIN
email: admin@odonto.com
senha: 123456

DENTISTA
email: dentista@odonto.com
senha: 123456
```

## Como Rodar Localmente sem Docker

Pre-requisitos:

- Java 21
- MySQL 8 ou MariaDB compativel
- Banco chamado `sistema_gestao_consultas`

Crie o banco:

```sql
CREATE DATABASE sistema_gestao_consultas;
```

Configure a chave JWT:

PowerShell:

```powershell
$env:JWT_SECRET="local-dev-secret-with-at-least-32-characters"
```

CMD:

```cmd
set JWT_SECRET=local-dev-secret-with-at-least-32-characters
```

Linux/macOS:

```bash
export JWT_SECRET="local-dev-secret-with-at-least-32-characters"
```

Execute:

```bash
cd sistema-gestao-consultas-backend
./mvnw spring-boot:run
```

No Windows:

```cmd
cd sistema-gestao-consultas-backend
mvnw.cmd spring-boot:run
```

## Variaveis de Ambiente

| Variavel | Descricao | Padrao |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | URL JDBC do MySQL | `jdbc:mysql://127.0.0.1:3306/sistema_gestao_consultas` |
| `SPRING_DATASOURCE_USERNAME` | Usuario do banco | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | vazio |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia Hibernate | `update` |
| `JWT_SECRET` | Chave usada para assinar JWT | obrigatoria |
| `JWT_EXPIRATION_MS` | Duracao do access token | `86400000` |
| `JWT_REFRESH_EXPIRATION_MS` | Duracao do refresh token | `604800000` |
| `UPLOAD_DIR` | Pasta de uploads | `uploads` |
| `MAX_FILE_SIZE` | Tamanho maximo de arquivo | `10MB` |
| `MAX_REQUEST_SIZE` | Tamanho maximo da requisicao multipart | `10MB` |
| `SERVER_PORT` | Porta da API | `8080` |

## Scripts SQL

Os scripts versionados ficam em:

```text
sistema-gestao-consultas-backend/src/main/resources/db/schema.sql
sistema-gestao-consultas-backend/src/main/resources/db/data.sql
```

Eles criam:

- tabelas obrigatorias do enunciado;
- tabelas extras de estoque, logs, anexos e refresh token;
- usuarios iniciais;
- pacientes, dentistas, especialidades, consultas e materiais de exemplo.

## Autenticacao

### Login

```http
POST /auth/login
```

Body:

```json
{
  "email": "admin@odonto.com",
  "senha": "123456"
}
```

Resposta:

```json
{
  "id": 1,
  "nome": "Admin Odonto",
  "email": "admin@odonto.com",
  "perfil": "ADMIN",
  "token": "access-token",
  "accessToken": "access-token",
  "refreshToken": "refresh-token",
  "tipoToken": "Bearer",
  "expiresInMs": 86400000,
  "refreshExpiresInMs": 604800000
}
```

Use o access token nas rotas protegidas:

```http
Authorization: Bearer <accessToken>
```

### Renovar Token

```http
POST /auth/refresh
```

Body:

```json
{
  "refreshToken": "refresh-token-atual"
}
```

O refresh token usa rotacao: ao renovar, o token antigo e revogado e um novo e
gerado. Ao fazer login novamente com o mesmo usuario, refresh tokens ativos
anteriores tambem sao revogados, mantendo apenas uma sessao renovavel por vez.

### Logout

```http
POST /auth/logout
```

Body:

```json
{
  "refreshToken": "refresh-token-atual"
}
```

Resposta:

```http
204 No Content
```

## Principais Endpoints

Todas as rotas abaixo exigem `Authorization: Bearer <accessToken>`, exceto
`/auth/login`, `/auth/refresh` e `/auth/logout`.

### Auth

| Metodo | Rota | Descricao | Acesso |
| --- | --- | --- | --- |
| POST | `/auth/login` | Login e geracao de tokens | Publico |
| POST | `/auth/refresh` | Renova access token usando refresh token | Publico |
| POST | `/auth/logout` | Revoga refresh token | Publico |
| POST | `/auth/register` | Cadastra usuario | ADMIN |

### Usuarios

| Metodo | Rota | Descricao | Acesso |
| --- | --- | --- | --- |
| GET | `/usuarios` | Lista usuarios | ADMIN |
| GET | `/usuarios/{id}` | Busca usuario | ADMIN |
| POST | `/usuarios` | Cria usuario | ADMIN |
| PUT | `/usuarios/{id}` | Atualiza usuario | ADMIN |
| DELETE | `/usuarios/{id}` | Remove usuario | ADMIN |

### Perfil

| Metodo | Rota | Descricao | Acesso |
| --- | --- | --- | --- |
| GET | `/perfil` | Dados do usuario autenticado | ADMIN/DENTISTA |

### Consultas

| Metodo | Rota | Descricao | Acesso |
| --- | --- | --- | --- |
| GET | `/consultas` | Lista consultas | ADMIN/DENTISTA |
| GET | `/consultas/{id}` | Busca consulta | ADMIN/DENTISTA |
| POST | `/consultas` | Cria consulta | ADMIN/DENTISTA |
| PUT | `/consultas/{id}` | Atualiza consulta | ADMIN/DENTISTA |
| PATCH | `/consultas/{id}/cancelar` | Cancela consulta | ADMIN/DENTISTA |
| DELETE | `/consultas/{id}` | Remove consulta | ADMIN/DENTISTA |
| GET | `/consultas/dashboard` | Dashboard de consultas | ADMIN/DENTISTA |
| GET | `/consultas/relatorio` | Relatorio filtrado | ADMIN/DENTISTA |

### Arquivos de Consulta

| Metodo | Rota | Descricao | Acesso |
| --- | --- | --- | --- |
| POST | `/consultas/{consultaId}/arquivos` | Upload multipart de PDF/PNG/JPG | ADMIN/DENTISTA |
| GET | `/consultas/{consultaId}/arquivos` | Lista anexos da consulta | ADMIN/DENTISTA |
| GET | `/consultas/arquivos/{arquivoId}/download` | Download de anexo | ADMIN/DENTISTA |
| DELETE | `/consultas/arquivos/{arquivoId}` | Remove anexo | ADMIN/DENTISTA |

### Pacientes, Dentistas e Especialidades

| Recurso | Rotas | Acesso |
| --- | --- | --- |
| Pacientes | `GET/POST /pacientes`, `GET/PUT/DELETE /pacientes/{id}` | ADMIN/DENTISTA |
| Dentistas | `GET/POST /dentistas`, `GET/PUT/DELETE /dentistas/{id}` | ADMIN/DENTISTA |
| Especialidades | `GET/POST /especialidades`, `GET/PUT/DELETE /especialidades/{id}` | ADMIN/DENTISTA |

### Materiais e Estoque

| Metodo | Rota | Descricao | Acesso |
| --- | --- | --- | --- |
| GET | `/materiais` | Lista materiais com filtros | ADMIN/DENTISTA |
| GET | `/materiais/{id}` | Busca material | ADMIN/DENTISTA |
| POST | `/materiais` | Cria material | ADMIN/DENTISTA |
| PUT | `/materiais/{id}` | Atualiza material | ADMIN/DENTISTA |
| PATCH | `/materiais/{id}/ativar` | Ativa material | ADMIN/DENTISTA |
| PATCH | `/materiais/{id}/inativar` | Inativa material | ADMIN/DENTISTA |
| GET | `/estoque/dashboard` | Dashboard de estoque | ADMIN/DENTISTA |
| GET | `/estoque/movimentacoes` | Lista movimentacoes | ADMIN/DENTISTA |
| POST | `/estoque/movimentacoes` | Registra movimentacao | ADMIN/DENTISTA |

### Administracao

| Metodo | Rota | Descricao | Acesso |
| --- | --- | --- | --- |
| GET | `/admin/dashboard` | Estatisticas administrativas | ADMIN |
| GET | `/admin/logs` | Logs de atividade | ADMIN |

## Upload de Arquivos

Upload em consulta:

```http
POST /consultas/{consultaId}/arquivos
Content-Type: multipart/form-data
```

Campo:

```text
arquivo
```

Formatos aceitos:

- PDF
- PNG
- JPG/JPEG

Limite:

- 10MB por arquivo.

Os arquivos sao salvos fisicamente na pasta configurada por `UPLOAD_DIR` e os
metadados ficam na tabela `arquivos_consulta`.

## WebSocket

Endpoint:

```text
/ws
```

Topico de notificacoes:

```text
/topic/notificacoes
```

Eventos de dominio sao publicados pelo backend em operacoes como consultas,
usuarios, estoque e upload de arquivos.

## Tratamento de Erros

As respostas de erro seguem o formato:

```json
{
  "timestamp": "2026-06-16T10:30:00",
  "status": 409,
  "erro": "Conflito de regra de negocio",
  "mensagem": "Conflito de horario para o dentista informado",
  "path": "/consultas",
  "detalhes": []
}
```

## Testes

Execute:

```bash
cd sistema-gestao-consultas-backend
./mvnw test
```

No Windows:

```cmd
cd sistema-gestao-consultas-backend
mvnw.cmd test
```

Os testes usam H2 em memoria, configurado em `src/test/resources/application.properties`.

## Estrutura do Projeto

```text
Sistemaodontoback/
├── docker-compose.yml
├── README.md
└── sistema-gestao-consultas-backend/
    ├── Dockerfile
    ├── pom.xml
    ├── src/main/java/com/wise/sistema_gestao_consultas_backend/
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── enums/
    │   ├── exception/
    │   ├── repository/
    │   ├── scheduler/
    │   ├── security/
    │   ├── service/
    │   └── validation/
    └── src/main/resources/
        ├── application.properties
        └── db/
            ├── schema.sql
            └── data.sql
```

## Extras Implementados

- Graficos e dashboards.
- Painel administrativo com estatisticas e logs.
- Controle de estoque de materiais.
- Upload de arquivos em consultas.
- Notificacoes em tempo real via WebSocket.
- Refresh token com rotacao e sessao unica renovavel por usuario.
- Finalizacao automatica de consultas vencidas.
- Docker para backend e banco.

## Observacoes para Avaliacao

- O frontend deve apontar para `http://127.0.0.1:8080` quando o backend estiver
  rodando localmente ou via Docker.
- Em alguns ambientes Windows/Docker, `localhost` pode resolver diferente de
  `127.0.0.1`. Se uma chamada para `localhost:8080` travar, use
  `127.0.0.1:8080`.
- Se usar Docker, os dados iniciais sao criados somente na primeira subida do
  volume MySQL. Para recriar, use `docker compose down -v`.
- A pasta de uploads nao e versionada. Em Docker, ela fica no volume
  `backend_uploads`.
