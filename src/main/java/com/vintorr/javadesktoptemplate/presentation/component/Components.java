package com.vintorr.javadesktoptemplate.presentation.component;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Factories for the components in resources/views/components/ui/*.blade.php.
 * Styling lives in css/theme.css so the class names read like the web app's.
 */
public final class Components {

    private Components() {
    }

    // ------------------------------------------------------------- text

    public static Label label(String text, String... styleClasses) {
        Label label = new Label(text);
        label.getStyleClass().addAll(styleClasses);
        return label;
    }

    public static Label wrapped(String text, String... styleClasses) {
        Label label = label(text, styleClasses);
        label.setWrapText(true);
        return label;
    }

    // ---------------------------------------------------------- spacing

    public static Region hGrow() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public static Region vGrow() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    // ---------------------------------------------------------- buttons

    /** x-ui.button variant=primary */
    public static Button primary(String text, String icon, boolean iconRight) {
        return button(text, icon, iconRight, "btn", "btn-primary");
    }

    public static Button primary(String text) {
        return primary(text, null, false);
    }

    /** x-ui.button variant=secondary */
    public static Button secondary(String text, String icon) {
        return button(text, icon, false, "btn", "btn-secondary");
    }

    /** x-ui.button variant=danger */
    public static Button danger(String text, String icon) {
        return button(text, icon, false, "btn", "btn-danger");
    }

    public static Button ghost(String text, String icon) {
        return button(text, icon, false, "btn", "btn-ghost");
    }

    /** x-ui.button size=sm */
    public static Button small(String text, String icon, String variant) {
        Button button = button(text, icon, false, "btn", "btn-" + variant, "btn-sm");
        if (button.getGraphic() instanceof Region region) {
            region.setMinSize(14, 14);
            region.setPrefSize(14, 14);
            region.setMaxSize(14, 14);
        }
        return button;
    }

    private static Button button(String text, String icon, boolean iconRight, String... styleClasses) {
        Button button = new Button(text);
        button.getStyleClass().addAll(styleClasses);
        if (icon != null) {
            button.setGraphic(Icons.of(icon, 16));
            button.setContentDisplay(iconRight
                    ? javafx.scene.control.ContentDisplay.RIGHT
                    : javafx.scene.control.ContentDisplay.LEFT);
        }
        return button;
    }

    public static Button iconButton(String icon) {
        Button button = new Button();
        button.getStyleClass().add("icon-btn");
        button.setGraphic(Icons.of(icon, 18, "icon-muted"));
        return button;
    }

    // ----------------------------------------------------------- inputs

    /** x-ui.input */
    public static TextField input(String prompt) {
        TextField field = new TextField();
        field.getStyleClass().add("form-input");
        if (prompt != null) {
            field.setPromptText(prompt);
        }
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    /** x-ui.input type=search — a text field with the magnifier tucked inside it. */
    public static StackPane searchField(TextField field) {
        field.getStyleClass().add("form-input-search");
        Region icon = Icons.of("search", 16, "icon-muted");
        StackPane wrap = new StackPane(field, icon);
        StackPane.setAlignment(icon, Pos.CENTER_LEFT);
        StackPane.setMargin(icon, new Insets(0, 0, 0, 14));
        wrap.setAlignment(Pos.CENTER_LEFT);
        wrap.setMaxWidth(Double.MAX_VALUE);
        return wrap;
    }

    /** x-ui.select */
    public static ComboBox<String> select(List<String> options, String selected) {
        ComboBox<String> select = new ComboBox<>(FXCollections.observableArrayList(options));
        select.getStyleClass().add("form-select");
        select.setMaxWidth(Double.MAX_VALUE);
        select.setValue(selected);
        return select;
    }

    // ------------------------------------------------------------ cards

    /** x-ui.card — title/subtitle header, body, optional footer. */
    public static VBox card(String title, String subtitle, Node body, Node footer, Node... headerActions) {
        VBox card = new VBox();
        card.getStyleClass().add("card");

        if (title != null) {
            VBox titles = new VBox(2);
            titles.getChildren().add(label(title, "section-title"));
            if (subtitle != null) {
                titles.getChildren().add(label(subtitle, "muted-sm"));
            }

            HBox header = new HBox(12, titles, hGrow());
            if (headerActions != null && headerActions.length > 0) {
                HBox actions = new HBox(8, headerActions);
                actions.setAlignment(Pos.CENTER_RIGHT);
                header.getChildren().add(actions);
            }
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(14, 20, 14, 20));
            header.getStyleClass().add("card-divider");
            card.getChildren().add(header);
        }

        VBox bodyWrap = new VBox(body);
        bodyWrap.setPadding(new Insets(20));
        VBox.setVgrow(bodyWrap, Priority.ALWAYS);
        VBox.setVgrow(body, Priority.ALWAYS);
        card.getChildren().add(bodyWrap);

        if (footer != null) {
            VBox footerWrap = new VBox(footer);
            footerWrap.setPadding(new Insets(12, 20, 12, 20));
            footerWrap.getStyleClass().add("card-footer");
            card.getChildren().add(footerWrap);
        }
        return card;
    }

