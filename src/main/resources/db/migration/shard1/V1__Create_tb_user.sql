-- Criação da tabela tb_user para o Shard 1 (IDs Pares)
CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    ativo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para performance
CREATE INDEX idx_tb_user_email_shard1 ON tb_user(email);
CREATE INDEX idx_tb_user_tipo_shard1 ON tb_user(tipo);
CREATE INDEX idx_tb_user_ativo_shard1 ON tb_user(ativo);

-- Trigger para atualizar updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_tb_user_updated_at 
    BEFORE UPDATE ON tb_user 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Comentário para identificar o shard
COMMENT ON TABLE tb_user IS 'Tabela de usuários - Shard 1 (IDs Pares)';
