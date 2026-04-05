# Migrações Flyway - Sistema de Shard Database

## Estrutura das Migrações

### 📁 Shard 1 (IDs Pares)
- **Localização:** `src/main/resources/db/migration/shard1/`
- **Database:** `digistart_shard1`
- **Função:** Armazenar usuários com IDs pares

### 📁 Shard 2 (IDs Ímpares)
- **Localização:** `src/main/resources/db/migration/shard2/`
- **Database:** `digistart_shard2`
- **Função:** Armazenar usuários com IDs ímpares

## Arquivos de Migração

### Shard 1
```
V1__Create_tb_user.sql          - Criação da tabela tb_user
V2__Insert_seed_data.sql        - Dados iniciais (se necessário)
```

### Shard 2
```
V1__Create_tb_user.sql          - Criação da tabela tb_user
V2__Insert_seed_data.sql        - Dados iniciais (se necessário)
```

## Configuração no application.properties

```properties
# Flyway principal
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# Flyway para Shard 1
spring.flyway.shard1.enabled=true
spring.flyway.shard1.locations=classpath:db/migration/shard1
spring.flyway.shard1.baseline-on-migrate=true

# Flyway para Shard 2
spring.flyway.shard2.enabled=true
spring.flyway.shard2.locations=classpath:db/migration/shard2
spring.flyway.shard2.baseline-on-migrate=true
```

## Estrutura da Tabela tb_user

```sql
CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,           -- ID auto-incremento
    nome VARCHAR(150) NOT NULL,         -- Nome do usuário
    email VARCHAR(150) UNIQUE NOT NULL, -- Email único
    senha VARCHAR(100) NOT NULL,       -- Senha criptografada
    tipo VARCHAR(50) NOT NULL,         -- Tipo (ALUNO, PROFESSOR, ADMIN)
    ativo BOOLEAN DEFAULT true,         -- Status do usuário
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Índices Criados

- `idx_tb_user_email_shardX` - Performance para busca por email
- `idx_tb_user_tipo_shardX` - Performance para filtro por tipo
- `idx_tb_user_ativo_shardX` - Performance para filtro por status

## Como Funciona

1. **Inicialização:** Flyway cria as tabelas em ambos os shards
2. **Criação de Usuário:** Application direciona para shard correto baseado no ID
3. **Migrações:** Cada shard tem suas próprias migrações independentes

## Próximas Migrações

Para adicionar novas migrações:

1. **Shard 1:** Criar `V3__NomeDaMigracao.sql` em `shard1/`
2. **Shard 2:** Criar `V3__NomeDaMigracao.sql` em `shard2/`
3. **Manter sincronia:** Ambos os shards devem ter a mesma estrutura

## Observações

- ✅ Flyway ativado para ambos os shards
- ✅ Migrações separadas por shard
- ✅ Índices otimizados para performance
- ✅ Triggers para atualização automática de timestamps
- ✅ Comentários para identificação dos shards
