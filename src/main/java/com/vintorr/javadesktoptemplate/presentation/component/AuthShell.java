package com.vintorr.javadesktoptemplate.presentation.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * The auth backdrop: a patterned, radially masked background with the logo lockup and a
 * max-width card in the middle. Ported from the web app's layouts/guest.blade.php.
 */
public final class AuthShell {

    private static final Color BACKGROUND = Color.web("#faf7f4");
    private static final double TILE = 48;

    private AuthShell() {
    }

    public static Region wrap(String title, String tagline, Node cardContent) {
        VBox card = new VBox(cardContent);
        card.getStyleClass().add("auth-card");
        card.setPadding(new Insets(32));
        card.setMaxWidth(448);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        VBox content = new VBox(32, Brand.lockup(title, tagline), card);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40, 16, 40, 16));
        content.setMaxWidth(Region.USE_PREF_SIZE);

        StackPane centered = new StackPane(content);
        centered.setMinHeight(Region.USE_PREF_SIZE);

        var scroller = Components.scroller(centered);
        scroller.setFitToHeight(true);

        StackPane shell = new StackPane(backdrop(), scroller);
        shell.getStyleClass().add("auth-shell");
        return shell;
    }

    /** .auth-pattern (diagonal tiles, radially masked) plus the two blurred brand blobs. */
    private static Pane backdrop() {
        Canvas pattern = new Canvas();
        pattern.setOpacity(0.10);

        Rectangle mask = new Rectangle();

        Circle brandBlob = new Circle();
        brandBlob.setFill(Color.web("#ff3131", 0.10));
        brandBlob.setEffect(new GaussianBlur(64));

        Circle accentBlob = new Circle();
        accentBlob.setFill(Color.web("#ff914d", 0.15));
        accentBlob.setEffect(new GaussianBlur(64));

        Pane backdrop = new Pane(pattern, mask, brandBlob, accentBlob);
        backdrop.setMouseTransparent(true);

        Runnable relayout = () -> {
            double w = backdrop.getWidth();
            double h = backdrop.getHeight();
            if (w <= 0 || h <= 0) {
                return;
            }

            pattern.setWidth(w);
            pattern.setHeight(h);
            paintPattern(pattern.getGraphicsContext2D(), w, h);

            mask.setWidth(w);
            mask.setHeight(h);
            // mask-image: radial-gradient(ellipse at center, black 20%, transparent 75%)
            mask.setFill(new RadialGradient(
                    0, 0, 0.5, 0.5, 0.72, true, CycleMethod.NO_CYCLE,
                    new Stop(0.15, Color.TRANSPARENT),
                    new Stop(1.0, BACKGROUND)));

            // -left-24 top-10 h-64 w-64
            brandBlob.setRadius(128);
            brandBlob.setCenterX(-96 + 128);
            brandBlob.setCenterY(40 + 128);

            // -right-16 bottom-10 h-72 w-72
            accentBlob.setRadius(144);
            accentBlob.setCenterX(w + 64 - 144);
            accentBlob.setCenterY(h - 40 - 144);
        };

        backdrop.widthProperty().addListener((obs, was, is) -> relayout.run());
        backdrop.heightProperty().addListener((obs, was, is) -> relayout.run());
        return backdrop;
    }

    private static void paintPattern(GraphicsContext g, double w, double h) {
        g.clearRect(0, 0, w, h);
        g.setLineWidth(TILE / 4);
        g.setLineCap(javafx.scene.shape.StrokeLineCap.BUTT);

        int index = 0;
        for (double x = -h; x < w + h; x += TILE / 2) {
            g.setStroke(index % 2 == 0 ? Color.web("#ff3131") : Color.web("#ff914d"));
            g.strokeLine(x, 0, x + h, h);
            index++;
        }
    }
}
