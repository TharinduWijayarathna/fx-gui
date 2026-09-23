package com.vintorr.javadesktoptemplate.common.security;

/**
 * Hashing contract for stored credentials. Swap the implementation for Spring Security's
 * {@code BCryptPasswordEncoder} without touching the service layer.
 */
public interface PasswordEncoder {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
