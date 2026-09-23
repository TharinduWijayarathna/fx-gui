package com.vintorr.javadesktoptemplate.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.vintorr.javadesktoptemplate.domain.model.DashboardMetrics;
import com.vintorr.javadesktoptemplate.domain.model.OverdueBooking;
import com.vintorr.javadesktoptemplate.domain.model.ScheduleEntry;
import com.vintorr.javadesktoptemplate.service.DashboardService;

import org.springframework.stereotype.Service;

/**
 * Demo metrics. The shape matches the web app's dashboard payload; the numbers are fixtures
 * because this template has no database behind it.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("dd MMM");

    /** A plausible month-to-date collections curve. */
    private static final double[] DAILY_COLLECTIONS = {
            48_500, 26_000, 91_200, 34_800, 12_400, 76_500, 58_300,
            22_100, 64_900, 103_400, 41_700, 18_900, 87_600, 52_300,
            31_800, 96_200, 44_500, 27_300, 71_900, 59_400, 38_600,
            84_100, 49_800, 23_700, 66_300, 92_800, 35_200, 57_600,
            40_900, 78_400, 61_500,
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
                186_400,
                revenue,
                412_000,
                cumulative,
                monthStart.format(DAY_LABEL),
                today.format(DAY_LABEL),
                todaysSchedule(),
                overdueBookings());
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
                new ScheduleEntry("ORD-1042", "Nimali Perera", ScheduleEntry.Kind.PICKUP, "08:30 AM"),
                new ScheduleEntry("ORD-1038", "Sahan Weerasinghe", ScheduleEntry.Kind.RETURN, "10:00 AM"),
                new ScheduleEntry("ORD-1051", "Colombo Hilton Events", ScheduleEntry.Kind.PICKUP, "11:15 AM"),
                new ScheduleEntry("ORD-1029", "Dilhara Fernando", ScheduleEntry.Kind.BOTH, "01:45 PM"),
                new ScheduleEntry("ORD-1055", "Kandy Wedding Co.", ScheduleEntry.Kind.PICKUP, "03:30 PM"),
                new ScheduleEntry("ORD-1033", "Ruwan Jayasuriya", ScheduleEntry.Kind.RETURN, "05:00 PM"));
    }

    private List<OverdueBooking> overdueBookings() {
        return List.of(
                new OverdueBooking("ORD-0987", "Tharaka Silva", "18 Sep 2026", "Overdue", "danger"),
                new OverdueBooking("ORD-1002", "Galle Face Catering", "20 Sep 2026", "Overdue", "danger"),
                new OverdueBooking("ORD-1014", "Menaka Rathnayake", "22 Sep 2026", "Active", "warning"));
    }
}
