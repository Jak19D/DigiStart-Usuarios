-- Script para criar os bancos de dados dos shards
-- Execute este script no PostgreSQL como superusuário

-- Criar banco de dados para o Shard 1 (IDs Pares)
CREATE DATABASE digistart_shard1;

-- Criar banco de dados para o Shard 2 (IDs Ímpares)
CREATE DATABASE digistart_shard2;

-- Conceder permissões para o usuário postgres (se necessário)
GRANT ALL PRIVILEGES ON DATABASE digistart_shard1 TO postgres;
GRANT ALL PRIVILEGES ON DATABASE digistart_shard2 TO postgres;

-- Verificar se os bancos foram criados
\l

-- Para testar, você pode se conectar a cada banco e criar a tabela de usuários:
-- \c digistart_shard1
-- CREATE TABLE tb_user (
--     id BIGSERIAL PRIMARY KEY,
--     nome VARCHAR(150) NOT NULL,
--     email VARCHAR(150) UNIQUE NOT NULL,
--     senha VARCHAR(100) NOT NULL,
--     tipo VARCHAR(50) NOT NULL,
--     ativo BOOLEAN DEFAULT true
-- );
--
-- \c digistart_shard2
-- CREATE TABLE tb_user (
--     id BIGSERIAL PRIMARY KEY,
--     nome VARCHAR(150) NOT NULL,
--     email VARCHAR(150) UNIQUE NOT NULL,
--     senha VARCHAR(100) NOT NULL,
--     tipo VARCHAR(50) NOT NULL,
--     ativo BOOLEAN DEFAULT true
-- );
