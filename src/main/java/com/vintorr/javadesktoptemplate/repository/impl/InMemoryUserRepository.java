package com.vintorr.javadesktoptemplate.repository.impl;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.vintorr.javadesktoptemplate.common.security.PasswordEncoder;
import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

/**
 * Process-lifetime user store, keyed by id so a profile edit can change the email address.
 * This template ships without a database; adding one means adding a new
 * {@link UserRepository} implementation and nothing else.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> byId = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public InMemoryUserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    void seedDemoAccount() {
        save(new User(
                "u-demo",
                "Alex Morgan",
                "demo@example.com",
                "Acme Industries",
                passwordEncoder.encode("password")));
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String wanted = normalise(email);
        return byId.values().stream()
                .filter(user -> normalise(user.email()).equals(wanted))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public User save(User user) {
        byId.put(user.id(), user);
        return user;
    }

    private String normalise(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
