package com.vintorr.javadesktoptemplate.service.dto;

/** The fields on the profile screen's "Profile information" card. */
public record UpdateProfileRequest(String name, String email, String businessName) {
}
