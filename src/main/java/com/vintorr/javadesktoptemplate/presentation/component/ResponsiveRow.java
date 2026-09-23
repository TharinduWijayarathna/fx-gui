package com.vintorr.javadesktoptemplate.presentation.component;

import java.util.List;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * A grid that reflows at a breakpoint, the way the dashboard's Tailwind grids do
 * (`grid sm:grid-cols-2 xl:grid-cols-5`, `grid lg:grid-cols-5`).
 */
public class ResponsiveRow extends GridPane {

    private final List<Region> items;
    private final int[] wideSpans;
    private final int wideColumns;
    private final int narrowColumns;
    private final double wideFrom;

    private Boolean wide;

    public ResponsiveRow(double gap, double wideFrom, int wideColumns, int narrowColumns,
                         List<Region> items, int... wideSpans) {
        this.items = items;
        this.wideSpans = wideSpans;
        this.wideColumns = wideColumns;
        this.narrowColumns = narrowColumns;
        this.wideFrom = wideFrom;

        setHgap(gap);
        setVgap(gap);

        for (Region item : items) {
            item.setMaxWidth(Double.MAX_VALUE);
            item.setMaxHeight(Double.MAX_VALUE);
        }

        widthProperty().addListener((obs, was, is) -> reflow(is.doubleValue()));
        reflow(0);
    }

    private void reflow(double width) {
        boolean nowWide = width >= wideFrom;
        if (wide != null && wide == nowWide) {
            return;
        }
        wide = nowWide;

        int columns = nowWide ? wideColumns : narrowColumns;

        getChildren().clear();
        getColumnConstraints().clear();
        for (int i = 0; i < columns; i++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(100d / columns);
            constraints.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(constraints);
        }

        int column = 0;
        int row = 0;
        for (int i = 0; i < items.size(); i++) {
            int span = nowWide && i < wideSpans.length ? wideSpans[i] : 1;
            if (column + span > columns) {
                column = 0;
                row++;
            }
            Region item = items.get(i);
            GridPane.setConstraints(item, column, row, span, 1);
            GridPane.setHgrow(item, Priority.ALWAYS);
            getChildren().add(item);
            column += span;
        }
    }
}
