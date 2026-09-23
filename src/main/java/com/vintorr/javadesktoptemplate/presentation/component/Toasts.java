package com.vintorr.javadesktoptemplate.presentation.component;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

/**
 * Port of the toast stack in layouts/app.blade.php — top-right, auto-dismissing.
 */
@Component
public class Toasts {

    private final VBox layer = new VBox(8);

    public Toasts() {
        layer.setAlignment(Pos.TOP_RIGHT);
        layer.setPadding(new Insets(20, 24, 20, 24));
        layer.setPickOnBounds(false);
        StackPane.setAlignment(layer, Pos.TOP_RIGHT);
    }

    /** Puts the toast layer above a screen. */
    public Parent wrap(Node screen) {
        StackPane stack = new StackPane(screen, layer);
        stack.setPickOnBounds(false);
        return stack;
    }

    public void success(String title, String message) {
        show("success", "check-circle", title, message);
    }

    public void error(String title, String message) {
        show("error", "x", title, message);
    }

    public void info(String title, String message) {
        show("info", "sparkles", title, message);
    }

    private void show(String type, String icon, String title, String message) {
        VBox text = new VBox(2, Components.label(title, "toast-title"), Components.wrapped(message, "muted-sm"));
        text.setMaxWidth(260);

        HBox toast = new HBox(12, Icons.of(icon, 18, iconClass(type)), text);
        toast.getStyleClass().addAll("toast", "toast-" + type);
        toast.setMaxWidth(340);
        toast.setOpacity(0);

        layer.getChildren().add(toast);

        FadeTransition in = new FadeTransition(Duration.millis(140), toast);
        in.setToValue(1);
        in.play();

        PauseTransition hold = new PauseTransition(Duration.millis(4200));
        hold.setOnFinished(e -> {
            FadeTransition out = new FadeTransition(Duration.millis(200), toast);
            out.setToValue(0);
            out.setOnFinished(done -> layer.getChildren().remove(toast));
            out.play();
        });
        hold.play();
    }

    private String iconClass(String type) {
        return switch (type) {
            case "success" -> "icon-success";
            case "error" -> "icon-danger";
            default -> "icon-brand";
        };
    }

    public void clear() {
        layer.getChildren().clear();
    }
}
