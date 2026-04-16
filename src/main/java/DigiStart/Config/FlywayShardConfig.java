package DigiStart.Config;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayShardConfig {

    private final DataSource shard2DataSource;

    public FlywayShardConfig(@Qualifier("shard2DataSource") DataSource shard2DataSource) {
        this.shard2DataSource = shard2DataSource;
    }

    @PostConstruct
    public void migrateShard2() {
        Flyway.configure()
                .dataSource(shard2DataSource)
                .locations("classpath:db/migration/shard2")
                .baselineOnMigrate(true)
                .baselineVersion("4")
                .load()
                .migrate();
    }
}
