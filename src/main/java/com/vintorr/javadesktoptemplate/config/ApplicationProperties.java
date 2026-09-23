package com.vintorr.javadesktoptemplate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Window and branding settings, bound from the {@code app.*} keys in application.yml.
 * Keeping these out of the code is what makes this project a template: point them at a
 * different product name and the shell follows.
 */
@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(String title, String tagline, Window window) {

    public ApplicationProperties {
        title = title == null ? "Java Desktop Template" : title;
        tagline = tagline == null ? "" : tagline;
        window = window == null ? new Window(null, null, null, null) : window;
    }

    public record Window(Double width, Double height, Double minWidth, Double minHeight) {

        public Window {
            width = width == null ? 1280 : width;
            height = height == null ? 840 : height;
            minWidth = minWidth == null ? 1024 : minWidth;
            minHeight = minHeight == null ? 700 : minHeight;
        }
    }
}
