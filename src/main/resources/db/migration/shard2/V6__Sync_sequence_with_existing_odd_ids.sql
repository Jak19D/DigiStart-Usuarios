-- Garante que o próximo ID gerado no shard2 (ímpares) não colida com dados existentes.
SELECT setval(
    'tb_user_id_seq',
    COALESCE((SELECT MAX(id) FROM tb_user WHERE id % 2 = 1), -1) + 2,
    false
);
