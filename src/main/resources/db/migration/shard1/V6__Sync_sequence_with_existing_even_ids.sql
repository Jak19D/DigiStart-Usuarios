-- Garante que o próximo ID gerado no shard1 (pares) não colida com dados existentes.
SELECT setval(
    'tb_user_id_seq',
    COALESCE((SELECT MAX(id) FROM tb_user WHERE id % 2 = 0), 0) + 2,
    false
);
