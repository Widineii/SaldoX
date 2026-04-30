# SaldoX

Dashboard financeiro pessoal feito com Java, Spring Boot e uma interface web responsiva.

![Preview do dashboard SaldoX](docs/images/saldox-preview.png)

## Funcionalidades

- Cadastro e login de usuarios
- Senhas criptografadas com BCrypt
- Cadastro de receitas e despesas
- Listagem, edicao e exclusao de transacoes
- Filtros no backend por texto, tipo e mes
- Cards com saldo, receitas, despesas e total de transacoes
- Grafico de categorias por despesa
- Grafico de resumo mensal
- Mensagens de sucesso e erro na tela
- Tratamento centralizado de erros na API
- DTOs para entrada e saida de dados
- Configuracao local com H2 e profile para PostgreSQL

## Usuario demo

```text
Email: lucas@email.com
Senha: 123456
```

## Tecnologias

- Java 17+
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Bean Validation
- H2 Database
- PostgreSQL
- Maven
- HTML, CSS e JavaScript
- Docker

## Como rodar localmente

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Depois abra:

```text
http://localhost:8080
```

Se a porta 8080 estiver ocupada:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Rodar com PostgreSQL

Crie um banco chamado `fintrack` e rode:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

Variaveis aceitas:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/fintrack
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
```

## Endpoints principais

```text
POST   /auth/registrar
POST   /auth/login

GET    /transacoes?usuarioId=1
GET    /transacoes?usuarioId=1&busca=mercado&tipo=DESPESA&mes=2026-04
POST   /transacoes
GET    /transacoes/{id}?usuarioId=1
PUT    /transacoes/{id}
DELETE /transacoes/{id}?usuarioId=1

GET    /dashboard/resumo?usuarioId=1
```

## Exemplo de transacao

```json
{
  "descricao": "Mercado",
  "valor": 320.45,
  "data": "2026-04-28",
  "categoria": "Alimentacao",
  "tipo": "DESPESA",
  "usuarioId": 1
}
```

## Banco H2

Console:

```text
http://localhost:8080/h2-console
```

Configuracao:

```text
JDBC URL: jdbc:h2:file:./data/fintrack
User: sa
Password: vazio
```

## Testes

```bash
mvnw.cmd test
```

## Deploy

O projeto inclui:

- `Dockerfile`
- `render.yaml`
- profile `postgres`

Para publicar, conecte o repositorio no Render/Railway e configure as variaveis:

```text
SPRING_PROFILES_ACTIVE=postgres
DATABASE_URL=jdbc:postgresql://host:5432/fintrack
DATABASE_USERNAME=usuario
DATABASE_PASSWORD=senha
```

## Proximas melhorias

- Autenticacao com JWT
- Recuperacao de senha
- Upload de avatar
- Graficos com Chart.js ou Recharts
- Mais testes de controller
