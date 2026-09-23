package com.vintorr.javadesktoptemplate.service.dto;

/** The fields on auth/register.blade.php. */
public record RegisterRequest(
        String name,
        String businessName,
        String email,
        String referralCode,
        String password,
        String passwordConfirmation) {
}
