-- Usuário demo para testes
-- Email: lucas@email.com
-- Senha: 123456 (hash BCrypt)
INSERT INTO usuario (nome, email, senha, avatar_url, reset_token, reset_token_expiracao) VALUES
('Lucas Demo', 'lucas@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NULL, NULL, NULL);
