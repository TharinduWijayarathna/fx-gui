package com.vintorr.javadesktoptemplate;

import com.vintorr.javadesktoptemplate.presentation.event.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Bridges the JavaFX lifecycle to the Spring lifecycle:
 * init()  -> boot the Spring context
 * start() -> publish {@link StageReadyEvent} so Spring beans can take the primary stage
 * stop()  -> close the context and exit
 */
public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        this.context = new SpringApplicationBuilder(JavaDesktopTemplateApplication.class)
                .web(org.springframework.boot.WebApplicationType.NONE)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) {
        context.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
