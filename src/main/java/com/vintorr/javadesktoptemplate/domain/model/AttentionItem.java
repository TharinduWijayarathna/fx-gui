package com.vintorr.javadesktoptemplate.domain.model;

/** A row in the dashboard's "Needs attention" list. */
public record AttentionItem(String reference, String subject, String dueDate, String status, String statusColor) {
}
