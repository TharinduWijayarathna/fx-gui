package com.vintorr.javadesktoptemplate.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

/**
 * Demo-grade encoder so passwords are never held in plain text in memory.
 * Production should use an adaptive hash (bcrypt/argon2), not a bare digest.
 */
@Component
public class Sha256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return MessageDigest.isEqual(
                encode(rawPassword).getBytes(StandardCharsets.UTF_8),
                encodedPassword.getBytes(StandardCharsets.UTF_8));
    }
}
