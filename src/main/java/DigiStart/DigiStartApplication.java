package DigiStart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = FlywayAutoConfiguration.class)
@EnableJpaRepositories(
    basePackages = "DigiStart.Repository",
    entityManagerFactoryRef = "shard1EntityManagerFactory",
    transactionManagerRef = "shard1TransactionManager"
)
public class DigiStartApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigiStartApplication.class, args);
	}

}
