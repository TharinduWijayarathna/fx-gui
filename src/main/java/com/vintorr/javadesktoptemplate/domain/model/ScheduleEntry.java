package com.vintorr.javadesktoptemplate.domain.model;

/** A row in "Today's schedule": a reference, who it involves, and what kind of entry it is. */
public record ScheduleEntry(String reference, String subject, Kind kind, String time) {

    public enum Kind {
        MEETING("Meeting", "brand"),
        REVIEW("Review", "info"),
        DEADLINE("Deadline", "violet");

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
