package org.vkartashov.hla2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.vkartashov.hla2.model.Person;
import org.vkartashov.hla2.repository.elasticsearch.PersonElasticRepository;
import org.vkartashov.hla2.repository.mongodb.PersonRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/people")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonElasticRepository personElasticRepository;

    @PostMapping
    public List<Person> createPerson(@RequestBody Person person) {
        return List.of(personRepository.save(person), personElasticRepository.save(person));
    }

    @GetMapping("/{id}")
    public List<Person> getPersonById(@PathVariable String id) {
        return Stream.of(
                personRepository.findById(id).orElse(null),
                personElasticRepository.findById(id).orElse(null)
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    // You can add more CRUD operations and custom queries here
}
