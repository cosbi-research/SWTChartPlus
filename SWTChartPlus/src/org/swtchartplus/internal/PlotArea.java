/*******************************************************************************
 * Copyright (c) 2008-2014 SWTChart project. All rights reserved.
 *
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchartplus.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.swtchartplus.Chart;
import org.swtchartplus.IAxis;
import org.swtchartplus.IBarSeries;
import org.swtchartplus.ICustomPaintListener;
import org.swtchartplus.ILineSeries;
import org.swtchartplus.ILineSeries.PlotSymbolType;
import org.swtchartplus.IPlotArea;
import org.swtchartplus.ISeries;
import org.swtchartplus.ISeriesSet;
import org.swtchartplus.ext.InteractiveChart;
import org.swtchartplus.internal.series.Series;
import org.swtchartplus.internal.series.SeriesSet;

/**
 * Plot area to draw series and grids.
 */
public class PlotArea extends Composite implements PaintListener, IPlotArea {

    /** the chart */
    protected Chart chart;

    /** the set of plots */
    protected SeriesSet seriesSet;

    /** the custom paint listeners */
    List<ICustomPaintListener> paintListeners;

    /** the default background color */
    private static final int DEFAULT_BACKGROUND = SWT.COLOR_WHITE;

    public static boolean highlight = false;

    /**
     * Constructor.
     *
     * @param chart
     *            the chart
     * @param style
     *            the style
     */
    public PlotArea(final Chart chart, int style) {
	super(chart, style | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);

	this.chart = chart;

	seriesSet = new SeriesSet(chart);
	paintListeners = new ArrayList<ICustomPaintListener>();

	setBackground(Display.getDefault().getSystemColor(DEFAULT_BACKGROUND));
	addPaintListener(this);

	addListener(SWT.MouseEnter, new Listener() {
	    @Override
	    public void handleEvent(Event arg0) {
		//System.err.println("MouseEnter - start");
		highlight = true;
		setFocus();
		chart.updateLayout();
		chart.redraw();
		//System.err.println("MouseEnter - end");
	    }
	});

	addListener(SWT.MouseExit, new Listener() {
	    @Override
	    public void handleEvent(Event arg0) {
		//System.err.println("MouseExit - start");
		if (InteractiveChart.lblMousePosition != null)
		    InteractiveChart.lblMousePosition.setText("");
		highlight = false;
		chart.redraw();
		if (!isUnix())
		    // this causes a enter/exit loop in linux
		    chart.updateLayout();
		//System.err.println("MouseExit - end");
	    }
	});

    }

    public static boolean isUnix() {
	String OS = System.getProperty("os.name").toLowerCase();
	return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

    }

    /**
     * Gets the set of series.
     *
     * @return the set of series
     */
    public ISeriesSet getSeriesSet() {
	return seriesSet;
    }

    /*
     * @see Control#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	((SeriesSet) getSeriesSet()).compressAllSeries();
    }

    /*
     * @see Control#setBackground(Color)
     */
    @Override
    public void setBackground(Color color) {
	if (color == null) {
	    super.setBackground(Display.getDefault().getSystemColor(DEFAULT_BACKGROUND));
	} else {
	    super.setBackground(color);
	}
    }

    /*
     * @see IPlotArea#addCustomPaintListener(ICustomPaintListener)
     */
    public void addCustomPaintListener(ICustomPaintListener listener) {
	paintListeners.add(listener);
    }

    /*
     * @see IPlotArea#removeCustomPaintListener(ICustomPaintListener)
     */
    public void removeCustomPaintListener(ICustomPaintListener listener) {
	paintListeners.remove(listener);
    }

