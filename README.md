# SaldoX

![Status](https://img.shields.io/badge/status-portfolio%20ready-16a34a)
![Java](https://img.shields.io/badge/Java-17+-f97316)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-22c55e)
![Database](https://img.shields.io/badge/database-H2%20%7C%20PostgreSQL-2563eb)
![Deploy](https://img.shields.io/badge/deploy-Docker%20%7C%20Render-9333ea)

Dashboard financeiro pessoal desenvolvido com **Java, Spring Boot e interface web responsiva**.

O projeto simula uma aplicacao real para controle de receitas e despesas, com autenticacao, cadastro de transacoes, filtros, graficos, resumo financeiro e estrutura preparada para deploy.

![Preview do dashboard SaldoX](docs/images/saldox-preview.png)

## Destaques do projeto

- API com **Spring Boot**, **Spring Security** e **JPA/Hibernate**
- Autenticacao com **JWT** e senhas criptografadas com **BCrypt**
- Cadastro, edicao, exclusao e filtros de transacoes
- Dashboard com saldo, receitas, despesas, total de transacoes e graficos
- DTOs para entrada e saida de dados
- Tratamento centralizado de erros
- Ambiente local com **H2** e profile para **PostgreSQL**
- Estrutura com **Docker** e `render.yaml` para publicacao
- Testes de services e controllers

## Funcionalidades

- Cadastro e login de usuarios
- Recuperacao de senha por codigo de redefinicao
- Upload de avatar do usuario
- Cadastro de receitas e despesas
- Listagem, edicao e exclusao de transacoes
- Filtros por texto, tipo e mes
- Cards de resumo financeiro
- Grafico de categorias por despesa
- Grafico de resumo mensal com Chart.js
- Mensagens de sucesso e erro na tela

## Usuario demo

```text
Email: lucas@email.com
Senha: 123456
```

## Tecnologias

- Java 17+
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Bean Validation
- H2 Database
- PostgreSQL
- Maven
- HTML, CSS e JavaScript
- Chart.js
- Docker

## Como rodar localmente

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Depois acesse:

```text
http://localhost:8080
```

Se a porta `8080` estiver ocupada:

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

## Como apresentar em entrevista

Este projeto mostra capacidade de construir uma aplicacao web completa, passando por:

- modelagem de entidades e relacionamentos
- criacao de API REST
- autenticacao e seguranca
- persistencia com banco de dados
- consumo dos dados em uma interface web
- preparacao para deploy

## Proximas melhorias

- Publicar uma versao online para demonstracao
- Adicionar documentacao Swagger/OpenAPI
- Criar mais testes de integracao
- Melhorar cobertura de testes dos controllers
- Adicionar exportacao de relatorios em CSV ou PDF

## Autor

Desenvolvido por **Widinei Martins**.

- GitHub: [github.com/Widineii](https://github.com/Widineii)
- LinkedIn: [linkedin.com/in/widineimartinsdev](https://www.linkedin.com/in/widineimartinsdev)
- WhatsApp: [w.app/widineii](https://w.app/widineii)
