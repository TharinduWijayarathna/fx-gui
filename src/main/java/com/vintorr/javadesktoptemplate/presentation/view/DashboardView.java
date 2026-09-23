package com.vintorr.javadesktoptemplate.presentation.view;

import java.util.ArrayList;
import java.util.List;

import com.vintorr.javadesktoptemplate.service.AuthenticationService;
import com.vintorr.javadesktoptemplate.service.SessionService;
import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.domain.model.DashboardMetrics;
import com.vintorr.javadesktoptemplate.service.DashboardService;
import com.vintorr.javadesktoptemplate.domain.model.OverdueBooking;
import com.vintorr.javadesktoptemplate.domain.model.ScheduleEntry;
import com.vintorr.javadesktoptemplate.common.util.Money;
import com.vintorr.javadesktoptemplate.presentation.view.AppView;
import com.vintorr.javadesktoptemplate.presentation.component.AreaChartView;
import com.vintorr.javadesktoptemplate.presentation.component.Icons;
import com.vintorr.javadesktoptemplate.presentation.component.ResponsiveRow;
import com.vintorr.javadesktoptemplate.presentation.Router;
import com.vintorr.javadesktoptemplate.presentation.component.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Port of layouts/app.blade.php + pages/dashboard/index.blade.php:
 * the 17.5rem sidebar, the topbar, and today's operations.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DashboardView implements AppView {

    private static final double SIDEBAR_WIDTH = 280;
    private static final double BAR_HEIGHT = 64;
    private static final double CONTENT_MAX_WIDTH = 1280;

    /** The nav groups from layouts/app.blade.php, in the same order. */
    private static final List<NavGroup> NAV = List.of(
            new NavGroup("Overview", List.of(
                    new NavItem("Dashboard", "home", true))),
            new NavGroup("Rentals", List.of(
                    new NavItem("Orders", "orders", false),
                    new NavItem("Availability", "clock", false))),
            new NavGroup("Directory", List.of(
                    new NavItem("Customers", "customers", false),
                    new NavItem("Items", "package", false),
                    new NavItem("Categories", "tag", false))),
            new NavGroup("Finance", List.of(
                    new NavItem("Invoices", "invoice", false),
                    new NavItem("Payments", "cash", false),
                    new NavItem("Security deposits", "lock", false),
                    new NavItem("Reports", "chart", false))),
            new NavGroup("Shop", List.of(
                    new NavItem("Team", "users", false),
                    new NavItem("Settings", "cog", false),
                    new NavItem("Billing", "card", false))));

    private final SessionService session;
    private final AuthenticationService authentication;
    private final DashboardService dashboard;
    private final Router router;

    public DashboardView(SessionService session, AuthenticationService authentication,
                         DashboardService dashboard, Router router) {
        this.session = session;
        this.authentication = authentication;
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

        VBox column = new VBox(topbar(user), main(user, metrics));
        HBox.setHgrow(column, Priority.ALWAYS);

        HBox shell = new HBox(sidebar(), column);
        shell.setFillHeight(true);
        return shell;
    }

    // ------------------------------------------------------------- sidebar

    private Region sidebar() {
        ImageView logo = new ImageView(new Image(
                getClass().getResourceAsStream("/com/vintorr/javadesktoptemplate/images/vintorr-rently-logo.png")));
        logo.setPreserveRatio(true);
        logo.setFitHeight(32);

        HBox header = new HBox(logo);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 16, 0, 16));
        header.setMinHeight(BAR_HEIGHT);
        header.setPrefHeight(BAR_HEIGHT);
        header.getStyleClass().add("sidebar-header");

        VBox nav = new VBox(20);
        nav.setPadding(new Insets(16, 12, 16, 12));
        for (NavGroup group : NAV) {
            VBox items = new VBox(2);
            for (NavItem item : group.items()) {
                items.getChildren().add(navLink(item));
            }

            Label groupLabel = Components.label(group.label().toUpperCase(), "meta");
            groupLabel.setPadding(new Insets(0, 12, 6, 12));
            nav.getChildren().add(new VBox(0, groupLabel, items));
        }

        var scroller = Components.scroller(nav);
        VBox.setVgrow(scroller, Priority.ALWAYS);

        HBox support = navLink(new NavItem("Support", "support", false));
        VBox footer = new VBox(support);
        footer.setPadding(new Insets(12));
        footer.getStyleClass().add("sidebar-footer");

        VBox sidebar = new VBox(header, scroller, footer);
        sidebar.setMinWidth(SIDEBAR_WIDTH);
        sidebar.setPrefWidth(SIDEBAR_WIDTH);
        sidebar.setMaxWidth(SIDEBAR_WIDTH);
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private HBox navLink(NavItem item) {
        HBox link = new HBox(12, Icons.nav(item.icon()), new Label(item.label()));
        link.setAlignment(Pos.CENTER_LEFT);
        link.getStyleClass().add("nav-link");
        if (item.active()) {
            link.getStyleClass().add("nav-link-active");
        } else {
            link.setOnMouseClicked(e -> comingSoon(item.label()));
        }
        return link;
    }

    // -------------------------------------------------------------- topbar

    private Region topbar(User user) {
        Button newOrder = Components.small("New order", "plus", "primary");
        newOrder.setOnAction(e -> comingSoon("New order"));

        HBox bar = new HBox(12, Components.hGrow(), newOrder, userChip(user));
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(0, 24, 0, 24));
        bar.setMinHeight(BAR_HEIGHT);
        bar.setPrefHeight(BAR_HEIGHT);
        bar.getStyleClass().add("topbar");
        return bar;
    }

    private Region userChip(User user) {
        HBox chip = new HBox(8, Components.avatar(user.name(), 32), Components.label(user.name(), "font-medium"));
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.getStyleClass().add("user-chip");

        Popup menu = userMenu();
        chip.setOnMouseClicked(e -> {
            if (menu.isShowing()) {
                menu.hide();
                return;
            }
            var bounds = chip.localToScreen(chip.getBoundsInLocal());
            menu.show(chip, bounds.getMaxX() - 192, bounds.getMaxY() + 8);
        });
        return chip;
    }

    private Popup userMenu() {
        VBox items = new VBox();
        items.getStyleClass().add("app-menu");
        items.setPrefWidth(192);

        Popup popup = new Popup();
        popup.setAutoHide(true);

        items.getChildren().addAll(
                menuItem("Profile", () -> comingSoon("Profile"), popup),
                menuItem("Settings", () -> comingSoon("Settings"), popup),
                menuItem("Log out", () -> {
                    authentication.logout();
                    router.showLogin();
                    router.toasts().info("Signed out", "You have been logged out.");
                }, popup));

        popup.getContent().add(items);
        return popup;
    }

    private Label menuItem(String text, Runnable action, Popup popup) {
        Label item = new Label(text);
        item.getStyleClass().add("app-menu-item");
        item.setMaxWidth(Double.MAX_VALUE);
        item.setOnMouseClicked(e -> {
            popup.hide();
            action.run();
        });
        return item;
    }

    // ---------------------------------------------------------------- main

    private Region main(User user, DashboardMetrics metrics) {
        Button newOrder = Components.primary("New order", "plus", false);
        newOrder.setOnAction(e -> comingSoon("New order"));

        Button availability = Components.secondary("Check availability", "search");
        availability.setOnAction(e -> comingSoon("Availability"));

        VBox page = new VBox(24,
                Components.pageHeader("Today’s operations", user.businessName(), List.of("Dashboard"), newOrder, availability),
                statRow(metrics),
                revenueRow(metrics),
                attentionRow(metrics));
        page.setPadding(new Insets(24, 32, 32, 32));
        page.setMaxWidth(CONTENT_MAX_WIDTH);

        StackPane centered = new StackPane(page);
        StackPane.setAlignment(page, Pos.TOP_CENTER);

        var scroller = Components.scroller(centered);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        return scroller;
    }

    /** grid sm:grid-cols-2 xl:grid-cols-5 */
    private Region statRow(DashboardMetrics m) {
        return new ResponsiveRow(12, 880, 5, 2, List.of(
                Components.stat("Today’s pickups", String.valueOf(m.todaysBookings()), "brand", null),
                Components.stat("Today’s returns", String.valueOf(m.todaysReturns()), "info", null),
                Components.stat("Currently out", String.valueOf(m.outNow()), "success", null),
                Components.stat("Overdue returns", String.valueOf(m.overdue()), "danger",
                        m.overdue() > 0 ? "Needs attention" : null),
                Components.stat("Outstanding", Money.format(m.outstandingPayments()), "warning", null)));
    }

    private Region revenueRow(DashboardMetrics m) {
        AreaChartView chart = new AreaChartView(m.revenueByDay(), 176);
        HBox axis = new HBox(Components.label(m.revenueRangeStart(), "faint-2xs"), Components.hGrow(),
                Components.label(m.revenueRangeEnd(), "faint-2xs"));

        var reports = Components.link("View reports →", "link");
        reports.setOnAction(e -> comingSoon("Reports"));
        HBox footer = new HBox(8, Components.label("Cumulative collections this month", "muted-sm"), Components.hGrow(), reports);
        footer.setAlignment(Pos.CENTER_LEFT);

        VBox chartBox = new VBox(4, chart, axis);
        VBox.setVgrow(chart, Priority.ALWAYS);

        VBox revenue = Components.card("Revenue", "This month · " + Money.format(m.revenue()),
                chartBox, footer);

        List<Node> rows = new ArrayList<>();
        for (ScheduleEntry entry : m.todaysSchedule()) {
            rows.add(scheduleRow(entry));
        }
        VBox schedule = Components.card("Today’s schedule", "Pickups and returns", Components.dividedList(rows));

        return new ResponsiveRow(16, 820, 5, 1, List.of(revenue, schedule), 3, 2);
    }

    private Region attentionRow(DashboardMetrics m) {
        List<Node> rows = new ArrayList<>();
        for (OverdueBooking booking : m.overdueBookings()) {
            rows.add(overdueRow(booking));
        }
        VBox attention = Components.card("Needs attention", "Overdue rentals", Components.dividedList(rows));

        Button manage = Components.secondary("Manage deposits", null);
        manage.setOnAction(e -> comingSoon("Deposits"));

        VBox depositsBody = new VBox(8,
                Components.label(Money.format(m.depositsHeld()), "big-value"),
                Components.label("Refund or deduct after returns.", "muted-sm"),
                spacer(8),
                manage);
        VBox deposits = Components.card("Deposits held", "Security still with you", depositsBody);

        return new ResponsiveRow(16, 820, 5, 1, List.of(attention, deposits), 3, 2);
    }

    // ----------------------------------------------------------- list rows

    private Node scheduleRow(ScheduleEntry entry) {
        HBox heading = new HBox(8,
                Components.label(entry.number(), "font-medium"),
                Components.badge(entry.kind().label(), entry.kind().color(), false));
        heading.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(2, heading, Components.label(entry.customer(), "muted-sm"));

        HBox row = new HBox(12, details, Components.hGrow(), Components.label(entry.time(), "muted-sm"));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 4, 10, 4));
        row.getStyleClass().add("list-row");
        row.setOnMouseClicked(e -> comingSoon("Order " + entry.number()));
        return row;
    }

    private Node overdueRow(OverdueBooking booking) {
        VBox details = new VBox(2,
                Components.label(booking.number() + " · " + booking.customer(), "font-medium"),
                Components.label("Due " + booking.dueDate(), "danger-xs"));

        HBox row = new HBox(12, details, Components.hGrow(), Components.badge(booking.status(), booking.statusColor(), true));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 4, 10, 4));
        row.getStyleClass().addAll("list-row", "list-row-danger");
        row.setOnMouseClicked(e -> comingSoon("Order " + booking.number()));
        return row;
    }

    // ------------------------------------------------------------- helpers

    private Region spacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        return spacer;
    }

    private void comingSoon(String what) {
        router.toasts().info(what, "This screen isn’t part of the UI port — login, register and dashboard are.");
    }

    private record NavGroup(String label, List<NavItem> items) {
    }

    private record NavItem(String label, String icon, boolean active) {
    }
}
