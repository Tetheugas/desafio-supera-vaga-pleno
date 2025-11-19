-- Tabela de usuários
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    departamento VARCHAR(100) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabela de módulos
CREATE TABLE modulos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao VARCHAR(500) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabela de departamentos permitidos por módulo
CREATE TABLE modulo_departamentos (
    modulo_id BIGINT NOT NULL,
    departamento VARCHAR(100) NOT NULL,
    PRIMARY KEY (modulo_id, departamento),
    FOREIGN KEY (modulo_id) REFERENCES modulos(id) ON DELETE CASCADE
);

-- Tabela de módulos incompatíveis
CREATE TABLE modulo_incompativeis (
    modulo_id BIGINT NOT NULL,
    modulo_incompativel_id BIGINT NOT NULL,
    PRIMARY KEY (modulo_id, modulo_incompativel_id),
    FOREIGN KEY (modulo_id) REFERENCES modulos(id) ON DELETE CASCADE
);

-- Tabela de módulos ativos do usuário
CREATE TABLE usuario_modulos (
    usuario_id BIGINT NOT NULL,
    modulo_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, modulo_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (modulo_id) REFERENCES modulos(id) ON DELETE CASCADE
);

-- Tabela de solicitações
CREATE TABLE solicitacoes (
    id BIGSERIAL PRIMARY KEY,
    protocolo VARCHAR(50) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    justificativa VARCHAR(500) NOT NULL,
    urgente BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL,
    motivo_negacao VARCHAR(500),
    motivo_cancelamento VARCHAR(200),
    data_solicitacao TIMESTAMP NOT NULL,
    data_expiracao TIMESTAMP,
    solicitacao_origem_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (solicitacao_origem_id) REFERENCES solicitacoes(id) ON DELETE SET NULL
);

-- Tabela de módulos da solicitação
CREATE TABLE solicitacao_modulos (
    solicitacao_id BIGINT NOT NULL,
    modulo_id BIGINT NOT NULL,
    PRIMARY KEY (solicitacao_id, modulo_id),
    FOREIGN KEY (solicitacao_id) REFERENCES solicitacoes(id) ON DELETE CASCADE,
    FOREIGN KEY (modulo_id) REFERENCES modulos(id) ON DELETE CASCADE
);

-- Tabela de histórico de solicitações
CREATE TABLE historico_solicitacoes (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_id BIGINT NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    FOREIGN KEY (solicitacao_id) REFERENCES solicitacoes(id) ON DELETE CASCADE
);

-- Tabela de refresh tokens
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_expiracao TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_solicitacoes_usuario ON solicitacoes(usuario_id);
CREATE INDEX idx_solicitacoes_status ON solicitacoes(status);
CREATE INDEX idx_solicitacoes_data ON solicitacoes(data_solicitacao);
CREATE INDEX idx_historico_solicitacao ON historico_solicitacoes(solicitacao_id);
CREATE INDEX idx_refresh_token ON refresh_tokens(token);
