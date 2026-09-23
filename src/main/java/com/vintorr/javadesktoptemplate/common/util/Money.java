package com.vintorr.javadesktoptemplate.common.util;

import java.text.DecimalFormat;

/** Port of App\Support\Money::format — "Rs. 12,450.00". */
public final class Money {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0.00");

    private Money() {
    }

    public static String format(double amount) {
        return "Rs. " + FORMAT.format(amount);
    }
}
