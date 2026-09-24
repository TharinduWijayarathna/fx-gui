package com.vintorr.javadesktoptemplate.presentation.view;

import java.util.ArrayList;
import java.util.List;

import com.vintorr.javadesktoptemplate.common.util.Money;
import com.vintorr.javadesktoptemplate.domain.model.AttentionItem;
import com.vintorr.javadesktoptemplate.domain.model.DashboardMetrics;
import com.vintorr.javadesktoptemplate.domain.model.ScheduleEntry;
import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.presentation.Route;
import com.vintorr.javadesktoptemplate.presentation.Router;
import com.vintorr.javadesktoptemplate.presentation.component.AppShell;
import com.vintorr.javadesktoptemplate.presentation.component.AreaChartView;
import com.vintorr.javadesktoptemplate.presentation.component.Components;
import com.vintorr.javadesktoptemplate.presentation.component.ResponsiveRow;
import com.vintorr.javadesktoptemplate.service.DashboardService;
import com.vintorr.javadesktoptemplate.service.SessionService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** The landing screen: today's numbers, the revenue curve and the two attention lists. */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DashboardView implements AppView {

    private final SessionService session;
    private final DashboardService dashboard;
    private final Router router;

    public DashboardView(SessionService session, DashboardService dashboard, Router router) {
        this.session = session;
        this.dashboard = dashboard;
        this.router = router;
    }

    @Override
    public String title(String applicationTitle) {
        return "Dashboard · " + applicationTitle;
    }

    @Override
    public Parent view() {
        User user = session.currentUser();
        DashboardMetrics metrics = dashboard.metrics();

        Button create = Components.primary("New record", "plus", false);
        create.setOnAction(e -> router.placeholder("New record"));

        Button people = Components.secondary("Browse people", "users");
        people.setOnAction(e -> router.show(Route.PEOPLE));

        VBox page = new VBox(24,
                Components.pageHeader("Today’s overview", user == null ? "" : user.businessName(),
                        List.of("Dashboard"), create, people),
                statRow(metrics),
                revenueRow(metrics),
                attentionRow(metrics));

        return AppShell.wrap(router, Route.DASHBOARD, page);
    }

    /** grid sm:grid-cols-2 xl:grid-cols-5 */
    private Region statRow(DashboardMetrics m) {
        return new ResponsiveRow(12, 880, 5, 2, List.of(
                Components.stat("Due today", String.valueOf(m.dueToday()), "brand", null),
                Components.stat("Completed today", String.valueOf(m.completedToday()), "info", null),
                Components.stat("In progress", String.valueOf(m.inProgress()), "success", null),
                Components.stat("Overdue", String.valueOf(m.overdue()), "danger",
                        m.overdue() > 0 ? "Needs attention" : null),
                Components.stat("Outstanding", Money.format(m.outstanding()), "warning", null)));
    }

    private Region revenueRow(DashboardMetrics m) {
        AreaChartView chart = new AreaChartView(m.revenueByDay(), 176);
        HBox axis = new HBox(Components.label(m.revenueRangeStart(), "faint-2xs"), Components.hGrow(),
                Components.label(m.revenueRangeEnd(), "faint-2xs"));

        var reports = Components.link("View reports →", "link");
        reports.setOnAction(e -> router.placeholder("Reports"));
        HBox footer = new HBox(8, Components.label("Cumulative revenue this month", "muted-sm"),
                Components.hGrow(), reports);
        footer.setAlignment(Pos.CENTER_LEFT);

        VBox chartBox = new VBox(4, chart, axis);
        VBox.setVgrow(chart, Priority.ALWAYS);

        VBox revenue = Components.card("Revenue", "This month · " + Money.format(m.revenue()), chartBox, footer);

        List<Node> rows = new ArrayList<>();
        for (ScheduleEntry entry : m.todaysSchedule()) {
            rows.add(scheduleRow(entry));
        }
        VBox schedule = Components.card("Today’s schedule", "Meetings, reviews and deadlines",
                Components.dividedList(rows));

        return new ResponsiveRow(16, 820, 5, 1, List.of(revenue, schedule), 3, 2);
    }

    private Region attentionRow(DashboardMetrics m) {
        List<Node> rows = new ArrayList<>();
        for (AttentionItem item : m.needsAttention()) {
            rows.add(attentionRow(item));
        }
        VBox attention = Components.card("Needs attention", "Overdue and at-risk items",
                Components.dividedList(rows));

        Button manage = Components.secondary("Open pipeline", null);
        manage.setOnAction(e -> router.placeholder("Pipeline"));

        VBox pipelineBody = new VBox(8,
                Components.label(Money.format(m.pipeline()), "big-value"),
                Components.label("Forecast value of everything still open.", "muted-sm"),
                spacer(8),
                manage);
        VBox pipeline = Components.card("Pipeline", "Open opportunities", pipelineBody);

        return new ResponsiveRow(16, 820, 5, 1, List.of(attention, pipeline), 3, 2);
    }

    // ----------------------------------------------------------- list rows

    private Node scheduleRow(ScheduleEntry entry) {
        HBox heading = new HBox(8,
                Components.label(entry.reference(), "font-medium"),
                Components.badge(entry.kind().label(), entry.kind().color(), false));
        heading.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(2, heading, Components.label(entry.subject(), "muted-sm"));

        HBox row = new HBox(12, details, Components.hGrow(), Components.label(entry.time(), "muted-sm"));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 4, 10, 4));
        row.getStyleClass().add("list-row");
        row.setOnMouseClicked(e -> router.placeholder(entry.reference()));
        return row;
    }

    private Node attentionRow(AttentionItem item) {
        VBox details = new VBox(2,
                Components.label(item.reference() + " · " + item.subject(), "font-medium"),
                Components.label("Due " + item.dueDate(), "danger-xs"));

        HBox row = new HBox(12, details, Components.hGrow(),
                Components.badge(item.status(), item.statusColor(), true));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 4, 10, 4));
        row.getStyleClass().addAll("list-row", "list-row-danger");
        row.setOnMouseClicked(e -> router.placeholder(item.reference()));
        return row;
    }

    private Region spacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        return spacer;
    }
}
