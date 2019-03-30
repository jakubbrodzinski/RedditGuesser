package bach.project.configuration;

import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"bach.project.dao"})
public class MongoConfiguration{
    @Bean
    public MongoClientOptions mongoClientOptions(){
        return MongoClientOptions.builder().minHeartbeatFrequency(500).heartbeatSocketTimeout(3000).socketTimeout(30000).heartbeatConnectTimeout(3000).connectTimeout(30000).build();
    }
}
