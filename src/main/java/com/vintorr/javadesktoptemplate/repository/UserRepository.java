package com.vintorr.javadesktoptemplate.repository;

import java.util.Optional;

import com.vintorr.javadesktoptemplate.domain.model.User;

/**
 * Persistence contract for users. The service layer depends on this interface only, so the
 * in-memory implementation can be replaced by a Spring Data repository unchanged.
 */
public interface UserRepository {

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Inserts or replaces the user with this id. */
    User save(User user);
}
