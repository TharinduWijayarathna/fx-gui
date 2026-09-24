package com.vintorr.javadesktoptemplate.domain.model;

import java.util.List;

/** Everything the dashboard screen renders. Demo data — see {@code DashboardServiceImpl}. */
public record DashboardMetrics(
        int dueToday,
        int completedToday,
        int inProgress,
        int overdue,
        double outstanding,
        double revenue,
        double pipeline,
        List<Double> revenueByDay,
        String revenueRangeStart,
        String revenueRangeEnd,
        List<ScheduleEntry> todaysSchedule,
        List<AttentionItem> needsAttention) {
}
