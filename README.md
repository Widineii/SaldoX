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
- Documentacao da API com **OpenAPI/Swagger**
- Exportacao de relatorios em **CSV**
- DTOs para entrada e saida de dados
- Tratamento centralizado de erros
- Ambiente local com **H2** e profile para **PostgreSQL**
- Estrutura com **Docker** e `render.yaml` para publicacao
- Testes de services, controllers e integracao com H2

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
- Exportacao CSV das transacoes filtradas
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

GET    /relatorios/csv?usuarioId=1
GET    /relatorios/csv?usuarioId=1&tipo=DESPESA&mes=2026-05
```

## Documentacao da API

Com o projeto rodando, acesse:

```text
http://localhost:8080/swagger.html
```

O arquivo OpenAPI tambem fica disponivel em:

```text
http://localhost:8080/openapi.yaml
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
- `netlify.toml`
- profile `postgres`

Guia completo:

- [Deploy no Render](DEPLOY_RENDER.md)

Tambem ha uma vitrine estatica para publicar no Netlify usando a pasta `site/`.

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

## Melhorias implementadas nesta fase

- Projeto preparado para publicacao online com Docker e Render Blueprint
- Documentacao Swagger/OpenAPI disponivel em `/swagger.html`
- Teste de integracao com banco H2 em memoria
- Mais cobertura nos controllers de auth, transacoes, dashboard e relatorios
- Exportacao de relatorios em CSV pelo backend

## Proximas melhorias

- Publicar a URL final em uma conta Render/Railway
- Adicionar envio real de email para recuperacao de senha
- Adicionar exportacao em PDF
- Criar pipeline de CI no GitHub Actions

## Autor

Desenvolvido por **Widinei Martins**.

- GitHub: [github.com/Widineii](https://github.com/Widineii)
- LinkedIn: [linkedin.com/in/widineimartinsdev](https://www.linkedin.com/in/widineimartinsdev)
- WhatsApp: [w.app/widineii](https://w.app/widineii)
