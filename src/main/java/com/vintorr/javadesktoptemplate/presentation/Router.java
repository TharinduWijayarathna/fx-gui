package com.vintorr.javadesktoptemplate.presentation;

import com.vintorr.javadesktoptemplate.config.ApplicationProperties;
import com.vintorr.javadesktoptemplate.presentation.component.Brand;
import com.vintorr.javadesktoptemplate.presentation.component.Toasts;
import com.vintorr.javadesktoptemplate.presentation.event.StageReadyEvent;
import com.vintorr.javadesktoptemplate.presentation.view.AppView;
import com.vintorr.javadesktoptemplate.presentation.view.DashboardView;
import com.vintorr.javadesktoptemplate.presentation.view.LoginView;
import com.vintorr.javadesktoptemplate.presentation.view.PeopleView;
import com.vintorr.javadesktoptemplate.presentation.view.ProfileView;
import com.vintorr.javadesktoptemplate.presentation.view.RegisterView;
import com.vintorr.javadesktoptemplate.service.AuthenticationService;
import com.vintorr.javadesktoptemplate.service.SessionService;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Owns the primary stage and swaps screens — the presentation layer's entry point and the
 * desktop equivalent of a route table. Views are resolved from the Spring context on
 * navigation, so each screen is built fresh, and {@link Route#requiresAuthentication()}
 * is enforced here rather than in every view.
 */
@Component
public class Router implements ApplicationListener<StageReadyEvent> {

    private static final String RESOURCE_ROOT = "/com/vintorr/javadesktoptemplate";
    private static final String[] FONTS = {
            "DMSans-400.ttf", "DMSans-500.ttf", "DMSans-600.ttf", "DMSans-700.ttf"
    };

    private final ApplicationContext context;
    private final ApplicationProperties properties;
    private final SessionService session;
    private final AuthenticationService authentication;
    private final Toasts toasts;

    private Stage stage;
    private Scene scene;

    public Router(ApplicationContext context, ApplicationProperties properties, SessionService session,
                  AuthenticationService authentication, Toasts toasts) {
        this.context = context;
        this.properties = properties;
        this.session = session;
        this.authentication = authentication;
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
        stage.getIcons().add(Brand.icon(128));
        stage.centerOnScreen();

        show(Route.LOGIN);
        stage.show();
    }

    /** The route table. */
    public void show(Route route) {
        if (route.requiresAuthentication() && !session.isAuthenticated()) {
            toasts.info("Session ended", "Please sign in again.");
            navigate(LoginView.class);
            return;
        }

        switch (route) {
            case LOGIN -> navigate(LoginView.class);
            case REGISTER -> navigate(RegisterView.class);
            case DASHBOARD -> navigate(DashboardView.class);
            case PEOPLE -> navigate(PeopleView.class);
            case PROFILE -> navigate(ProfileView.class);
        }
    }

    public void showLogin() {
        show(Route.LOGIN);
    }

    public void showRegister() {
        show(Route.REGISTER);
    }

    public void showDashboard() {
        show(Route.DASHBOARD);
    }

    /** Ends the session and returns to the login screen. */
    public void logout() {
        authentication.logout();
        show(Route.LOGIN);
        toasts.info("Signed out", "You have been signed out.");
    }

    /** What the placeholder nav entries and toolbar buttons do in this template. */
    public void placeholder(String what) {
        toasts.info(what, "Placeholder screen — wire this route up to your own view.");
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

    public SessionService session() {
        return session;
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
