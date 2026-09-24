package com.vintorr.javadesktoptemplate.domain.model;

import java.time.LocalDate;

/**
 * The sample entity behind the data-table example page. It is deliberately plain: swap it
 * for whatever the real app lists and the table configuration is the only thing that changes.
 */
public record Person(
        String id,
        String name,
        String email,
        String role,
        String team,
        Status status,
        LocalDate joined,
        double monthlySpend) {

    public String initial() {
        return name == null || name.isBlank() ? "?" : name.trim().substring(0, 1).toUpperCase();
    }

    /** Status drives the badge colour, the same tones x-ui.badge uses on the web. */
    public enum Status {
        ACTIVE("Active", "success"),
        INVITED("Invited", "info"),
        SUSPENDED("Suspended", "warning"),
        ARCHIVED("Archived", "slate");

        private final String label;
        private final String color;

        Status(String label, String color) {
            this.label = label;
            this.color = color;
        }

        public String label() {
            return label;
        }

        public String color() {
            return color;
        }
    }
}
