package org.vkartashov.hla2.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.vkartashov.hla2.model.Person;

public interface PersonRepository extends MongoRepository<Person, String> {
    // Custom queries (if needed)
}
