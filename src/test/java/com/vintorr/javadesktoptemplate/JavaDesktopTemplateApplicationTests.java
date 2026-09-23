package com.vintorr.javadesktoptemplate;

import com.vintorr.javadesktoptemplate.config.ApplicationProperties;
import com.vintorr.javadesktoptemplate.domain.exception.ValidationException;
import com.vintorr.javadesktoptemplate.service.AuthenticationService;
import com.vintorr.javadesktoptemplate.service.DashboardService;
import com.vintorr.javadesktoptemplate.service.SessionService;
import com.vintorr.javadesktoptemplate.service.dto.LoginRequest;
import com.vintorr.javadesktoptemplate.service.dto.RegisterRequest;

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
    SessionService session;

    @Autowired
    DashboardService dashboard;

    @Autowired
    ApplicationProperties properties;

    @Test
    void demoAccountCanLogIn() {
        var user = authentication.login(new LoginRequest("demo@vintorr.com", "password", true));

        assertThat(user.businessName()).isEqualTo("Colombo Event Rentals");
        assertThat(session.isAuthenticated()).isTrue();
        assertThat(session.isRemembered()).isTrue();
    }

    @Test
    void badCredentialsAreRejected() {
        assertThatThrownBy(() -> authentication.login(new LoginRequest("demo@vintorr.com", "wrong", false)))
                .isInstanceOf(ValidationException.class)
                .hasMessage("These credentials do not match our records.");
    }

    @Test
    void registrationRequiresMatchingPasswords() {
        var request = new RegisterRequest(
                "Jane", "Jane Rentals", "jane@example.com", null, "password1", "password2");

        assertThatThrownBy(() -> authentication.register(request))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertThat(((ValidationException) e).field()).isEqualTo("passwordConfirmation"));
    }

    @Test
    void registrationSignsTheNewUserIn() {
        var user = authentication.register(new RegisterRequest(
                "Nimal", "Nimal Events", "nimal@example.com", "REF123", "password", "password"));

        assertThat(session.currentUser()).isEqualTo(user);
    }

    @Test
    void dashboardMetricsAreComplete() {
        var metrics = dashboard.metrics();

        assertThat(metrics.revenueByDay()).isNotEmpty();
        assertThat(metrics.todaysSchedule()).hasSize(6);
        assertThat(metrics.overdueBookings()).hasSize(3);
        assertThat(metrics.revenue()).isGreaterThan(0);
    }

    @Test
    void brandingComesFromConfiguration() {
        assertThat(properties.title()).isEqualTo("Vintorr Rently");
        assertThat(properties.window().width()).isEqualTo(1280);
    }
}
