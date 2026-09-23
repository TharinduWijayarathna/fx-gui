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
 * Process-lifetime user store. This template ships without a database; adding one means
 * adding a new {@link UserRepository} implementation and nothing else.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> byEmail = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public InMemoryUserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    void seedDemoAccount() {
        save(new User(
                "u-demo",
                "Tharindu",
                "demo@vintorr.com",
                "Colombo Event Rentals",
                passwordEncoder.encode("password")));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(byEmail.get(normalise(email)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return byEmail.containsKey(normalise(email));
    }

    @Override
    public User save(User user) {
        byEmail.put(normalise(user.email()), user);
        return user;
    }

    private String normalise(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
