# Sistema de Shard Database - DigiStart

## Visão Geral
Este projeto implementa um sistema de shard database com 2 shards para distribuir usuários baseados no ID:
- **Shard 1**: Armazena usuários com IDs pares
- **Shard 2**: Armazena usuários com IDs ímpares

## Configuração dos Bancos de Dados

### 1. Criar os Bancos de Dados
Execute o script `create_databases.sql` no PostgreSQL:
```bash
psql -U postgres -f create_databases.sql
```

### 2. Configuração das Conexões
Os shards estão configurados em `application.properties`:
- Shard 1: `digistart_shard1` (localhost:5432)
- Shard 2: `digistart_shard2` (localhost:5432)

## Arquivos da Implementação

### Configuração
- `DataSourceConfig.java`: Configuração das múltiplas datasources
- `JpaConfig.java`: Configuração JPA para o Shard 1
- `JpaConfigShard2.java`: Configuração JPA para o Shard 2

### Repositories
- `UserRepositoryShard1.java`: Repository para IDs pares
- `UserRepositoryShard2.java`: Repository para IDs ímpares

### Serviços
- `ShardRoutingService.java`: Lógica de roteamento entre shards
- `UserService.java`: Modificado para usar o sistema de sharding

## Funcionalidades

### Roteamento Automático
- **Novos usuários**: São salvos inicialmente no Shard 1, e se o ID gerado for ímpar, são movidos para o Shard 2
- **Consultas por ID**: O sistema roteia automaticamente para o shard correto baseado no ID
- **Consultas por email**: Busca em ambos os shards
- **Listagem completa**: Combina resultados de ambos os shards

### Verificações Implementadas
- Validação de email único em ambos os shards
- Verificação de existência de ID no shard correto
- Transações gerenciadas por shard
- Consistência na distribuição (pares/ímpares)

## Como Funciona

### Criação de Usuário
1. Usuário é salvo inicialmente no Shard 1
2. Sistema verifica o ID gerado
3. Se ID for ímpar, usuário é movido para o Shard 2
4. Se ID for par, usuário permanece no Shard 1

### Consultas
- **Por ID**: Direcionado ao shard correto baseado na paridade
- **Por Email**: Busca sequencial nos dois shards
- **Listagem**: Combinação de resultados de ambos os shards

## Testes

Para testar o sistema:
1. Inicie a aplicação: `mvn spring-boot:run`
2. Use o GraphQL endpoint em `http://localhost:8081/graphiql`
3. Crie múltiplos usuários e observe a distribuição
4. Verifique os bancos de dados diretamente para confirmar o sharding

## Benefícios
- **Escalabilidade**: Distribuição da carga entre múltiplos bancos
- **Performance**: Redução do volume de dados por consulta
- **Flexibilidade**: Sistema transparente para a aplicação
- **Consistência**: Garantia de distribuição correta dos dados
