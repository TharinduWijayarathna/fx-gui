package com.vintorr.javadesktoptemplate.presentation.view;

import java.util.List;

import com.vintorr.javadesktoptemplate.domain.exception.ValidationException;
import com.vintorr.javadesktoptemplate.domain.model.User;
import com.vintorr.javadesktoptemplate.presentation.Route;
import com.vintorr.javadesktoptemplate.presentation.Router;
import com.vintorr.javadesktoptemplate.presentation.component.AppShell;
import com.vintorr.javadesktoptemplate.presentation.component.Components;
import com.vintorr.javadesktoptemplate.presentation.component.Field;
import com.vintorr.javadesktoptemplate.presentation.component.PasswordBox;
import com.vintorr.javadesktoptemplate.presentation.component.ResponsiveRow;
import com.vintorr.javadesktoptemplate.service.ProfileService;
import com.vintorr.javadesktoptemplate.service.SessionService;
import com.vintorr.javadesktoptemplate.service.dto.ChangePasswordRequest;
import com.vintorr.javadesktoptemplate.service.dto.UpdateProfileRequest;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The account screen behind the topbar menu, and the one place in this template where a
 * form actually persists: saving updates the signed-in user through {@link ProfileService},
 * so the sidebar, the avatar and the chip pick the change up on the next navigation.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProfileView implements AppView {

    private final SessionService session;
    private final ProfileService profile;
    private final Router router;

    private final TextField name = Components.input("Your name");
    private final TextField email = Components.input("you@example.com");
    private final TextField organisation = Components.input("Your organisation");
    private final PasswordBox currentPassword = new PasswordBox();
    private final PasswordBox newPassword = new PasswordBox();
    private final PasswordBox confirmation = new PasswordBox();

    private final Field nameField = new Field("Name", name);
    private final Field emailField = new Field("Email", email);
    private final Field organisationField = new Field("Organisation", organisation);
    private final Field currentPasswordField = new Field("Current password", currentPassword);
    private final Field newPasswordField = new Field("New password", newPassword, "At least 8 characters.");
    private final Field confirmationField = new Field("Confirm password", confirmation);

    public ProfileView(SessionService session, ProfileService profile, Router router) {
        this.session = session;
        this.profile = profile;
        this.router = router;
    }

    @Override
    public String title(String applicationTitle) {
        return "Profile · " + applicationTitle;
    }

    @Override
    public Parent view() {
        User user = session.currentUser();
        name.setText(user == null ? "" : user.name());
        email.setText(user == null ? "" : user.email());
        organisation.setText(user == null ? "" : user.businessName());

        VBox page = new VBox(20,
                Components.pageHeader("Profile", "Your account details for this workspace.",
                        List.of("Account", "Profile"), signOutButton()),
                identityCard(user),
                new ResponsiveRow(16, 900, 2, 1, List.of(detailsCard(), passwordCard())));

        return AppShell.wrap(router, Route.PROFILE, page);
    }

    // --------------------------------------------------------------- cards

    private Region identityCard(User user) {
        VBox text = new VBox(2,
                Components.label(user == null ? "Guest" : user.name(), "section-title"),
                Components.label(user == null ? "" : user.email() + " · " + user.businessName(), "muted-sm"));

        HBox row = new HBox(16, Components.avatar(user == null ? "?" : user.name(), 56), text,
                Components.hGrow(), Components.badge("Signed in", "success", true));
        row.setAlignment(Pos.CENTER_LEFT);
        return Components.card(null, null, row);
    }

    private Region detailsCard() {
        Button save = Components.primary("Save changes", "check", false);
        save.setOnAction(e -> saveProfile());

        VBox body = new VBox(16, nameField, emailField, organisationField, actionRow(save));
        return Components.card("Profile information", "Update your name, email and organisation.", body);
    }

    private Region passwordCard() {
        Button save = Components.primary("Update password", "shield-check", false);
        save.setOnAction(e -> savePassword());

        VBox body = new VBox(16, currentPasswordField, newPasswordField, confirmationField, actionRow(save));
        return Components.card("Update password", "Use a long, random password to stay secure.", body);
    }

    private Region actionRow(Button action) {
        HBox row = new HBox(action);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Button signOutButton() {
        Button signOut = Components.danger("Sign out", "logout");
        signOut.setOnAction(e -> router.logout());
        return signOut;
    }

    // ------------------------------------------------------------ handlers

    private void saveProfile() {
        for (Field field : new Field[] { nameField, emailField, organisationField }) {
            field.clearError();
        }

        try {
            User updated = profile.updateProfile(
                    new UpdateProfileRequest(name.getText(), email.getText(), organisation.getText()));
            router.toasts().success("Profile updated", "Saved as " + updated.name() + ".");
            router.show(Route.PROFILE);
        } catch (ValidationException e) {
            profileFieldFor(e.field()).setError(e.getMessage());
            router.toasts().error("Please fix the following:", e.getMessage());
        }
    }

    private void savePassword() {
        for (Field field : new Field[] { currentPasswordField, newPasswordField, confirmationField }) {
            field.clearError();
        }

        try {
            profile.changePassword(new ChangePasswordRequest(
                    currentPassword.getText(), newPassword.getText(), confirmation.getText()));

            currentPassword.clear();
            newPassword.clear();
            confirmation.clear();
            router.toasts().success("Password updated", "Use the new password the next time you sign in.");
        } catch (ValidationException e) {
            passwordFieldFor(e.field()).setError(e.getMessage());
            router.toasts().error("Please fix the following:", e.getMessage());
        }
    }

    private Field profileFieldFor(String field) {
        return switch (field) {
            case "name" -> nameField;
            case "businessName" -> organisationField;
            default -> emailField;
        };
    }

    private Field passwordFieldFor(String field) {
        return switch (field) {
            case "password" -> newPasswordField;
            case "passwordConfirmation" -> confirmationField;
            default -> currentPasswordField;
        };
    }
}
