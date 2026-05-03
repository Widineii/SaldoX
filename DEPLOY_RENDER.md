# Deploy do SaldoX no Render

Este projeto ja possui `Dockerfile` e `render.yaml` para deploy no Render com PostgreSQL.

## Passo a passo

1. Acesse [dashboard.render.com](https://dashboard.render.com).
2. Clique em **New +**.
3. Escolha **Blueprint**.
4. Conecte o repositório:

```text
Widineii/SaldoX
```

5. Confirme o arquivo:

```text
render.yaml
```

6. Clique em **Apply** ou **Create Blueprint Instance**.

O Render deve criar automaticamente:

- Web Service `saldox`
- Banco PostgreSQL `saldox-db`
- Variáveis de ambiente para conexão com o banco
- `JWT_SECRET` gerado automaticamente

## Variáveis configuradas

```text
SPRING_PROFILES_ACTIVE=postgres
JWT_SECRET=gerado automaticamente
DATABASE_URL=connectionString do banco Render
DATABASE_USERNAME=usuario do banco Render
DATABASE_PASSWORD=senha do banco Render
```

## Observações

- O app usa `server.port=${PORT:8080}`, então funciona com a porta dinâmica do Render.
- O app converte automaticamente URLs `postgresql://...` do Render para o formato JDBC esperado pelo Spring.
- O usuário demo é criado na inicialização:

```text
Email: lucas@email.com
Senha: 123456
```

## Depois do deploy

Quando o Render finalizar o deploy, acesse a URL pública gerada, por exemplo:

```text
https://saldox.onrender.com
```

Em seguida, atualize o campo **Website** do repositório no GitHub com a URL pública.
