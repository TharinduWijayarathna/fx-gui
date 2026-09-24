package com.vintorr.javadesktoptemplate.service;

import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.service.dto.ChangePasswordRequest;
import com.vintorr.javadesktoptemplate.service.dto.UpdateProfileRequest;

/**
 * Account self-service for whoever is signed in — the desktop equivalent of the web app's
 * profile page. Both methods raise {@code ValidationException} with the offending field.
 */
public interface ProfileService {

    User updateProfile(UpdateProfileRequest request);

    User changePassword(ChangePasswordRequest request);
}
