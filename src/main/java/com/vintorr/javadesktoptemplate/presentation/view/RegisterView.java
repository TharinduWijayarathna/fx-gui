package com.vintorr.javadesktoptemplate.presentation.view;

import com.vintorr.javadesktoptemplate.domain.exception.ValidationException;
import com.vintorr.javadesktoptemplate.service.AuthenticationService;
import com.vintorr.javadesktoptemplate.service.dto.RegisterRequest;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** Port of auth/register.blade.php. */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegisterView implements AppView {

    private final AuthenticationService authentication;
    private final Router router;

    private final TextField name = Components.input("Jane Doe");
    private final TextField business = Components.input("Acme Industries");
    private final TextField email = Components.input("you@example.com");
    private final TextField referral = Components.input(null);
    private final PasswordBox password = new PasswordBox();
    private final PasswordBox confirmation = new PasswordBox();

    private final Field nameField = new Field("Your name", name);
    private final Field businessField = new Field("Organisation", business);
    private final Field emailField = new Field("Email", email);
    private final Field referralField = new Field("Referral code (optional)", referral);
    private final Field passwordField = new Field("Password", password, "At least 8 characters.");
    private final Field confirmationField = new Field("Confirm password", confirmation);

    public RegisterView(AuthenticationService authentication, Router router) {
        this.authentication = authentication;
        this.router = router;
    }

    @Override
    public String title(String applicationTitle) {
        return "Register · " + applicationTitle;
    }

    @Override
    public Parent view() {
        referral.textProperty().addListener((obs, was, is) -> {
            if (is != null && !is.equals(is.toUpperCase())) {
                referral.setText(is.toUpperCase());
            }
        });

        Button submit = Components.primary("Register", "arrow-right", true);
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setDefaultButton(true);
        submit.setOnAction(e -> submit());

        Hyperlink login = Components.link("Already registered?", "link-underline");
        login.setOnAction(e -> router.showLogin());

        HBox footer = new HBox(login);
        footer.setAlignment(Pos.CENTER);

        VBox form = new VBox(16,
                nameField, businessField, emailField, referralField,
                passwordField, confirmationField, submit, footer);

        Platform.runLater(name::requestFocus);
        return AuthShell.wrap(router.properties().title(), router.properties().tagline(), form);
    }

    private void submit() {
        for (Field field : new Field[] {
                nameField, businessField, emailField, referralField, passwordField, confirmationField }) {
            field.clearError();
        }

        try {
            var user = authentication.register(new RegisterRequest(
                    name.getText(),
                    business.getText(),
                    email.getText(),
                    referral.getText(),
                    password.getText(),
                    confirmation.getText()));

            router.toasts().success("Saved", "Welcome to " + router.properties().title() + ", " + user.name() + ".");
            router.showDashboard();
        } catch (ValidationException e) {
            fieldFor(e.field()).setError(e.getMessage());
            router.toasts().error("Please fix the following:", e.getMessage());
        }
    }

    private Field fieldFor(String field) {
        return switch (field) {
            case "name" -> nameField;
            case "businessName" -> businessField;
            case "password" -> passwordField;
            case "passwordConfirmation" -> confirmationField;
            default -> emailField;
        };
    }
}
