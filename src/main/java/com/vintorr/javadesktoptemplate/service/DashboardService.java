package com.vintorr.javadesktoptemplate.service;

import com.vintorr.javadesktoptemplate.domain.model.DashboardMetrics;

/** Supplies everything the dashboard screen renders. */
public interface DashboardService {

    DashboardMetrics metrics();
}
