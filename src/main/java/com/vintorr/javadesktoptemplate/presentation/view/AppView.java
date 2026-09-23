package com.vintorr.javadesktoptemplate.presentation.view;

import javafx.scene.Parent;

/** A full-window screen. Each implementation is a prototype-scoped Spring bean. */
public interface AppView {

    Parent view();

    /** Window title for this screen, given the configured application title. */
    default String title(String applicationTitle) {
        return applicationTitle;
    }
}
