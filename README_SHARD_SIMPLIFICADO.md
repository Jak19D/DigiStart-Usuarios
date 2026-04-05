# Sistema de Shard Database Simplificado - DigiStart

## Visão Geral
Implementação simplificada de shard database com 2 shards diretamente no UserService usando JdbcTemplate.

## Estrutura

### Configuração
- **ShardConfig.java**: Configuração simples das datasources e JdbcTemplate
- **application.properties**: Configuração dos dois bancos de dados

### Lógica de Sharding
- **UserService.java**: Contém toda a lógica de roteamento internamente
- Usa JdbcTemplate para operações SQL diretas
- Sem repositories duplicados
- Sem configs JPA duplicadas

## Funcionalidades

### Distribuição de Dados
- **Shard 1**: Usuários com IDs pares
- **Shard 2**: Usuários com IDs ímpares

### Operações
- **Criar Usuário**: Salva no Shard 1, move para Shard 2 se ID for ímpar
- **Buscar por ID**: Direcionado ao shard correto
- **Buscar por Email**: Busca em ambos os shards
- **Listar Todos**: Combina resultados de ambos os shards

### Métodos Implementados
- `salvar(User user)`: Salva com roteamento automático
- `findById(Long id)`: Busca no shard correto
- `existeEmail(String email)`: Verifica em ambos os shards
- `autenticarUsuario(email, senha)`: Autenticação cross-shard
- `listar()`: Combinação de todos os usuários

## Configuração dos Bancos

### application.properties
```properties
# Shard 1 - IDs Pares
spring.datasource.shard1.url=jdbc:postgresql://localhost:5432/digistart_shard1
spring.datasource.shard1.username=postgres
spring.datasource.shard1.password=123

# Shard 2 - IDs Ímpares
spring.datasource.shard2.url=jdbc:postgresql://localhost:5432/digistart_shard2
spring.datasource.shard2.username=postgres
spring.datasource.shard2.password=123
```

## Estrutura SQL
Crie a tabela `tb_user` em ambos os bancos:
```sql
CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    ativo BOOLEAN DEFAULT true
);
```

## Vantagens da Abordagem Simplificada

✅ **Menos Complexidade**: Sem múltiplos repositories
✅ **Performance**: JdbcTemplate direto sem overhead JPA
✅ **Manutenibilidade**: Toda lógica centralizada no UserService
✅ **Flexibilidade**: Fácil para testar e modificar
✅ **Controle Total**: SQL explícito e transparente

## Como Funciona

1. **Novo Usuário**: Insert no Shard 1 → Verifica ID → Move se necessário
2. **Consulta por ID**: Calcula paridade → Direciona ao shard correto
3. **Consulta por Email**: Busca sequencial nos dois shards
4. **Listagem**: Query paralela + merge dos resultados

O sistema está pronto para uso com configuração mínima e máxima simplicidade!
