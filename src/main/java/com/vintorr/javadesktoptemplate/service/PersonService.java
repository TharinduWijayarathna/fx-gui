package com.vintorr.javadesktoptemplate.service;

import java.util.List;

import com.vintorr.javadesktoptemplate.domain.model.Person;

/** Supplies the rows the data-table example page lists. */
public interface PersonService {

    List<Person> all();

    /** The distinct teams, sorted — the example page's filter options. */
    List<String> teams();
}
