-- Inserir usuários (senha: password - BCrypt hash)
-- Hash BCrypt válido para senha "password"
INSERT INTO usuarios (email, senha, nome, departamento, ativo) VALUES
('ti@empresa.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'João Silva', 'TI', true),
('financeiro@empresa.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Maria Santos', 'Financeiro', true),
('rh@empresa.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Pedro Costa', 'RH', true),
('operacoes@empresa.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Ana Oliveira', 'Operações', true);

-- Inserir módulos
INSERT INTO modulos (nome, descricao, ativo) VALUES
('Portal do Colaborador', 'Portal de acesso geral para colaboradores', true),
('Relatórios Gerenciais', 'Acesso a relatórios e dashboards gerenciais', true),
('Gestão Financeira', 'Sistema de gestão financeira e contábil', true),
('Aprovador Financeiro', 'Perfil de aprovador de transações financeiras', true),
('Solicitante Financeiro', 'Perfil de solicitante de transações financeiras', true),
('Administrador RH', 'Administração completa do sistema de RH', true),
('Colaborador RH', 'Acesso básico ao sistema de RH', true),
('Gestão de Estoque', 'Sistema de controle de estoque', true),
('Compras', 'Sistema de gestão de compras e fornecedores', true),
('Auditoria', 'Acesso a logs e auditoria do sistema', true);

-- Configurar departamentos permitidos
-- Portal do Colaborador (todos)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(1, 'TI'), (1, 'Financeiro'), (1, 'RH'), (1, 'Operações'), (1, 'Outros');

-- Relatórios Gerenciais (todos)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(2, 'TI'), (2, 'Financeiro'), (2, 'RH'), (2, 'Operações'), (2, 'Outros');

-- Gestão Financeira (Financeiro, TI)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(3, 'Financeiro'), (3, 'TI');

-- Aprovador Financeiro (Financeiro, TI)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(4, 'Financeiro'), (4, 'TI');

-- Solicitante Financeiro (Financeiro, TI)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(5, 'Financeiro'), (5, 'TI');

-- Administrador RH (RH, TI)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(6, 'RH'), (6, 'TI');

-- Colaborador RH (RH, TI)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(7, 'RH'), (7, 'TI');

-- Gestão de Estoque (Operações, TI)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(8, 'Operações'), (8, 'TI');

-- Compras (Operações, TI)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(9, 'Operações'), (9, 'TI');

-- Auditoria (apenas TI)
INSERT INTO modulo_departamentos (modulo_id, departamento) VALUES
(10, 'TI');

-- Configurar módulos incompatíveis
-- Aprovador Financeiro (4) incompatível com Solicitante Financeiro (5)
INSERT INTO modulo_incompativeis (modulo_id, modulo_incompativel_id) VALUES
(4, 5), (5, 4);

-- Administrador RH (6) incompatível com Colaborador RH (7)
INSERT INTO modulo_incompativeis (modulo_id, modulo_incompativel_id) VALUES
(6, 7), (7, 6);
