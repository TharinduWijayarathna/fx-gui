package com.vintorr.javadesktoptemplate.service.impl;

import java.util.UUID;
import java.util.regex.Pattern;

import com.vintorr.javadesktoptemplate.common.security.PasswordEncoder;
import com.vintorr.javadesktoptemplate.domain.exception.ValidationException;
import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.repository.UserRepository;
import com.vintorr.javadesktoptemplate.service.AuthenticationService;
import com.vintorr.javadesktoptemplate.service.SessionService;
import com.vintorr.javadesktoptemplate.service.dto.LoginRequest;
import com.vintorr.javadesktoptemplate.service.dto.RegisterRequest;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");
    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    private final UserRepository users;
    private final SessionService session;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserRepository users, SessionService session, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.session = session;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User login(LoginRequest request) {
        if (isBlank(request.email())) {
            throw new ValidationException("email", "The email field is required.");
        }
        if (isBlank(request.password())) {
            throw new ValidationException("password", "The password field is required.");
        }

        User user = users.findByEmail(request.email())
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.passwordHash()))
                .orElseThrow(() -> new ValidationException("email", "These credentials do not match our records."));

        session.login(user, request.remember());
        return user;
    }

    @Override
    public User register(RegisterRequest request) {
        validate(request);

        User user = users.save(new User(
                UUID.randomUUID().toString(),
                request.name().trim(),
                request.email().trim(),
                request.businessName().trim(),
                passwordEncoder.encode(request.password())));

        session.login(user, false);
        return user;
    }

    @Override
    public void logout() {
        session.logout();
    }

    private void validate(RegisterRequest request) {
        if (isBlank(request.name())) {
            throw new ValidationException("name", "The your name field is required.");
        }
        if (isBlank(request.businessName())) {
            throw new ValidationException("businessName", "The business name field is required.");
        }
        if (isBlank(request.email())) {
            throw new ValidationException("email", "The email field is required.");
        }
        if (!EMAIL.matcher(request.email().trim()).matches()) {
            throw new ValidationException("email", "The email field must be a valid email address.");
        }
        if (users.existsByEmail(request.email())) {
            throw new ValidationException("email", "The email has already been taken.");
        }
        if (isBlank(request.password()) || request.password().length() < MINIMUM_PASSWORD_LENGTH) {
            throw new ValidationException("password", "The password field must be at least 8 characters.");
        }
        if (!request.password().equals(request.passwordConfirmation())) {
            throw new ValidationException("passwordConfirmation", "The password field confirmation does not match.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
