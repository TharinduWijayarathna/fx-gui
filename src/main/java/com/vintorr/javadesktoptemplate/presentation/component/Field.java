package com.vintorr.javadesktoptemplate.presentation.component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Port of x-ui.field: label, control, then an error or hint line underneath.
 */
public class Field extends VBox {

    private final Node control;
    private final Label error = new Label();
    private final Label hint = new Label();

    public Field(String labelText, Node control) {
        this(labelText, control, null);
    }

    public Field(String labelText, Node control, String hintText) {
        this.control = control;
        setSpacing(6);

        if (labelText != null) {
            Label label = new Label(labelText);
            label.getStyleClass().add("form-label");
            getChildren().add(label);
        }

        if (control instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
        }
        getChildren().add(control);

        hint.getStyleClass().add("form-hint");
        hint.setWrapText(true);
        hint.setVisible(false);
        hint.setManaged(false);
        if (hintText != null) {
            hint.setText(hintText);
            hint.setVisible(true);
            hint.setManaged(true);
        }

        error.getStyleClass().add("form-error");
        error.setWrapText(true);
        error.setVisible(false);
        error.setManaged(false);

        getChildren().addAll(hint, error);
    }

    public void setError(String message) {
        boolean has = message != null && !message.isBlank();
        error.setText(has ? message : "");
        error.setVisible(has);
        error.setManaged(has);
        hint.setVisible(!has && !hint.getText().isBlank());
        hint.setManaged(!has && !hint.getText().isBlank());

        if (control instanceof PasswordBox box) {
            box.setInvalid(has);
        } else if (has) {
            if (!control.getStyleClass().contains("has-error")) {
                control.getStyleClass().add("has-error");
            }
        } else {
            control.getStyleClass().remove("has-error");
        }
    }

    public void clearError() {
        setError(null);
    }

    public Node getControl() {
        return control;
    }
}
