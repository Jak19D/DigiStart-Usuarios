-- Corrigir sequence para gerar apenas IDs ímpares no shard2
-- Remover identidade da coluna ID antes de modificar a sequence
ALTER TABLE tb_user ALTER COLUMN id DROP IDENTITY;
DROP SEQUENCE IF EXISTS tb_user_id_seq;
CREATE SEQUENCE tb_user_id_seq
    START WITH 1
    INCREMENT BY 2
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Reassociar a sequence à coluna ID da tabela
ALTER TABLE tb_user ALTER COLUMN id SET DEFAULT nextval('tb_user_id_seq');
