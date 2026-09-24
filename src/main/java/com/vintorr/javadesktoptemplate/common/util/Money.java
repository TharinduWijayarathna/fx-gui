package com.vintorr.javadesktoptemplate.common.util;

import java.text.DecimalFormat;

/** Formats money the one way the whole app does it — "$12,450.00". */
public final class Money {

    private static final String SYMBOL = "$";
    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0.00");

    private Money() {
    }

    public static String format(double amount) {
        return SYMBOL + FORMAT.format(amount);
    }

    /** No decimals — for table cells and other dense places. */
    public static String compact(double amount) {
        return SYMBOL + new DecimalFormat("#,##0").format(amount);
    }
}
