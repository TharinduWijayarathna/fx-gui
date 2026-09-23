package com.vintorr.javadesktoptemplate.service;

import com.vintorr.javadesktoptemplate.domain.model.User;

/** Holds who is signed in for this desktop session — the equivalent of {@code auth()->user()}. */
public interface SessionService {

    void login(User user, boolean remember);

    void logout();

    User currentUser();

    boolean isAuthenticated();

    boolean isRemembered();
}
