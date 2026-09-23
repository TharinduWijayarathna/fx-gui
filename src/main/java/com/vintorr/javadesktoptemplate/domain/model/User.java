package com.vintorr.javadesktoptemplate.domain.model;

/** A shop owner / team member. Mirrors the web app's users + their tenant's business name. */
public record User(String id, String name, String email, String businessName, String passwordHash) {

    public String initial() {
        return name == null || name.isBlank() ? "U" : name.trim().substring(0, 1).toUpperCase();
    }
}
