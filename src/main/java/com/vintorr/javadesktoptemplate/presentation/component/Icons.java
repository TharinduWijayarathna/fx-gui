package com.vintorr.javadesktoptemplate.presentation.component;

import java.util.Map;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

/**
 * The same Heroicons outline paths the web app uses
 * (resources/views/components/ui/icon.blade.php + the sidebar nav in layouts/app.blade.php).
 */
public final class Icons {

    /** viewBox of every path below. */
    private static final double VIEW_BOX = 24d;

    private static final Map<String, String> PATHS = Map.ofEntries(
            // ui/icon.blade.php
            Map.entry("plus", "M12 4v16m8-8H4"),
            Map.entry("minus", "M20 12H4"),
            Map.entry("check", "M5 13l4 4L19 7"),
            Map.entry("x", "M6 18L18 6M6 6l12 12"),
            Map.entry("arrow-right", "M14 5l7 7m0 0l-7 7m7-7H3"),
            Map.entry("arrow-left", "M10 19l-7-7m0 0l7-7m-7 7h18"),
            Map.entry("chevron-left", "M15 19l-7-7 7-7"),
            Map.entry("chevron-right", "M9 5l7 7-7 7"),
            Map.entry("search", "M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"),
            Map.entry("eye", "M15 12a3 3 0 11-6 0 3 3 0 016 0z M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 "
                    + "9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"),
            Map.entry("eye-off", "M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 "
                    + "011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 "
                    + "7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 "
                    + "10.025 0 01-4.132 5.411m0 0L21 21"),
            Map.entry("calendar", "M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"),
            Map.entry("check-circle", "M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"),
            Map.entry("sparkles", "M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 "
                    + "12l5.714-2.143L13 3z"),

            // layouts/app.blade.php sidebar
            Map.entry("home", "M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-4 0h4"),
            Map.entry("orders", "M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 "
                    + "2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"),
            Map.entry("clock", "M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"),
            Map.entry("customers", "M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 "
                    + "20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 "
                    + "3 0 11-6 0 3 3 0 016 0z"),
            Map.entry("package", "M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"),
            Map.entry("tag", "M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 "
                    + "1.994 0 013 12V7a4 4 0 014-4z"),
            Map.entry("invoice", "M9 14l6-6m-5.5.5h.01m4.99 5h.01M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16l3.5-2 3.5 2 3.5-2 "
                    + "3.5 2z"),
            Map.entry("cash", "M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 "
                    + "0 00-2 2v6a2 2 0 002 2z"),
            Map.entry("lock", "M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 "
                    + "00-8 0v4h8z"),
            Map.entry("chart", "M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 "
                    + "012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 "
                    + "01-2-2z"),
            Map.entry("users", "M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197"),
            Map.entry("cog", "M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 "
                    + "2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 "
                    + "2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 "
                    + "0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 "
                    + "00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 "
                    + "2.37-2.37.996.608 2.296.07 2.572-1.065z M15 12a3 3 0 11-6 0 3 3 0 016 0z"),
            Map.entry("card", "M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"),
            Map.entry("support", "M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 "
                    + "9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z"),
            Map.entry("menu", "M4 6h16M4 12h16M4 18h16")
    );

    private Icons() {
    }

    /** An icon sized like Tailwind's h-4 w-4 (16px) by default. */
    public static Region of(String name, double size, String... styleClasses) {
        SVGPath path = new SVGPath();
        path.setContent(PATHS.getOrDefault(name, PATHS.get("plus")));
        path.getStyleClass().add("icon");
        path.getStyleClass().addAll(styleClasses);

        double scale = size / VIEW_BOX;
        path.setScaleX(scale);
        path.setScaleY(scale);

        StackPane holder = new StackPane(path);
        holder.setMinSize(size, size);
        holder.setPrefSize(size, size);
        holder.setMaxSize(size, size);
        holder.setMouseTransparent(true);
        return holder;
    }

    public static Region of(String name) {
        return of(name, 16);
    }

    /** Sidebar variant: 16px, follows the nav-link colour states. */
    public static Region nav(String name) {
        SVGPath path = new SVGPath();
        path.setContent(PATHS.getOrDefault(name, PATHS.get("plus")));
        path.getStyleClass().add("nav-icon");
        path.setScaleX(16 / VIEW_BOX);
        path.setScaleY(16 / VIEW_BOX);

        StackPane holder = new StackPane(path);
        holder.setMinSize(16, 16);
        holder.setPrefSize(16, 16);
        holder.setMaxSize(16, 16);
        holder.setMouseTransparent(true);
        return holder;
    }
}