    /*
     * @see PaintListener#paintControl(PaintEvent)
     */
    public void paintControl(PaintEvent e) {
	//System.err.println("PlotArea.paintControl()");
	Point p = getSize();
	GC gc = e.gc;
	// draw the plot area background
	Color oldBackground = gc.getBackground();
	gc.setBackground(getBackground());
	gc.fillRectangle(0, 0, p.x, p.y);
	// draw grid
	//System.err.println("PlotArea.paintControl() - draw grid");
	for (IAxis axis : chart.getAxisSet().getAxes()) {
	    ((Grid) axis.getGrid()).draw(gc, p.x, p.y);
	}
	// draw behind series
	//System.err.println("PlotArea.paintControl() - draw behind series");
	for (ICustomPaintListener listener : paintListeners) {
	    if (listener.drawBehindSeries()) {
		listener.paintControl(e);
	    }
	}
	// draw series. The line series should be drawn on bar series.
	//System.err.println("PlotArea.paintControl() - draw bar series");
	for (ISeries series : chart.getSeriesSet().getSeries()) {
	    if (series instanceof IBarSeries) {
		((Series) series).draw(gc, p.x, p.y);
	    }
	}
	//System.err.println("PlotArea.paintControl() - draw line series");
	for (ISeries series : chart.getSeriesSet().getSeries()) {
	    if (series instanceof ILineSeries) {
		((Series) series).draw(gc, p.x, p.y);
	    }
	}
	// draw over series
	//System.err.println("PlotArea.paintControl() - draw over series");
	for (ICustomPaintListener listener : paintListeners) {
	    if (!listener.drawBehindSeries()) {
		listener.paintControl(e);
	    }
	}
	e.gc.setBackground(oldBackground);

	// if nearest mouse is out of bounds skip draw
	if (Legend.xNearestMouse < 0) {
	    System.err.println(
		    "Couldn't display series position for an out-of-range series element: " + Legend.xNearestMouse);
	    return;
	}

	if (highlight && InteractiveChart.propertiesDialogOpen == false && isFocusControl()) {
	    gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
	    gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
	    gc.setAlpha(255);
	    for (ISeries serie : chart.getSeriesSet().getSeries()) {
		int xCorner;
		if (serie.getXSeries().length > Legend.xNearestMouse)
		    xCorner = chart.getAxisSet().getXAxis(0)
			    .getPixelCoordinate(serie.getXSeries()[Legend.xNearestMouse]);
		else
		    xCorner = chart.getAxisSet().getXAxis(0)
			    .getPixelCoordinate(serie.getXSeries()[serie.getXSeries().length - 1]);

		int yCorner;

		if (serie.getYSeries().length > Legend.xNearestMouse)
		    yCorner = chart.getAxisSet().getYAxis(0)
			    .getPixelCoordinate(serie.getYSeries()[Legend.xNearestMouse]);
		else
		    yCorner = chart.getAxisSet().getYAxis(0)
			    .getPixelCoordinate(serie.getYSeries()[serie.getYSeries().length - 1]);
		PlotSymbolType typeSymbolHighlighted = ((ILineSeries) serie).getSymbolType();
		int sizeSymbolHighlighted = ((ILineSeries) serie).getSymbolSize() + 2;
		if (typeSymbolHighlighted == PlotSymbolType.NONE) {
		    typeSymbolHighlighted = PlotSymbolType.CIRCLE;
		    sizeSymbolHighlighted = 4;
		}
		switch (typeSymbolHighlighted) {
		case CIRCLE:
		    gc.fillOval(xCorner - sizeSymbolHighlighted, yCorner - sizeSymbolHighlighted,
			    sizeSymbolHighlighted * 2, sizeSymbolHighlighted * 2);
		    break;
		case SQUARE:
		    gc.fillRectangle(xCorner - sizeSymbolHighlighted, yCorner - sizeSymbolHighlighted,
			    sizeSymbolHighlighted * 2, sizeSymbolHighlighted * 2);
		    break;
		case DIAMOND:
		    int[] diamondArray = { xCorner, yCorner - sizeSymbolHighlighted, xCorner + sizeSymbolHighlighted,
			    yCorner, xCorner, yCorner + sizeSymbolHighlighted, xCorner - sizeSymbolHighlighted,
			    yCorner };
		    gc.fillPolygon(diamondArray);
		    break;
		case TRIANGLE:
		    int[] triangleArray = { xCorner, yCorner - sizeSymbolHighlighted, xCorner + sizeSymbolHighlighted,
			    yCorner + sizeSymbolHighlighted, xCorner - sizeSymbolHighlighted,
			    yCorner + sizeSymbolHighlighted };
		    gc.fillPolygon(triangleArray);
		    break;
		case INVERTED_TRIANGLE:
		    int[] invertedTriangleArray = { xCorner, yCorner + sizeSymbolHighlighted,
			    xCorner + sizeSymbolHighlighted, yCorner - sizeSymbolHighlighted,
			    xCorner - sizeSymbolHighlighted, yCorner - sizeSymbolHighlighted };
		    gc.fillPolygon(invertedTriangleArray);
		    break;
		case CROSS:
		    gc.setLineStyle(SWT.LINE_SOLID);
		    gc.drawLine(xCorner - sizeSymbolHighlighted, yCorner - sizeSymbolHighlighted,
			    xCorner + sizeSymbolHighlighted, yCorner + sizeSymbolHighlighted);
		    gc.drawLine(xCorner - sizeSymbolHighlighted, yCorner + sizeSymbolHighlighted,
			    xCorner + sizeSymbolHighlighted, yCorner - sizeSymbolHighlighted);
		    break;
		case PLUS:
		    gc.setLineStyle(SWT.LINE_SOLID);
		    gc.drawLine(xCorner, yCorner - sizeSymbolHighlighted, xCorner, yCorner + sizeSymbolHighlighted);
		    gc.drawLine(xCorner - sizeSymbolHighlighted, yCorner, xCorner + sizeSymbolHighlighted, yCorner);
		    break;
		case NONE:
		default:
		    break;
		}
	    }
	}
	//        gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	//System.err.println("PlotArea.paintControl() - done.");
    }

    /*
     * @see Widget#dispose()
     */
    @Override
    public void dispose() {
	super.dispose();
	seriesSet.dispose();
    }
}
