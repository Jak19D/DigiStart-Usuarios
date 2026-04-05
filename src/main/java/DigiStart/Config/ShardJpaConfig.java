package DigiStart.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ShardJpaConfig {

    @Primary
    @Bean(name = "shard1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.shard1")
    public DataSource shard1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "shard2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.shard2")
    public DataSource shard2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "shard1EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shard1EntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("shard1DataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("DigiStart.Model")
                .persistenceUnit("shard1")
                .build();
    }

    @Bean(name = "shard2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shard2EntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("shard2DataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("DigiStart.Model")
                .persistenceUnit("shard2")
                .build();
    }

    @Primary
    @Bean(name = "shard1TransactionManager")
    public PlatformTransactionManager shard1TransactionManager(
            @Qualifier("shard1EntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean(name = "shard2TransactionManager")
    public PlatformTransactionManager shard2TransactionManager(
            @Qualifier("shard2EntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}
