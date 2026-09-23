package com.vintorr.javadesktoptemplate.service.dto;

/** The fields on the login screen. */
public record LoginRequest(String email, String password, boolean remember) {
}
