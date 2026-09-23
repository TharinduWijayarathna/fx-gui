package com.vintorr.javadesktoptemplate.domain.model;

/** A row in "Today's schedule": an order number, the customer, and whether it's a pickup or a return. */
public record ScheduleEntry(String number, String customer, Kind kind, String time) {

    public enum Kind {
        PICKUP("Pickup", "brand"),
        RETURN("Return", "info"),
        BOTH("Pickup & return", "violet");

        private final String label;
        private final String color;

        Kind(String label, String color) {
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
