package com.vintorr.javadesktoptemplate.presentation;

import com.vintorr.javadesktoptemplate.config.ApplicationProperties;
import com.vintorr.javadesktoptemplate.presentation.event.StageReadyEvent;
import com.vintorr.javadesktoptemplate.presentation.component.Toasts;
import com.vintorr.javadesktoptemplate.presentation.view.AppView;
import com.vintorr.javadesktoptemplate.presentation.view.DashboardView;
import com.vintorr.javadesktoptemplate.presentation.view.LoginView;
import com.vintorr.javadesktoptemplate.presentation.view.RegisterView;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Owns the primary stage and swaps screens — the presentation layer's entry point and the
 * desktop equivalent of a route table. Views are resolved from the Spring context on
 * navigation, so each screen is built fresh.
 */
@Component
public class Router implements ApplicationListener<StageReadyEvent> {

    private static final String RESOURCE_ROOT = "/com/vintorr/javadesktoptemplate";
    private static final String[] FONTS = {
            "DMSans-400.ttf", "DMSans-500.ttf", "DMSans-600.ttf", "DMSans-700.ttf"
    };

    private final ApplicationContext context;
    private final ApplicationProperties properties;
    private final Toasts toasts;

    private Stage stage;
    private Scene scene;

    public Router(ApplicationContext context, ApplicationProperties properties, Toasts toasts) {
        this.context = context;
        this.properties = properties;
        this.toasts = toasts;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        this.stage = event.stage();

        loadFonts();

        var window = properties.window();
        scene = new Scene(toasts.wrap(new StackPane()), window.width(), window.height());
        scene.getStylesheets().add(resource(RESOURCE_ROOT + "/css/theme.css"));

        stage.setScene(scene);
        stage.setTitle(properties.title());
        stage.setMinWidth(window.minWidth());
        stage.setMinHeight(window.minHeight());
        stage.getIcons().add(new Image(resource(RESOURCE_ROOT + "/images/vintorr-rently-logo.png")));
        stage.centerOnScreen();

        showLogin();
        stage.show();
    }

    public void showLogin() {
        navigate(LoginView.class);
    }

    public void showRegister() {
        navigate(RegisterView.class);
    }

    public void showDashboard() {
        navigate(DashboardView.class);
    }

    private void navigate(Class<? extends AppView> viewType) {
        AppView view = context.getBean(viewType);
        Platform.runLater(() -> {
            scene.setRoot(toasts.wrap(view.view()));
            stage.setTitle(view.title(properties.title()));
        });
    }

    public Toasts toasts() {
        return toasts;
    }

    public ApplicationProperties properties() {
        return properties;
    }

    public Stage stage() {
        return stage;
    }

    private void loadFonts() {
        for (String font : FONTS) {
            try (var in = getClass().getResourceAsStream(RESOURCE_ROOT + "/fonts/" + font)) {
                if (in != null) {
                    Font.loadFont(in, 14);
                }
            } catch (Exception e) {
                // Fall back to the system sans stack declared in theme.css.
            }
        }
    }

    private String resource(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing classpath resource: " + path);
        }
        return url.toExternalForm();
    }
}
