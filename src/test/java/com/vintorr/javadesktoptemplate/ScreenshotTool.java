package com.vintorr.javadesktoptemplate;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import com.vintorr.javadesktoptemplate.presentation.view.AppView;
import com.vintorr.javadesktoptemplate.presentation.view.DashboardView;
import com.vintorr.javadesktoptemplate.presentation.view.LoginView;
import com.vintorr.javadesktoptemplate.presentation.view.PeopleView;
import com.vintorr.javadesktoptemplate.presentation.view.ProfileView;
import com.vintorr.javadesktoptemplate.presentation.view.RegisterView;
import com.vintorr.javadesktoptemplate.service.AuthenticationService;
import com.vintorr.javadesktoptemplate.service.SessionService;
import com.vintorr.javadesktoptemplate.service.dto.LoginRequest;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Dev utility: renders each screen off-screen and writes a PNG, so UI changes can be
 * eyeballed without launching the app. Pass the output directory as the only argument;
 * override the canvas height with -Dshot.height.
 */
public final class ScreenshotTool {

    private static final String RESOURCE_ROOT = "/com/vintorr/javadesktoptemplate";

    public static void main(String[] args) throws Exception {
        Path out = Path.of(args.length > 0 ? args[0] : "target/screenshots");
        out.toFile().mkdirs();

        var context = new SpringApplicationBuilder(JavaDesktopTemplateApplication.class)
                .web(WebApplicationType.NONE)
                .run();

        double height = Double.parseDouble(System.getProperty("shot.height", "900"));

        CountDownLatch done = new CountDownLatch(1);
        Platform.startup(() -> {
            try {
                for (String font : new String[] {
                        "DMSans-400.ttf", "DMSans-500.ttf", "DMSans-600.ttf", "DMSans-700.ttf" }) {
                    try (var in = ScreenshotTool.class.getResourceAsStream(RESOURCE_ROOT + "/fonts/" + font)) {
                        if (in != null) {
                            Font.loadFont(in, 14);
                        }
                    }
                }

                // A shown stage makes the snapshot match what the user sees (popups aside).
                Stage stage = new Stage();

                for (Object[] entry : new Object[][] {
                        { "login", LoginView.class },
                        { "register", RegisterView.class },
                        { "dashboard", DashboardView.class },
                        { "people", PeopleView.class },
                        { "profile", ProfileView.class } }) {

                    String name = (String) entry[0];
                    @SuppressWarnings("unchecked")
                    var type = (Class<? extends AppView>) entry[1];

                    if (!context.getBean(SessionService.class).isAuthenticated()) {
                        context.getBean(AuthenticationService.class)
                                .login(new LoginRequest("demo@example.com", "password", false));
                    }

                    Parent root = context.getBean(type).view();
                    Scene scene = new Scene(root, 1280, height);
                    scene.getStylesheets().add(ScreenshotTool.class
                            .getResource(RESOURCE_ROOT + "/css/theme.css").toExternalForm());
                    stage.setScene(scene);
                    stage.show();

                    root.applyCss();
                    root.layout();

                    WritableImage image = scene.snapshot(null);
                    javax.imageio.ImageIO.write(
                            SwingFXUtils.fromFXImage(image, null), "png",
                            new File(out.toFile(), name + ".png"));
                    System.out.println("wrote " + name + ".png");
                }
                stage.hide();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                done.countDown();
            }
        });

        done.await();
        Platform.exit();
        context.close();
        System.exit(0);
    }
}
