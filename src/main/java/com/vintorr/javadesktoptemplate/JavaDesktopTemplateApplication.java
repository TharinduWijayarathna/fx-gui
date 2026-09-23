package com.vintorr.javadesktoptemplate;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point. Spring Boot is started from {@link JavaFxApplication#init()} so the
 * JavaFX toolkit owns the main thread and Spring owns the beans.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class JavaDesktopTemplateApplication {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}
