package org.vkartashov.hla2.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.vkartashov.hla2.model.Person;

public interface PersonElasticRepository extends ElasticsearchRepository<Person, String> {
    // Custom queries (if needed)
}
