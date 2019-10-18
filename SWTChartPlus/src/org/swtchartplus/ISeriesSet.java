/*******************************************************************************
 * Copyright (c) 2008-2014 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchartplus;

import org.swtchartplus.ISeries.SeriesType;

/**
 * A series container.
 */
public interface ISeriesSet {

    /**
     * Creates the series. If series for given id already exists, the existing
     * series will be overwritten.
     * 
     * @param type
     *            the series type
     * @param id
     *            the id for series
     * @return the series
     */
    ISeries createSeries(SeriesType type, String id);

    /**
     * Creates the series. If series for given id already exists, the existing
     * series will be overwritten.
     * 
     * @param type
     *            the series type
     * @param id
     *            the id for series
     * @param updateChart
     * 		  true if you want also to graphically update the chart (default)
     * 		  set this to false if you are adding a lot of series and manually
     * 		  call chart.updateLayout() at the end of the insertion.
     * @return the series
     */
    ISeries createSeries(SeriesType type, String id, boolean updateChart);

    /**
     * Gets the series for given id.
     * 
     * @param id
     *            the id for series
     * @return the series, or null if series doesn't exist for the given id.
     */
    ISeries getSeries(String id);

    /**
     * Gets the array of series
     * 
     * @return the array of series
     */
    ISeries[] getSeries();

    /**
     * Deletes the series for given id.
     * 
     * @param id
     *            the series id
     * @throws IllegalArgumentException
     *             if there is no series for the given id.
     */
    void deleteSeries(String id);

    /**
     * Brings the series for given id forward.
     * 
     * @param id
     *            the series id
     */
    void bringForward(String id);

    /**
     * Brings the series for given id to front.
     * 
     * @param id
     *            the series id
     */
    void bringToFront(String id);

    /**
     * Sends the series for given id backward.
     * 
     * @param id
     *            the series id
     */
    void sendBackward(String id);

    /**
     * Sends the series for given id to back.
     * 
     * @param id
     *            the series id
     */
    void sendToBack(String id);
}