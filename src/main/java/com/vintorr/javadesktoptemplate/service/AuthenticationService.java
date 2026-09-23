package com.vintorr.javadesktoptemplate.service;

import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.service.dto.LoginRequest;
import com.vintorr.javadesktoptemplate.service.dto.RegisterRequest;

/** Login and registration rules. */
public interface AuthenticationService {

    User login(LoginRequest request);

    User register(RegisterRequest request);

    void logout();
}
