package com.vintorr.javadesktoptemplate.presentation.event;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/** Fired once the JavaFX primary stage exists. */
public class StageReadyEvent extends ApplicationEvent {

    public StageReadyEvent(Stage stage) {
        super(stage);
    }

    public Stage stage() {
        return (Stage) getSource();
    }
}
