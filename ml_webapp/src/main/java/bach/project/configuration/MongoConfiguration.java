package bach.project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"bach.project.dao"})
public class MongoConfiguration {
    private final MongoDbFactory mongoDbFactory;

    public MongoConfiguration(MongoDbFactory mongoDbFactory){
        this.mongoDbFactory=mongoDbFactory;
    }

    @Bean
    MongoTransactionManager transactionManager() {
        return new MongoTransactionManager(this.mongoDbFactory);
    }
}
