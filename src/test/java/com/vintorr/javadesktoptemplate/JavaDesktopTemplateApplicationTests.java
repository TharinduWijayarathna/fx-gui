package com.vintorr.javadesktoptemplate;

import com.vintorr.javadesktoptemplate.config.ApplicationProperties;
import com.vintorr.javadesktoptemplate.domain.exception.ValidationException;
import com.vintorr.javadesktoptemplate.domain.model.Person;
import com.vintorr.javadesktoptemplate.service.AuthenticationService;
import com.vintorr.javadesktoptemplate.service.DashboardService;
import com.vintorr.javadesktoptemplate.service.PersonService;
import com.vintorr.javadesktoptemplate.service.ProfileService;
import com.vintorr.javadesktoptemplate.service.SessionService;
import com.vintorr.javadesktoptemplate.service.dto.ChangePasswordRequest;
import com.vintorr.javadesktoptemplate.service.dto.LoginRequest;
import com.vintorr.javadesktoptemplate.service.dto.RegisterRequest;
import com.vintorr.javadesktoptemplate.service.dto.UpdateProfileRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JavaDesktopTemplateApplicationTests {

    @Autowired
    AuthenticationService authentication;

    @Autowired
    ProfileService profile;

    @Autowired
    SessionService session;

    @Autowired
    DashboardService dashboard;

    @Autowired
    PersonService people;

    @Autowired
    ApplicationProperties properties;

    @Test
    void demoAccountCanLogIn() {
        var user = authentication.login(new LoginRequest("demo@example.com", "password", true));

        assertThat(user.businessName()).isEqualTo("Acme Industries");
        assertThat(session.isAuthenticated()).isTrue();
        assertThat(session.isRemembered()).isTrue();
    }

    @Test
    void badCredentialsAreRejected() {
        assertThatThrownBy(() -> authentication.login(new LoginRequest("demo@example.com", "wrong", false)))
                .isInstanceOf(ValidationException.class)
                .hasMessage("These credentials do not match our records.");
    }

    @Test
    void registrationRequiresMatchingPasswords() {
        var request = new RegisterRequest(
                "Jane", "Jane Ltd", "jane@example.com", null, "password1", "password2");

        assertThatThrownBy(() -> authentication.register(request))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertThat(((ValidationException) e).field()).isEqualTo("passwordConfirmation"));
    }

    @Test
    void registrationSignsTheNewUserIn() {
        var user = authentication.register(new RegisterRequest(
                "Nimal", "Nimal Ltd", "nimal@example.com", "REF123", "password", "password"));

        assertThat(session.currentUser()).isEqualTo(user);
    }

    // ------------------------------------------------------------- profile
    //
    // These share one Spring context with the tests above, so each one signs up its own
    // account rather than editing the seeded demo user out from under them.

    @Test
    void profileEditsPersistAndRefreshTheSession() {
        var user = signUp("edits");

        var updated = profile.updateProfile(new UpdateProfileRequest(
                "Edited Name", "edits-new@example.com", "Edited Ltd"));

        assertThat(updated.id()).isEqualTo(user.id());
        assertThat(updated.name()).isEqualTo("Edited Name");
        assertThat(session.currentUser().email()).isEqualTo("edits-new@example.com");
        assertThat(session.currentUser().businessName()).isEqualTo("Edited Ltd");

        // the new address is the one that signs in now, and the old one is gone
        assertThat(authentication.login(new LoginRequest("edits-new@example.com", "password", false)).id())
                .isEqualTo(user.id());
        assertThatThrownBy(() -> authentication.login(new LoginRequest("edits@example.com", "password", false)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void profileEmailCannotCollideWithAnotherAccount() {
        signUp("collision-owner");
        authentication.logout();
        signUp("collision");

        assertThatThrownBy(() -> profile.updateProfile(
                new UpdateProfileRequest("Whoever", "collision-owner@example.com", "Acme")))
                .isInstanceOf(ValidationException.class)
                .hasMessage("The email has already been taken.");
    }

    @Test
    void keepingYourOwnEmailIsNotACollision() {
        var user = signUp("keeps-email");

        var updated = profile.updateProfile(
                new UpdateProfileRequest("Renamed", user.email(), user.businessName()));

        assertThat(updated.email()).isEqualTo(user.email());
        assertThat(updated.name()).isEqualTo("Renamed");
    }

    @Test
    void changingThePasswordRequiresTheCurrentOne() {
        signUp("wrong-current");

        assertThatThrownBy(() -> profile.changePassword(
                new ChangePasswordRequest("nope", "supersecret", "supersecret")))
                .isInstanceOf(ValidationException.class)
                .hasMessage("The password is incorrect.");
    }

    @Test
    void changingThePasswordSwapsTheCredential() {
        signUp("rotates");
        profile.changePassword(new ChangePasswordRequest("password", "supersecret", "supersecret"));
        authentication.logout();

        assertThatThrownBy(() -> authentication.login(new LoginRequest("rotates@example.com", "password", false)))
                .isInstanceOf(ValidationException.class);
        assertThat(authentication.login(new LoginRequest("rotates@example.com", "supersecret", false)).name())
                .isEqualTo("rotates");
    }

    @Test
    void signingOutClearsTheSession() {
        signUp("signs-out");
        authentication.logout();

        assertThat(session.isAuthenticated()).isFalse();
        assertThat(session.currentUser()).isNull();
    }

    /** Registers a throwaway account and leaves it signed in. */
    private com.vintorr.javadesktoptemplate.domain.model.User signUp(String handle) {
        return authentication.register(new RegisterRequest(
                handle, handle + " Ltd", handle + "@example.com", null, "password", "password"));
    }

    // ------------------------------------------------------- demo datasets

    @Test
    void dashboardMetricsAreComplete() {
        var metrics = dashboard.metrics();

        assertThat(metrics.revenueByDay()).isNotEmpty();
        assertThat(metrics.todaysSchedule()).hasSize(6);
        assertThat(metrics.needsAttention()).hasSize(3);
        assertThat(metrics.revenue()).isGreaterThan(0);
    }

    @Test
    void peopleFixturesFillMoreThanOneTablePage() {
        assertThat(people.all()).hasSizeGreaterThan(10);
        assertThat(people.all()).extracting(Person::status).contains(Person.Status.values());
        assertThat(people.teams()).isSorted().doesNotHaveDuplicates();
    }

    @Test
    void brandingComesFromConfiguration() {
        assertThat(properties.title()).isEqualTo("Acme Suite");
        assertThat(properties.window().width()).isEqualTo(1280);
    }
}
