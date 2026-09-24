package com.vintorr.javadesktoptemplate.presentation.component;

import java.util.List;

import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.presentation.Route;
import com.vintorr.javadesktoptemplate.presentation.Router;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

/**
 * The signed-in chrome every page sits in: the sidebar, the topbar and the scrolling
 * content column. Pages hand it their body and say which nav entry is current.
 *
 * <p>The nav is placeholder content for a template — the entries that have a {@link Route}
 * navigate, the rest raise a toast so the shell can be clicked through end to end.</p>
 */
public final class AppShell {

    private static final double SIDEBAR_WIDTH = 280;
    private static final double BAR_HEIGHT = 64;
    private static final double CONTENT_MAX_WIDTH = 1280;

    /** Dummy sections — rename them, drop them, or point them at your own routes. */
    private static final List<NavGroup> NAV = List.of(
            new NavGroup("Overview", List.of(
                    new NavItem("Dashboard", "home", Route.DASHBOARD),
                    new NavItem("Reports", "chart", null))),
            new NavGroup("Workspace", List.of(
                    new NavItem("People", "users", Route.PEOPLE),
                    new NavItem("Projects", "package", null),
                    new NavItem("Calendar", "calendar", null))),
            new NavGroup("Content", List.of(
                    new NavItem("Documents", "document", null),
                    new NavItem("Tags", "tag", null))),
            new NavGroup("Account", List.of(
                    new NavItem("Profile", "user", Route.PROFILE),
                    new NavItem("Settings", "cog", null),
                    new NavItem("Billing", "card", null))));

    private AppShell() {
    }

    /** Wraps a page body in the sidebar + topbar chrome. */
    public static Parent wrap(Router router, Route active, Node content) {
        User user = router.session().currentUser();

        VBox column = new VBox(topbar(router, user), content(content));
        HBox.setHgrow(column, Priority.ALWAYS);

        HBox shell = new HBox(sidebar(router, active), column);
        shell.setFillHeight(true);
        return shell;
    }

    // ------------------------------------------------------------- sidebar

    private static Region sidebar(Router router, Route active) {
        HBox header = new HBox(Brand.wordmark(router.properties().title(), 32, "brand-wordmark"));
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
                items.getChildren().add(navLink(router, item, active));
            }

            Label groupLabel = Components.label(group.label().toUpperCase(), "meta");
            groupLabel.setPadding(new Insets(0, 12, 6, 12));
            nav.getChildren().add(new VBox(0, groupLabel, items));
        }

        var scroller = Components.scroller(nav);
        VBox.setVgrow(scroller, Priority.ALWAYS);

        VBox footer = new VBox(navLink(router, new NavItem("Help & support", "support", null), active));
        footer.setPadding(new Insets(12));
        footer.getStyleClass().add("sidebar-footer");

        VBox sidebar = new VBox(header, scroller, footer);
        sidebar.setMinWidth(SIDEBAR_WIDTH);
        sidebar.setPrefWidth(SIDEBAR_WIDTH);
        sidebar.setMaxWidth(SIDEBAR_WIDTH);
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private static HBox navLink(Router router, NavItem item, Route active) {
        HBox link = new HBox(12, Icons.nav(item.icon()), new Label(item.label()));
        link.setAlignment(Pos.CENTER_LEFT);
        link.getStyleClass().add("nav-link");

        if (item.route() != null && item.route() == active) {
            link.getStyleClass().add("nav-link-active");
        } else if (item.route() != null) {
            link.setOnMouseClicked(e -> router.show(item.route()));
        } else {
            link.setOnMouseClicked(e -> router.placeholder(item.label()));
        }
        return link;
    }

    // -------------------------------------------------------------- topbar

    private static Region topbar(Router router, User user) {
        Button create = Components.small("New", "plus", "primary");
        create.setOnAction(e -> router.placeholder("New record"));

        Button notifications = Components.iconButton("bell");
        notifications.setOnAction(e -> router.placeholder("Notifications"));

        HBox bar = new HBox(12, Components.hGrow(), create, notifications, userChip(router, user));
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(0, 24, 0, 24));
        bar.setMinHeight(BAR_HEIGHT);
        bar.setPrefHeight(BAR_HEIGHT);
        bar.getStyleClass().add("topbar");
        return bar;
    }

    private static Region userChip(Router router, User user) {
        String name = user == null ? "Guest" : user.name();
        HBox chip = new HBox(8, Components.avatar(name, 32), Components.label(name, "font-medium"),
                Icons.of("chevron-down", 14, "icon-muted"));
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.getStyleClass().add("user-chip");

        Popup menu = userMenu(router, user);
        chip.setOnMouseClicked(e -> {
            if (menu.isShowing()) {
                menu.hide();
                return;
            }
            var bounds = chip.localToScreen(chip.getBoundsInLocal());
            menu.show(chip, bounds.getMaxX() - 232, bounds.getMaxY() + 8);
        });
        return chip;
    }

    private static Popup userMenu(Router router, User user) {
        VBox items = new VBox();
        items.getStyleClass().add("app-menu");
        items.setPrefWidth(232);

        Popup popup = new Popup();
        popup.setAutoHide(true);

        VBox identity = new VBox(2,
                Components.label(user == null ? "Guest" : user.name(), "font-medium"),
                Components.label(user == null ? "" : user.email(), "muted-xs"));
        identity.setPadding(new Insets(12, 16, 12, 16));
        identity.getStyleClass().add("card-divider");

        items.getChildren().addAll(
                identity,
                menuItem("Profile", "user", () -> router.show(Route.PROFILE), popup),
                menuItem("Settings", "cog", () -> router.placeholder("Settings"), popup),
                separator(),
                menuItem("Sign out", "logout", router::logout, popup));

        popup.getContent().add(items);
        return popup;
    }

    private static Label menuItem(String text, String icon, Runnable action, Popup popup) {
        Label item = new Label(text, Icons.of(icon, 16, "icon-muted"));
        item.getStyleClass().add("app-menu-item");
        item.setGraphicTextGap(10);
        item.setMaxWidth(Double.MAX_VALUE);
        item.setOnMouseClicked(e -> {
            popup.hide();
            action.run();
        });
        return item;
    }

    private static Region separator() {
        Region line = new Region();
        line.setMinHeight(1);
        line.setPrefHeight(1);
        line.getStyleClass().add("menu-separator");
        VBox.setMargin(line, new Insets(4, 0, 4, 0));
        return line;
    }

    // ------------------------------------------------------------- content

    private static Region content(Node body) {
        VBox page = new VBox(body);
        page.setPadding(new Insets(24, 32, 32, 32));
        page.setMaxWidth(CONTENT_MAX_WIDTH);
        VBox.setVgrow(body, Priority.ALWAYS);

        StackPane centered = new StackPane(page);
        StackPane.setAlignment(page, Pos.TOP_CENTER);

        var scroller = Components.scroller(centered);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        return scroller;
    }

    private record NavGroup(String label, List<NavItem> items) {
    }

    private record NavItem(String label, String icon, Route route) {
    }
}
