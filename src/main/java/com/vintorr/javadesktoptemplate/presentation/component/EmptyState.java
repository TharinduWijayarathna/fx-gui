package com.vintorr.javadesktoptemplate.presentation.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Port of x-ui.empty — the dashed placeholder a list falls back to when it has no rows. */
public final class EmptyState {

    private EmptyState() {
    }

    public static Region of(String title, String description, Node action) {
        StackPane icon = new StackPane(Icons.of("inbox", 20, "icon-brand"));
        icon.getStyleClass().add("empty-icon");
        icon.setMinSize(44, 44);
        icon.setPrefSize(44, 44);
        icon.setMaxSize(44, 44);

        VBox box = new VBox(6, icon, Components.label(title, "empty-title"));
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40, 24, 40, 24));
        box.getStyleClass().add("empty-state");

        if (description != null) {
            var text = Components.wrapped(description, "muted-sm");
            text.setMaxWidth(380);
            text.setAlignment(Pos.CENTER);
            text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            box.getChildren().add(text);
        }
        if (action != null) {
            VBox.setMargin(action, new Insets(10, 0, 0, 0));
            box.getChildren().add(action);
        }
        return box;
    }
}
