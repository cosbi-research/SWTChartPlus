/*******************************************************************************
 * Copyright (c) 2008-2014 SWTChart project. All rights reserved.
 *
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchartplus.internal;

import org.swtchartplus.Chart;

/**
 * A chart title.
 */
public class ChartTitle extends Title {

    /** the default text */
    public static String DEFAULT_TEXT = "Timeseries from the simulation";

    /**
     * Constructor.
     *
     * @param chart
     *            the plot chart
     */
    public ChartTitle(Chart chart) {
        super(chart);
        setText(getDefaultText());
    }

    /*
     * @see Title#getDefaultText()
     */
    @Override
    protected String getDefaultText() {
        return DEFAULT_TEXT;
    }
}
