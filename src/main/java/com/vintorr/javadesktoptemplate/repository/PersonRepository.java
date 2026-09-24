package com.vintorr.javadesktoptemplate.repository;

import java.util.List;

import com.vintorr.javadesktoptemplate.domain.model.Person;

/** Read side of the sample directory the data-table example page lists. */
public interface PersonRepository {

    List<Person> findAll();
}
