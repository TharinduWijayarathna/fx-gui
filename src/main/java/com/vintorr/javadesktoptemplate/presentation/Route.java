package com.vintorr.javadesktoptemplate.presentation;

/**
 * The screens this shell can navigate to. Adding a page means adding a constant here,
 * a view bean, and one line in {@link Router}.
 */
public enum Route {

    LOGIN(false),
    REGISTER(false),
    DASHBOARD(true),
    PEOPLE(true),
    PROFILE(true);

    private final boolean requiresAuthentication;

    Route(boolean requiresAuthentication) {
        this.requiresAuthentication = requiresAuthentication;
    }

    public boolean requiresAuthentication() {
        return requiresAuthentication;
    }
}
