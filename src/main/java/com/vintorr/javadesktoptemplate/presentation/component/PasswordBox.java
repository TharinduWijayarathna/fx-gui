package com.vintorr.javadesktoptemplate.presentation.component;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

/**
 * Port of x-ui.password-input: a password field with an eye toggle pinned to the right.
 * JavaFX has no "reveal" mode, so a PasswordField and a TextField share one text property
 * and we swap which of the two is visible.
 */
public class PasswordBox extends StackPane {

    private final PasswordField masked = new PasswordField();
    private final TextField revealed = new TextField();
    private final Button toggle = new Button();
    private boolean showing = false;

    public PasswordBox() {
        masked.getStyleClass().addAll("form-input", "form-input-password");
        revealed.getStyleClass().addAll("form-input", "form-input-password");
        revealed.textProperty().bindBidirectional(masked.textProperty());

        revealed.setVisible(false);
        revealed.setManaged(false);

        toggle.getStyleClass().add("eye-btn");
        toggle.setGraphic(Icons.of("eye", 16, "icon-muted"));
        toggle.setFocusTraversable(false);
        toggle.setOnAction(e -> setShowing(!showing));
        StackPane.setAlignment(toggle, Pos.CENTER_RIGHT);

        getChildren().addAll(masked, revealed, toggle);
        setAlignment(Pos.CENTER_LEFT);
        setMaxWidth(Double.MAX_VALUE);
    }

    private void setShowing(boolean show) {
        this.showing = show;
        masked.setVisible(!show);
        masked.setManaged(!show);
        revealed.setVisible(show);
        revealed.setManaged(show);
        toggle.setGraphic(Icons.of(show ? "eye-off" : "eye", 16, "icon-muted"));
        (show ? revealed : masked).requestFocus();
        (show ? revealed : masked).end();
    }

    public StringProperty textProperty() {
        return masked.textProperty();
    }

    public String getText() {
        return masked.getText() == null ? "" : masked.getText();
    }

    public void clear() {
        masked.clear();
    }

    public void setPromptText(String prompt) {
        masked.setPromptText(prompt);
        revealed.setPromptText(prompt);
    }

    /** Mirrors .form-input.has-error */
    public void setInvalid(boolean invalid) {
        for (var node : new javafx.scene.Node[] { masked, revealed }) {
            if (invalid) {
                if (!node.getStyleClass().contains("has-error")) {
                    node.getStyleClass().add("has-error");
                }
            } else {
                node.getStyleClass().remove("has-error");
            }
        }
    }

    @Override
    public void requestFocus() {
        (showing ? revealed : masked).requestFocus();
    }
}
