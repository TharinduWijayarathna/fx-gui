package com.vintorr.javadesktoptemplate.repository.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vintorr.javadesktoptemplate.domain.model.Person;
import com.vintorr.javadesktoptemplate.domain.model.Person.Status;
import com.vintorr.javadesktoptemplate.repository.PersonRepository;

import org.springframework.stereotype.Repository;

/**
 * Fixture rows for the example page — enough of them to exercise search, filtering,
 * sorting and pagination without a database.
 */
@Repository
public class InMemoryPersonRepository implements PersonRepository {

    private static final String[][] SEED = {
            // name, role, team, status, joined, monthly spend
            { "Ada Whitfield", "Product designer", "Design", "ACTIVE", "2023-02-14", "1840" },
            { "Bruno Castellanos", "Backend engineer", "Platform", "ACTIVE", "2022-11-02", "2650" },
            { "Camille Dubois", "Account manager", "Sales", "INVITED", "2025-08-19", "0" },
            { "Dmitri Volkov", "Data analyst", "Insights", "ACTIVE", "2024-01-08", "1275" },
            { "Elena Marsh", "Head of support", "Support", "ACTIVE", "2021-06-30", "3110" },
            { "Farid Haddad", "QA engineer", "Platform", "SUSPENDED", "2023-09-11", "740" },
            { "Grace Okonkwo", "Marketing lead", "Growth", "ACTIVE", "2022-03-21", "2980" },
            { "Hugo Lindqvist", "Frontend engineer", "Design", "ACTIVE", "2024-05-06", "1620" },
            { "Isabela Ferreira", "Customer success", "Support", "ACTIVE", "2023-12-04", "1395" },
            { "Jonas Keller", "Solutions architect", "Platform", "ARCHIVED", "2020-10-15", "0" },
            { "Keiko Tanaka", "Product manager", "Product", "ACTIVE", "2022-07-18", "2440" },
            { "Liam O'Donnell", "Sales engineer", "Sales", "ACTIVE", "2024-09-23", "1880" },
            { "Maya Srinivasan", "Data engineer", "Insights", "ACTIVE", "2023-04-27", "2120" },
            { "Noah Bergström", "Support specialist", "Support", "INVITED", "2025-09-01", "0" },
            { "Olivia Nkemdirim", "Brand designer", "Design", "ACTIVE", "2024-02-12", "1490" },
            { "Pavel Novak", "Site reliability", "Platform", "ACTIVE", "2021-11-29", "3350" },
            { "Quentin Marchand", "Content strategist", "Growth", "SUSPENDED", "2023-06-16", "615" },
            { "Rania Belkacem", "Finance analyst", "Operations", "ACTIVE", "2022-09-05", "2075" },
            { "Sofia Ricci", "Recruiter", "Operations", "ACTIVE", "2024-11-11", "960" },
            { "Tomas Ivarsson", "Mobile engineer", "Product", "ACTIVE", "2023-01-23", "1755" },
            { "Uma Chandrasekhar", "UX researcher", "Design", "ACTIVE", "2024-07-30", "1340" },
            { "Viktor Petrov", "Security engineer", "Platform", "ACTIVE", "2022-05-09", "2890" },
            { "Wendy Ashford", "Operations lead", "Operations", "ARCHIVED", "2019-08-20", "0" },
            { "Xavier Boucher", "Partnerships", "Sales", "INVITED", "2025-07-14", "0" },
            { "Yara Haddadin", "Support engineer", "Support", "ACTIVE", "2023-10-02", "1180" },
            { "Zachary Lindgren", "Growth analyst", "Growth", "ACTIVE", "2024-03-18", "1425" },
            { "Amara Diallo", "Technical writer", "Product", "ACTIVE", "2023-08-07", "1090" },
            { "Bjorn Halvorsen", "Platform engineer", "Platform", "ACTIVE", "2021-04-12", "3020" },
            { "Chiara Bellini", "Designer", "Design", "SUSPENDED", "2024-06-25", "830" },
            { "Devon Hargreaves", "Sales manager", "Sales", "ACTIVE", "2022-12-19", "2560" },
            { "Elif Yilmaz", "Insights lead", "Insights", "ACTIVE", "2021-09-27", "3240" },
            { "Felipe Cardoso", "Support specialist", "Support", "ACTIVE", "2025-01-15", "705" },
            { "Greta Sorensen", "Product ops", "Product", "INVITED", "2025-09-08", "0" },
            { "Hassan Rahimi", "Growth engineer", "Growth", "ACTIVE", "2023-03-06", "1930" },
    };

    private final List<Person> people = build();

    @Override
    public List<Person> findAll() {
        return people;
    }

    private static List<Person> build() {
        List<Person> rows = new ArrayList<>(SEED.length);
        for (int i = 0; i < SEED.length; i++) {
            String[] row = SEED[i];
            rows.add(new Person(
                    String.format("P-%04d", 1001 + i),
                    row[0],
                    email(row[0]),
                    row[1],
                    row[2],
                    Status.valueOf(row[3]),
                    LocalDate.parse(row[4]),
                    Double.parseDouble(row[5])));
        }
        return List.copyOf(rows);
    }

    private static String email(String name) {
        String handle = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z]+", ".");
        return handle + "@example.com";
    }
}
