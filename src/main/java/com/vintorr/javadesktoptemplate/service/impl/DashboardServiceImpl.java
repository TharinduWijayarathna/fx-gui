package com.vintorr.javadesktoptemplate.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.vintorr.javadesktoptemplate.domain.model.AttentionItem;
import com.vintorr.javadesktoptemplate.domain.model.DashboardMetrics;
import com.vintorr.javadesktoptemplate.domain.model.ScheduleEntry;
import com.vintorr.javadesktoptemplate.service.DashboardService;

import org.springframework.stereotype.Service;

/**
 * Demo metrics. The numbers are fixtures because this template has no database behind it —
 * replace this bean and every dashboard widget keeps working.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("dd MMM");

    /** A plausible month-to-date collections curve. */
    private static final double[] DAILY_COLLECTIONS = {
            4_850, 2_600, 9_120, 3_480, 1_240, 7_650, 5_830,
            2_210, 6_490, 10_340, 4_170, 1_890, 8_760, 5_230,
            3_180, 9_620, 4_450, 2_730, 7_190, 5_940, 3_860,
            8_410, 4_980, 2_370, 6_630, 9_280, 3_520, 5_760,
            4_090, 7_840, 6_150,
    };

    @Override
    public DashboardMetrics metrics() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        List<Double> cumulative = cumulativeRevenue(monthStart, today);
        double revenue = cumulative.isEmpty() ? 0 : cumulative.get(cumulative.size() - 1);

        return new DashboardMetrics(
                12,
                9,
                34,
                3,
                18_640,
                revenue,
                41_200,
                cumulative,
                monthStart.format(DAY_LABEL),
                today.format(DAY_LABEL),
                todaysSchedule(),
                needsAttention());
    }

    private List<Double> cumulativeRevenue(LocalDate from, LocalDate to) {
        List<Double> series = new ArrayList<>();
        double running = 0;
        int days = to.getDayOfMonth() - from.getDayOfMonth() + 1;
        for (int i = 0; i < days; i++) {
            running += DAILY_COLLECTIONS[i % DAILY_COLLECTIONS.length];
            series.add(running);
        }
        return series;
    }

    private List<ScheduleEntry> todaysSchedule() {
        return List.of(
                new ScheduleEntry("TSK-1042", "Design review · Acme Industries", ScheduleEntry.Kind.MEETING, "08:30 AM"),
                new ScheduleEntry("TSK-1038", "Q3 report sign-off", ScheduleEntry.Kind.REVIEW, "10:00 AM"),
                new ScheduleEntry("TSK-1051", "Onboarding call · Northwind", ScheduleEntry.Kind.MEETING, "11:15 AM"),
                new ScheduleEntry("TSK-1029", "Billing migration cutover", ScheduleEntry.Kind.DEADLINE, "01:45 PM"),
                new ScheduleEntry("TSK-1055", "Roadmap sync · Platform", ScheduleEntry.Kind.MEETING, "03:30 PM"),
                new ScheduleEntry("TSK-1033", "Security checklist", ScheduleEntry.Kind.REVIEW, "05:00 PM"));
    }

    private List<AttentionItem> needsAttention() {
        return List.of(
                new AttentionItem("TSK-0987", "Vendor contract renewal", "18 Sep 2026", "Overdue", "danger"),
                new AttentionItem("TSK-1002", "Invoice #4471 unpaid", "20 Sep 2026", "Overdue", "danger"),
                new AttentionItem("TSK-1014", "Access review pending", "22 Sep 2026", "Due soon", "warning"));
    }
}