    public static VBox card(String title, String subtitle, Node body) {
        return card(title, subtitle, body, null);
    }

    /** x-ui.stat — tone is one of default/brand/success/warning/danger/info. */
    public static VBox stat(String statLabel, String value, String tone, String hint) {
        VBox stat = new VBox(6);
        stat.getStyleClass().addAll("card", "stat-card", "stat-" + tone);
        stat.setPadding(new Insets(16));

        // Long currency values need to step down a size to stay on one line in a 1/5 column.
        Label valueLabel = label(value, value.length() > 8 ? "stat-value-sm" : "stat-value");
        stat.getChildren().addAll(label(statLabel.toUpperCase(), "meta"), valueLabel);
        if (hint != null) {
            stat.getChildren().add(label(hint, "muted-xs"));
        }
        return stat;
    }

    /** x-ui.badge */
    public static Node badge(String text, String color, boolean dot) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("badge", "badge-" + color);
        if (dot) {
            javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(3);
            circle.setOpacity(0.8);
            circle.getStyleClass().add("badge-dot");
            circle.setFill(javafx.scene.paint.Color.web(dotColor(color)));
            badge.setGraphic(circle);
            badge.setGraphicTextGap(5);
        }
        return badge;
    }

    private static String dotColor(String color) {
        return switch (color) {
            case "success" -> "#047857";
            case "info" -> "#0369a1";
            case "warning" -> "#b45309";
            case "danger" -> "#be123c";
            case "violet" -> "#6d28d9";
            case "brand" -> "#c81414";
            default -> "#334155";
        };
    }

    /** x-ui.avatar */
    public static StackPane avatar(String name, double size) {
        String initial = (name == null || name.isBlank() ? "U" : name.trim().substring(0, 1)).toUpperCase();
        Label label = new Label(initial);
        StackPane avatar = new StackPane(label);
        avatar.getStyleClass().add("avatar");
        avatar.setMinSize(size, size);
        avatar.setPrefSize(size, size);
        avatar.setMaxSize(size, size);
        return avatar;
    }

    // ------------------------------------------------------- page chrome

    /** x-ui.breadcrumb — Home › … › current */
    public static HBox breadcrumb(List<String> crumbs) {
        HBox bar = new HBox(6);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getChildren().add(link("Home", "link-muted"));
        for (int i = 0; i < crumbs.size(); i++) {
            bar.getChildren().add(Icons.of("chevron-right", 12, "icon-faint"));
            boolean last = i == crumbs.size() - 1;
            bar.getChildren().add(last
                    ? label(crumbs.get(i), "font-medium", "crumb-current")
                    : link(crumbs.get(i), "link-muted"));
        }
        return bar;
    }

    /** x-ui.page-header */
    public static VBox pageHeader(String title, String subtitle, List<String> crumbs, Node... actions) {
        VBox header = new VBox(8);

        if (crumbs != null && !crumbs.isEmpty()) {
            header.getChildren().add(breadcrumb(crumbs));
        }

        VBox titles = new VBox(4, label(title, "page-title"));
        if (subtitle != null) {
            titles.getChildren().add(label(subtitle, "page-subtitle"));
        }

        HBox row = new HBox(16, titles, hGrow());
        row.setAlignment(Pos.BOTTOM_LEFT);
        if (actions != null && actions.length > 0) {
            HBox actionBar = new HBox(8, actions);
            actionBar.setAlignment(Pos.CENTER_RIGHT);
            row.getChildren().add(actionBar);
        }
        header.getChildren().add(row);
        return header;
    }

    public static javafx.scene.control.Hyperlink link(String text, String... styleClasses) {
        javafx.scene.control.Hyperlink link = new javafx.scene.control.Hyperlink(text);
        link.getStyleClass().addAll(styleClasses);
        return link;
    }

    /** A vertically scrolling, transparent scroll pane that fits its content width. */
    public static ScrollPane scroller(Node content) {
        ScrollPane pane = new ScrollPane(content);
        pane.setFitToWidth(true);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.getStyleClass().add("scroll-pane");
        return pane;
    }

    /** divide-y divide-stone-100: a top border on every row but the first. */
    public static VBox dividedList(List<Node> rows) {
        VBox list = new VBox();
        for (int i = 0; i < rows.size(); i++) {
            Node row = rows.get(i);
            if (i > 0 && row instanceof Region region) {
                region.getStyleClass().add("row-divider");
            }
            list.getChildren().add(row);
        }
        return list;
    }
}
