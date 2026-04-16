package DigiStart.Service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ShardRoutingService {
    
    private final AtomicLong counter = new AtomicLong(0);
    
    public int determinarShard() {
        return (int) (counter.incrementAndGet() % 2) + 1;
    }
    
    public int determinarShardPorHash(String valor) {
        if (valor == null) {
            return determinarShard();
        }
        // Usando a mesma regra: hash PAR → shard 1, hash ÍMPAR → shard 2
        int hash = valor.hashCode() & Integer.MAX_VALUE;
        return (hash % 2 == 0) ? 1 : 2;
    }
    
    public int determinarShardPorId(Long id) {
        if (id == null) {
            return determinarShard();
        }
        return (id % 2 == 0) ? 1 : 2;
    }
}
