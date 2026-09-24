package com.vintorr.javadesktoptemplate.presentation.view;

import com.vintorr.javadesktoptemplate.domain.exception.ValidationException;
import com.vintorr.javadesktoptemplate.service.AuthenticationService;
import com.vintorr.javadesktoptemplate.service.dto.LoginRequest;
import com.vintorr.javadesktoptemplate.presentation.view.AppView;
import com.vintorr.javadesktoptemplate.presentation.component.AuthShell;
import com.vintorr.javadesktoptemplate.presentation.component.Field;
import com.vintorr.javadesktoptemplate.presentation.component.PasswordBox;
import com.vintorr.javadesktoptemplate.presentation.Router;
import com.vintorr.javadesktoptemplate.presentation.component.Components;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** Port of auth/login.blade.php. */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LoginView implements AppView {

    private final AuthenticationService authentication;
    private final Router router;

    private final TextField email = Components.input("you@example.com");
    private final PasswordBox password = new PasswordBox();
    private final Field emailField = new Field("Email", email);
    private final Field passwordField = new Field("Password", password);

    public LoginView(AuthenticationService authentication, Router router) {
        this.authentication = authentication;
        this.router = router;
    }

    @Override
    public String title(String applicationTitle) {
        return "Log in · " + applicationTitle;
    }

    @Override
    public Parent view() {
        CheckBox remember = new CheckBox("Remember me");
        remember.getStyleClass().add("check");

        Hyperlink forgot = Components.link("Forgot your password?", "link");
        forgot.setOnAction(e -> router.toasts().info(
                "Password reset",
                "We'd email you a reset link — not wired up in this UI build."));

        HBox options = new HBox(12, remember, Components.hGrow(), forgot);
        options.setAlignment(Pos.CENTER_LEFT);

        Button submit = Components.primary("Log in", "arrow-right", true);
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setDefaultButton(true);
        submit.setOnAction(e -> submit(remember.isSelected()));

        Hyperlink register = Components.link("Sign up", "link");
        register.setOnAction(e -> router.showRegister());

        HBox footer = new HBox(4,
                Components.label("New to " + router.properties().title() + "?", "muted-sm"), register);
        footer.setAlignment(Pos.CENTER);

        VBox form = new VBox(16, emailField, passwordField, options, submit, footer);

        Platform.runLater(email::requestFocus);
        return AuthShell.wrap(router.properties().title(), router.properties().tagline(), form);
    }

    private void submit(boolean remember) {
        emailField.clearError();
        passwordField.clearError();

        try {
            var user = authentication.login(new LoginRequest(email.getText(), password.getText(), remember));
            router.toasts().success("Welcome back", "Signed in as " + user.name() + ".");
            router.showDashboard();
        } catch (ValidationException e) {
            switch (e.field()) {
                case "password" -> passwordField.setError(e.getMessage());
                default -> emailField.setError(e.getMessage());
            }
            router.toasts().error("Please fix the following:", e.getMessage());
        }
    }
}
