package com.vintorr.javadesktoptemplate.domain.model;

/** A row in "Needs attention". */
public record OverdueBooking(String number, String customer, String dueDate, String status, String statusColor) {
}
