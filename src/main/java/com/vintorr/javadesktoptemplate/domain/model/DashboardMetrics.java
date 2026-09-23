package com.vintorr.javadesktoptemplate.domain.model;

import java.util.List;

/** Everything pages/dashboard/index.blade.php renders. */
public record DashboardMetrics(
        int todaysBookings,
        int todaysReturns,
        int outNow,
        int overdue,
        double outstandingPayments,
        double revenue,
        double depositsHeld,
        List<Double> revenueByDay,
        String revenueRangeStart,
        String revenueRangeEnd,
        List<ScheduleEntry> todaysSchedule,
        List<OverdueBooking> overdueBookings) {
}
