package com.vintorr.javadesktoptemplate.service.impl;

import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.service.SessionService;

import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {

    private User user;
    private boolean remembered;

    @Override
    public void login(User user, boolean remember) {
        this.user = user;
        this.remembered = remember;
    }

    @Override
    public void logout() {
        this.user = null;
        this.remembered = false;
    }

    @Override
    public User currentUser() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return user != null;
    }

    @Override
    public boolean isRemembered() {
        return remembered;
    }
}
