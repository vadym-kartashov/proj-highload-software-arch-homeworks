package org.vkartashov.hla2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.vkartashov.hla2.repository.mongodb",
        mongoTemplateRef = "mongoTemplate")
public class MongoDbConfig {
}
