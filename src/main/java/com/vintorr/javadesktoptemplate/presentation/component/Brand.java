package com.vintorr.javadesktoptemplate.presentation.component;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * The application logo, drawn rather than shipped as a bitmap: a gradient tile carrying an
 * open-source Heroicons glyph (MIT, Tailwind Labs), next to the product name from
 * {@code app.title}. Swapping the glyph or the gradient re-brands the whole shell.
 */
public final class Brand {

    /** Heroicons "view-grid", the same 24×24 viewBox as the rest of the icon set. */
    private static final String GLYPH = "M4 5a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 15a1 1 0 "
            + "011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H5a1 1 0 01-1-1v-4zM14 5a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 "
            + "1h-4a1 1 0 01-1-1V5zM14 15a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1v-4z";

    private static final String GRADIENT = "linear-gradient(to bottom right, #ff3131 0%, #ff914d 100%)";

    private Brand() {
    }

    /** The logo tile on its own — inline-styled, so it renders with or without the stylesheet. */
    public static Region mark(double size) {
        SVGPath glyph = new SVGPath();
        glyph.setContent(GLYPH);
        glyph.setFill(null);
        glyph.setStroke(Color.WHITE);
        glyph.setStrokeWidth(1.8);
        glyph.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        glyph.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

        double scale = size * 0.6 / 24d;
        glyph.setScaleX(scale);
        glyph.setScaleY(scale);

        StackPane tile = new StackPane(glyph);
        tile.setMinSize(size, size);
        tile.setPrefSize(size, size);
        tile.setMaxSize(size, size);
        tile.setStyle("-fx-background-color: " + GRADIENT + "; -fx-background-radius: " + (size * 0.28) + ";");
        return tile;
    }

    /** Mark + product name, for the sidebar header and the auth screens. */
    public static Region wordmark(String title, double markSize, String... titleStyleClasses) {
        HBox row = new HBox(12, mark(markSize), Components.label(title, titleStyleClasses));
        row.setAlignment(Pos.CENTER_LEFT);
        // Keep the row at its own width, or a centring parent stretches it and the lockup
        // ends up left-aligned inside the full-width row.
        row.setMaxWidth(Region.USE_PREF_SIZE);
        return row;
    }

    /** The stacked, centred lockup the login and register screens sit under. */
    public static Region lockup(String title, String tagline) {
        VBox stack = new VBox(10,
                wordmark(title, 44, "brand-wordmark-lg"),
                Components.label(tagline, "muted-sm"));
        stack.setAlignment(Pos.CENTER);
        return stack;
    }

    /** The same mark rasterised for the window/taskbar icon. */
    public static Image icon(double size) {
        Region mark = mark(size);
        Group root = new Group(mark);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        root.applyCss();
        root.layout();

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        return mark.snapshot(parameters, null);
    }
}
