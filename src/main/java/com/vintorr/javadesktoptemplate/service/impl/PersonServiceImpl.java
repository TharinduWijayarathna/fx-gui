package com.vintorr.javadesktoptemplate.service.impl;

import java.util.Comparator;
import java.util.List;

import com.vintorr.javadesktoptemplate.domain.model.Person;
import com.vintorr.javadesktoptemplate.repository.PersonRepository;
import com.vintorr.javadesktoptemplate.service.PersonService;

import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository people;

    public PersonServiceImpl(PersonRepository people) {
        this.people = people;
    }

    @Override
    public List<Person> all() {
        return people.findAll();
    }

    @Override
    public List<String> teams() {
        return people.findAll().stream()
                .map(Person::team)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
