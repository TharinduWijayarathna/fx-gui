package com.vintorr.javadesktoptemplate.service.impl;

import java.util.regex.Pattern;

import com.vintorr.javadesktoptemplate.common.security.PasswordEncoder;
import com.vintorr.javadesktoptemplate.domain.exception.ValidationException;
import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.repository.UserRepository;
import com.vintorr.javadesktoptemplate.service.ProfileService;
import com.vintorr.javadesktoptemplate.service.SessionService;
import com.vintorr.javadesktoptemplate.service.dto.ChangePasswordRequest;
import com.vintorr.javadesktoptemplate.service.dto.UpdateProfileRequest;

import org.springframework.stereotype.Service;

/**
 * Mirrors the web app's Profile\Edit rules: the email has to stay unique across everyone
 * else, and a password change has to prove the current one first.
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");
    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    private final UserRepository users;
    private final SessionService session;
    private final PasswordEncoder passwordEncoder;

    public ProfileServiceImpl(UserRepository users, SessionService session, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.session = session;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User updateProfile(UpdateProfileRequest request) {
        User current = signedIn();

        if (isBlank(request.name())) {
            throw new ValidationException("name", "The name field is required.");
        }
        if (isBlank(request.email())) {
            throw new ValidationException("email", "The email field is required.");
        }
        if (!EMAIL.matcher(request.email().trim()).matches()) {
            throw new ValidationException("email", "The email field must be a valid email address.");
        }
        if (isBlank(request.businessName())) {
            throw new ValidationException("businessName", "The organisation field is required.");
        }
        boolean takenByAnother = users.findByEmail(request.email())
                .filter(other -> !other.id().equals(current.id()))
                .isPresent();
        if (takenByAnother) {
            throw new ValidationException("email", "The email has already been taken.");
        }

        User updated = users.save(new User(
                current.id(),
                request.name().trim(),
                request.email().trim(),
                request.businessName().trim(),
                current.passwordHash()));

        session.replaceCurrentUser(updated);
        return updated;
    }

    @Override
    public User changePassword(ChangePasswordRequest request) {
        User current = signedIn();

        if (isBlank(request.currentPassword())) {
            throw new ValidationException("currentPassword", "The current password field is required.");
        }
        if (!passwordEncoder.matches(request.currentPassword(), current.passwordHash())) {
            throw new ValidationException("currentPassword", "The password is incorrect.");
        }
        if (isBlank(request.password()) || request.password().length() < MINIMUM_PASSWORD_LENGTH) {
            throw new ValidationException("password", "The password field must be at least 8 characters.");
        }
        if (!request.password().equals(request.passwordConfirmation())) {
            throw new ValidationException("passwordConfirmation", "The password field confirmation does not match.");
        }

        User updated = users.save(new User(
                current.id(),
                current.name(),
                current.email(),
                current.businessName(),
                passwordEncoder.encode(request.password())));

        session.replaceCurrentUser(updated);
        return updated;
    }

    private User signedIn() {
        User user = session.currentUser();
        if (user == null) {
            throw new IllegalStateException("No user is signed in.");
        }
        return user;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
