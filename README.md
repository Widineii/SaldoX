# SaldoX

![Status](https://img.shields.io/badge/status-portfolio%20ready-16a34a)
![Java](https://img.shields.io/badge/Java-17+-f97316)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-22c55e)
![Database](https://img.shields.io/badge/database-H2%20%7C%20PostgreSQL-2563eb)
![Deploy](https://img.shields.io/badge/deploy-Docker%20%7C%20Railway-9333ea)
![CI/CD](https://img.shields.io/github/actions/workflow/status/Widineii/SaldoX/ci.yml?branch=main&label=CI%2FCD)

Dashboard financeiro pessoal desenvolvido com **Java, Spring Boot e interface web responsiva**.

O projeto simula uma aplicacao real para controle de receitas e despesas, com autenticacao, cadastro de transacoes, filtros, graficos, resumo financeiro e estrutura preparada para deploy.

![Preview do dashboard SaldoX](docs/images/saldox-preview.png)

## ðŸŽ¬ Demo ao Vivo

[![Deploy](https://img.shields.io/badge/Acessar%20Demo-Railway-0B47D9)](https://saldox.up.railway.app/)

**Credenciais demo:**
- Email: `lucas@email.com`
- Senha: `123456`

## Destaques do projeto

- API com **Spring Boot**, **Spring Security** e **JPA/Hibernate**
- Autenticacao com **JWT** e senhas criptografadas com **BCrypt**
- Cadastro, edicao, exclusao e filtros de transacoes
- Dashboard com saldo, receitas, despesas, total de transacoes e graficos
- Documentacao da API com **OpenAPI/Swagger**
- Exportacao de relatorios em **CSV** e **PDF**
- DTOs para entrada e saida de dados
- Tratamento centralizado de erros
- Ambiente local com **H2** e profile para **PostgreSQL**
- Estrutura com **Docker** e `render.yaml` para publicacao
- Pipeline **CI/CD** com GitHub Actions
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
- Exportacao PDF de relatorios
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
- iText (PDF Export)
- Maven
- HTML, CSS e JavaScript
- Chart.js
- Docker
- GitHub Actions

## Como rodar localmente

No Windows:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

Depois acesse:

```text
http://localhost:8080
```

Se a porta `8080` estiver ocupada:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--server.port=8081
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

Nas rotas autenticadas, envie o header `Authorization: Bearer <jwt>` obtido em login ou cadastro.

```text
POST   /auth/registrar
POST   /auth/login
POST   /auth/recuperar-senha
POST   /auth/redefinir-senha

GET    /transacoes
GET    /transacoes?busca=mercado&tipo=DESPESA&mes=2026-04
POST   /transacoes
GET    /transacoes/{id}
PUT    /transacoes/{id}
DELETE /transacoes/{id}

PUT    /auth/perfil
POST   /auth/avatar          (multipart, campo avatar)

GET    /dashboard/resumo

GET    /relatorios/csv
GET    /relatorios/csv?tipo=DESPESA&mes=2026-05
GET    /relatorios/pdf
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

O usuario Ã© definido pelo JWT; nÃ£o envie `usuarioId` no corpo.

```json
{
  "descricao": "Mercado",
  "valor": 320.45,
  "data": "2026-04-28",
  "categoria": "Alimentacao",
  "tipo": "DESPESA"
}
```

## Ferramentas de banco H2 em producao

- Com o profile **`postgres`** (por exemplo Render), `spring.h2.console.enabled` e `app.h2-viewer.enabled` ficam **desativados**, o console `/h2-console`, o viewer `/banco-h2.html` e a API `/banco-h2/**` retornam **403** mesmo autenticado.
- Ambiente **local/dev** mantem visualizacao e endpoint do viewer habilitados quando iniciado com `-Dspring-boot.run.profiles=local` ou `-Dspring-boot.run.profiles=dev`.

## Envio real de email (recuperacao de senha)

Configure SMTP e remetente. Exemplo (`application.properties` ou variÃ¡veis):

```text
SPRING_MAIL_HOST=smtp.seuprovedor.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=...
SPRING_MAIL_PASSWORD=...
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

MAIL_FROM=no-reply@seudominio.com
```

Tambem aceita `APP_MAIL_FROM` como variavel de ambiente.

Sem `spring.mail.host` e com `app.recuperacao.exibir-codigo-quando-sem-smtp=true` (padrao em ambiente H2 local), o codigo de recuperacao volta no campo `token` da resposta (apenas desenvolvimento). No profile **postgres**, essa opcao esta desligada: a resposta e sempre discreta e o codigo aparece somente nos **logs** do servidor (WARN) quando nao ha SMTP configurado â€” configure email em producao.

A solicitacao **nao revela se o email existe** quando o modo seguro esta ativo; a mensagem (`mensagem`) e neutra, evitando enumeracao de contas.

## Banco H2

Disponivel apenas quando a aplicacao e iniciada com profile local ou dev.

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

O repositorio inclui fluxo `.github/workflows/ci.yml` rodando `./mvnw test` em **push** e **pull_request** sobre `main`/`master`.

## Deploy

O projeto inclui:

- `Dockerfile`
- `render.yaml`
- `netlify.toml`
- profile `postgres`
- `.github/workflows/ci.yml` (CI/CD)

Guia completo:

- [Deploy no Render](DEPLOY_RENDER.md)

Tambem ha uma vitrine estatica para publicar no Netlify usando a pasta `site/`.

Para publicar, conecte o repositorio no Railway e configure as variaveis:

```text
SPRING_PROFILES_ACTIVE=postgres
DATABASE_URL=postgresql://user:password@host:5432/fintrack
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
- preparacao para deploy com CI/CD

## Melhorias implementadas nesta fase

- Corrigido login do usuario demo com senha em BCrypt
- Login migra senha legada para BCrypt automaticamente
- Implementada recuperacao de senha com codigo de redefinicao
- Adicionada seguranca: usuario extraido do contexto JWT
- Configurado PostgreSQL para producao em Railway
- Pipeline CI/CD com GitHub Actions
- Suporte a exportacao de PDF com iText
- Documentacao e README atualizados
- Usuario demo funcionando corretamente
- Projeto preparado para publicacao online com Docker e Render Blueprint
- Documentacao Swagger/OpenAPI disponivel em `/swagger.html`
- Teste de integracao com banco H2 em memoria
- Mais cobertura nos controllers de auth, transacoes, dashboard e relatorios
- Exportacao de relatorios em CSV pelo backend
- API protegida: rotas de dados usam apenas o usuario do **JWT**, sem `usuarioId` na URL
- Console H2 e viewer HTML da base desligados com seguranca por profile/propriedades quando nao usar H2
- Recuperacao de senha com **SMTP opcional** (mensagem discreta quando email esta configurado)
- CI com **GitHub Actions** (`./mvnw test`)

## Proximas melhorias

- Publicar a URL final em uma conta Render/Railway (com variaveis de email se quiser recuperacao real)
- Adicionar exportacao em PDF ou Excel
- Testes E2E com Selenium
- Adicionar paginacao nos endpoints de listagem
- Implementar cache com Redis

## Autor

Desenvolvido por **Widinei Martins**.

- GitHub: [github.com/Widineii](https://github.com/Widineii)
- LinkedIn: [linkedin.com/in/widineimartinsdev](https://www.linkedin.com/in/widineimartinsdev)
- WhatsApp: [w.app/widineii](https://w.app/widineii)

