CREATE TABLE professores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(20),
    curriculo_path VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

CREATE INDEX idx_professores_user_id ON professores(user_id);
CREATE INDEX idx_professores_nome ON professores(nome);
