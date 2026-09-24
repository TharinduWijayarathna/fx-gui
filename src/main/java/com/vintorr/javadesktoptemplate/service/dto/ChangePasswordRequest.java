package com.vintorr.javadesktoptemplate.service.dto;

/** The fields on the profile screen's "Update password" card. */
public record ChangePasswordRequest(String currentPassword, String password, String passwordConfirmation) {
}
