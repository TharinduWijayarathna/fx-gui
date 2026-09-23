package com.vintorr.javadesktoptemplate.presentation.component;

import java.util.List;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Port of x-ui.area-chart: the cumulative revenue sparkline on the dashboard.
 * Same geometry as the Blade version — 4px horizontal padding, 12px vertical,
 * dashed guides at 25/50/75%, brand gradient fill and stroke, dot on the last point.
 */
public class AreaChartView extends Pane {

    private static final double PAD_X = 4;
    private static final double PAD_Y = 12;

    private static final LinearGradient FILL = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#ff3131", 0.28)),
            new Stop(1, Color.web("#ff914d", 0.02)));

    private static final LinearGradient STROKE = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#ed1f1f")),
            new Stop(1, Color.web("#ff914d")));

    private final List<Double> values;
    private final Path area = new Path();
    private final Path line = new Path();
    private final Circle dot = new Circle(4.5);
    private final Line[] guides = { new Line(), new Line(), new Line() };

    public AreaChartView(List<Double> values, double height) {
        this.values = values;
        setMinHeight(height);
        setPrefHeight(height);
        setMaxHeight(Double.MAX_VALUE);

        for (Line guide : guides) {
            guide.setStroke(Color.web("#e7e5e4"));
            guide.setStrokeWidth(1);
            guide.getStrokeDashArray().setAll(4d, 6d);
            getChildren().add(guide);
        }

        area.setFill(FILL);
        area.setStroke(null);

        line.setFill(null);
        line.setStroke(STROKE);
        line.setStrokeWidth(2.5);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeLineJoin(StrokeLineJoin.ROUND);

        dot.setFill(Color.web("#ed1f1f"));
        dot.setStroke(Color.WHITE);
        dot.setStrokeWidth(2);

        getChildren().addAll(area, line, dot);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double chartW = Math.max(1, w - (PAD_X * 2));
        double chartH = Math.max(1, h - (PAD_Y * 2));

        for (int i = 0; i < guides.length; i++) {
            double y = PAD_Y + (chartH * (0.25 * (i + 1)));
            guides[i].setStartX(PAD_X);
            guides[i].setEndX(PAD_X + chartW);
            guides[i].setStartY(y);
            guides[i].setEndY(y);
        }

        area.getElements().clear();
        line.getElements().clear();
        if (values.isEmpty()) {
            dot.setVisible(false);
            return;
        }

        double max = Math.max(1, values.stream().mapToDouble(Double::doubleValue).max().orElse(1));
        int count = values.size();

        double firstX = 0;
        double lastX = 0;
        double lastY = 0;
        for (int i = 0; i < count; i++) {
            double x = count <= 1
                    ? PAD_X + (chartW / 2)
                    : PAD_X + ((i / (double) (count - 1)) * chartW);
            double y = PAD_Y + chartH - ((values.get(i) / max) * chartH);

            if (i == 0) {
                firstX = x;
                line.getElements().add(new MoveTo(x, y));
                area.getElements().add(new MoveTo(x, y));
            } else {
                line.getElements().add(new LineTo(x, y));
                area.getElements().add(new LineTo(x, y));
            }
            lastX = x;
            lastY = y;
        }

        area.getElements().add(new LineTo(lastX, PAD_Y + chartH));
        area.getElements().add(new LineTo(firstX, PAD_Y + chartH));
        area.getElements().add(new ClosePath());

        dot.setVisible(true);
        dot.setCenterX(lastX);
        dot.setCenterY(lastY);
    }
}
