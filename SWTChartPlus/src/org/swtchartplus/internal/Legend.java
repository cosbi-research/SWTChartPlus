/*******************************************************************************
 * Copyright (c) 2008-2014 SWTChart project. All rights reserved.
 *
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchartplus.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchartplus.Chart;
import org.swtchartplus.Constants;
import org.swtchartplus.IBarSeries;
import org.swtchartplus.ILegend;
import org.swtchartplus.ILineSeries;
import org.swtchartplus.ISeries;
import org.swtchartplus.internal.series.LineSeries;
import org.swtchartplus.internal.series.Series;

/**
 * A legend for chart.
 */
public class Legend extends Composite implements ILegend, PaintListener {

    /** the plot chart */
    private Chart chart;

    /** the state indicating the legend visibility */
    private boolean visible;

    /** the position of legend */
    private int position;

    /** the margin */
    private static final int MARGIN = 5;

    /** the width of area to draw symbol */
    private static final int SYMBOL_WIDTH = 20;

    /** the line width */
    private static final int LINE_WIDTH = 2;

    /** the default foreground */
    private static final Color DEFAULT_FOREGROUND = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

    /** the default background */
    private static final Color DEFAULT_BACKGROUND = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

    /** the default font */
    private Font defaultFont;

    /** the default font size */
    private static final int DEFAULT_FONT_SIZE = Constants.SMALL_FONT_SIZE;

    /** the default position */
    private static final int DEFAULT_POSITION = SWT.RIGHT;

    private static final int MAX_HEIGHT = 200;

    /** the map between series id and cell bounds */
    private Map<String, Rectangle> cellBounds;

    public static int xNearestMouse = 0;

    public static int numDecimals = 0;

    int inc = 0;

    private int stopDrawingAtSeries;

    /**
     * Constructor.
     *
     * @param chart
     *            the chart
     * @param style
     *            the style
     */
    public Legend(Chart chart, int style) {
	super(chart, style | SWT.DOUBLE_BUFFERED);
	this.chart = chart;

	stopDrawingAtSeries = Integer.MAX_VALUE;
	visible = true;
	position = DEFAULT_POSITION;
	cellBounds = new HashMap<String, Rectangle>();
	defaultFont = new Font(Display.getDefault(), "Tahoma", DEFAULT_FONT_SIZE, SWT.NORMAL);
	setFont(defaultFont);
	setForeground(DEFAULT_FOREGROUND);
	setBackground(DEFAULT_BACKGROUND);
	addPaintListener(this);

    }

    /*
     * @see Control#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
	if (this.visible == visible) {
	    return;
	}

	this.visible = visible;
	chart.updateLayout();
    }

    /*
     * @see Control#isVisible()
     */
    @Override
    public boolean isVisible() {
	return visible;
    }

    /*
     * @see Canvas#setFont(Font)
     */
    @Override
    public void setFont(Font font) {
	if (font == null) {
	    super.setFont(defaultFont);
	} else {
	    super.setFont(font);
	}
	chart.updateLayout();
    }

    /*
     * @see Control#setForeground(Color)
     */
    @Override
    public void setForeground(Color color) {
	if (color == null) {
	    super.setForeground(DEFAULT_FOREGROUND);
	} else {
	    super.setForeground(color);
	}
    }

    /*
     * @see Control#setBackground(Color)
     */
    @Override
    public void setBackground(Color color) {
	if (color == null) {
	    super.setBackground(DEFAULT_BACKGROUND);
	} else {
	    super.setBackground(color);
	}
    }

    /*
     * @see ILegend#getPosition()
     */
    public int getPosition() {
	return position;
    }

    /*
     * @see ILegend#setPosition(int)
     */
    public void setPosition(int value) {
	if (value == SWT.LEFT || value == SWT.RIGHT || value == SWT.TOP || value == SWT.BOTTOM) {
	    position = value;
	} else {
	    position = DEFAULT_POSITION;
	}
	chart.updateLayout();
    }

    /*
     * @see ILegend#getBounds(String)
     */
    public Rectangle getBounds(String seriesId) {
	return cellBounds.get(seriesId);
    }

    /*
     * @see Widget#dispose()
     */
    @Override
    public void dispose() {
	super.dispose();
	if (!defaultFont.isDisposed()) {
	    defaultFont.dispose();
	}
    }

    /**
     * Sorts the given series array. For instance, if there are two stack series
     * in horizontal orientation, the top of stack series should appear at top
     * of legend.
     * <p>
     * If there are multiple x axes, the given series array will be sorted with
     * x axis first. And then, the series in each x axis will be sorted with
     * {@link Legend#sort(List, boolean, boolean)}.
     *
     * @param seriesArray
     *            the series array
     * @return the sorted series array
     */
    private ISeries[] sort(ISeries[] seriesArray) {

	// create a map between axis id and series list
	Map<Integer, List<ISeries>> map = new HashMap<Integer, List<ISeries>>();
	for (ISeries series : seriesArray) {
	    int axisId = series.getXAxisId();
	    List<ISeries> list = map.get(axisId);
	    if (list == null) {
		list = new ArrayList<ISeries>();
	    }
	    list.add(series);
	    map.put(axisId, list);
	}

	// sort an each series list
	List<ISeries> sortedArray = new ArrayList<ISeries>();
	boolean isVertical = chart.getOrientation() == SWT.VERTICAL;
	for (Entry<Integer, List<ISeries>> entry : map.entrySet()) {
	    boolean isCategoryEnabled = chart.getAxisSet().getXAxis(entry.getKey()).isCategoryEnabled();
	    sortedArray.addAll(sort(entry.getValue(), isCategoryEnabled, isVertical));
	}

	return sortedArray.toArray(new ISeries[sortedArray.size()]);
    }

    /**
     * Sorts the given series list which belongs to a certain x axis.
     * <ul>
     * <li>The stacked series will be gathered, and the order of stack series
     * will be reversed.</li>
     * <li>In the case of vertical orientation, the order of whole series will
     * be reversed.</li>
     * </ul>
     *
     * @param seriesList
     *            the series list which belongs to a certain x axis
     * @param isCategoryEnabled
     *            true if category is enabled
     * @param isVertical
     *            true in the case of vertical orientation
     * @return the sorted series array
     */
    private static List<ISeries> sort(List<ISeries> seriesList, boolean isCategoryEnabled, boolean isVertical) {
	List<ISeries> sortedArray = new ArrayList<ISeries>();

	// gather the stacked series reversing the order of stack series
	int insertIndex = -1;
	for (int i = 0; i < seriesList.size(); i++) {
	    if (isCategoryEnabled && ((Series) seriesList.get(i)).isValidStackSeries()) {
		if (insertIndex == -1) {
		    insertIndex = i;
		} else {
		    sortedArray.add(insertIndex, seriesList.get(i));
		    continue;
		}
	    }
	    sortedArray.add(seriesList.get(i));
	}

	// reverse the order of whole series in the case of vertical orientation
	if (isVertical) {
	    Collections.reverse(sortedArray);
	}

	return sortedArray;
    }

    /**
     * Update the layout data.
     */
    public void updateLayoutData() {
	if (!visible || chart.getSeriesSet().getSeries().length == 0) {
	    setLayoutData(new ChartLayoutData(0, 0));
	    return;
	}
	int width = 0;
	int height = 0;
	ISeries[] seriesArray = sort(chart.getSeriesSet().getSeries());
	Rectangle r = chart.getClientArea();
	Rectangle titleBounds = ((Title) chart.getTitle()).getBounds();
	int titleHeight = titleBounds.y + titleBounds.height;
	int cellHeight = Util.getExtentInGC(getFont(), "dummy").y;
	if (PlotArea.highlight && seriesArray[0].getXSeries().length > xNearestMouse) {
	    String sXnearestMouse = String.format("%." + numDecimals + "f", seriesArray[0].getXSeries()[xNearestMouse]);
	    sXnearestMouse = sXnearestMouse.replace(',', '.');
	    String xAxisTitle = chart.getAxisSet().getXAxis(0).getTitle().getText();
	    inc = Util.getExtentInGC(getFont(), "  " + xAxisTitle + ": " + sXnearestMouse + "  ").x;
	}
	if (position == SWT.RIGHT || position == SWT.LEFT) {
	    int columns = 1;
	    int yPosition = MARGIN;
	    int maxCellWidth = 0;
	    for (ISeries series : seriesArray) {
		if (!series.isVisibleInLegend()) {
		    continue;
		}
		String label = getLegendLabel(series);
		int textWidth = Util.getExtentInGC(getFont(), label).x;
		int cellWidth = textWidth + SYMBOL_WIDTH + MARGIN * 3;
		maxCellWidth = Math.max(maxCellWidth, cellWidth);
		if (yPosition + cellHeight < r.height - titleHeight - MARGIN || yPosition == MARGIN) {
		    yPosition += cellHeight + MARGIN;
		} else {
		    columns++;
		    yPosition = cellHeight + MARGIN * 2;
		}
		cellBounds.put(series.getId(), new Rectangle(maxCellWidth * (columns - 1),
			yPosition - cellHeight - MARGIN, cellWidth, cellHeight));
		height = Math.max(yPosition, height);
	    }
	    width = maxCellWidth * columns;
	} else if (position == SWT.TOP || position == SWT.BOTTOM) {
	    int rows = 1;
	    int xPosition = 0;
	    /* if they don't fit the panel don't draw */
	    stopDrawingAtSeries = Integer.MAX_VALUE;
	    int seriesId = 0;
	    for (ISeries series : seriesArray) {
		if (!series.isVisibleInLegend()) {
		    continue;
		}
		String label = getLegendLabel(series);
		int textWidth = Util.getExtentInGC(getFont(), label).x;
		int cellWidth = textWidth + SYMBOL_WIDTH + MARGIN * 3 + 20;
		if (PlotArea.highlight && series.getYSeries().length > xNearestMouse) {
		    double d = series.getYSeries()[xNearestMouse];
		    String sYvalue = d == (int) d ? String.valueOf((int) d) : String.valueOf(d);
		    cellWidth += Util.getExtentInGC(getFont(), sYvalue + "  ").x;
		}
		if (PlotArea.highlight) {
		    if (inc + xPosition + cellWidth < r.width || xPosition == 0)
			xPosition += cellWidth;
		    else {
			rows++;
			xPosition = cellWidth;
		    }
		} else {
		    if (xPosition + cellWidth < r.width || xPosition == 0)
			xPosition += cellWidth;
		    else {
			rows++;
			xPosition = cellWidth;
		    }
		}
		cellBounds.put(series.getId(), new Rectangle(xPosition - cellWidth,
			(cellHeight + MARGIN) * (rows - 1) + MARGIN, cellWidth, cellHeight));
		if (PlotArea.highlight && rows == 1)
		    width = Math.max(xPosition + inc, width);
		else
		    width = Math.max(xPosition, width);

		/* don't grow to infinity.. */
		if (((cellHeight + MARGIN) * rows + MARGIN) > MAX_HEIGHT) {
		    stopDrawingAtSeries = seriesId;
		    break;
		}
		seriesId++;
	    }
	    height = (cellHeight + MARGIN) * rows + MARGIN;
	}

	if (height > MAX_HEIGHT)
	    height = MAX_HEIGHT;

	setLayoutData(new ChartLayoutData(width, height));
	//        System.out.println("width: "+width+", height: "+height);
	//        System.out.println("r.width: "+r.width);
	//        System.out.println("inc: "+inc);

    }

    /**
     * Gets the legend label.
     * 
     * @param series
     *            the series
     * @return the legend label
     */
    private static String getLegendLabel(ISeries series) {
	String description = series.getDescription();

	if (description == null) {
	    return series.getId();
	}
	return description;
    }

    /**
     * Draws the symbol of series.
     *
     * @param gc
     *            the graphics context
     * @param series
     *            the series
     * @param r
     *            the rectangle to draw the symbol of series
     */
    protected void drawSymbol(GC gc, Series series, Rectangle r) {

	if (!visible) {
	    return;
	}

	if (series instanceof ILineSeries) {
	    // draw plot line
	    gc.setForeground(((ILineSeries) series).getLineColor());
	    gc.setLineWidth(LINE_WIDTH);
	    int lineStyle = Util.getIndexDefinedInSWT(((ILineSeries) series).getLineStyle());
	    int x = r.x;
	    int y = r.y + r.height / 2;
	    if (lineStyle != SWT.NONE) {
		gc.setLineStyle(lineStyle);
		gc.drawLine(x, y, x + SYMBOL_WIDTH, y);
	    }

	    // draw series symbol
	    Color color = ((ILineSeries) series).getSymbolColor();
	    Color[] colors = ((ILineSeries) series).getSymbolColors();
	    if (colors != null && colors.length > 0) {
		color = colors[0];
	    }
	    ((LineSeries) series).drawSeriesSymbol(gc, x + SYMBOL_WIDTH / 2, y, color);
	} else if (series instanceof IBarSeries) {
	    // draw riser
	    gc.setBackground(((IBarSeries) series).getBarColor());
	    int size = SYMBOL_WIDTH / 2;
	    int x = r.x + size / 2;
	    int y = (int) (r.y - size / 2d + r.height / 2d);
	    gc.fillRectangle(x, y, size, size);
	}
    }

    /*
     * @see PaintListener#paintControl(PaintEvent)
     */
    public void paintControl(PaintEvent e) {
	if (!visible) {
	    return;
	}
	//        updateLayoutData();
	GC gc = e.gc;
	gc.setFont(getFont());
	ISeries[] seriesArray = chart.getSeriesSet().getSeries();
	if (seriesArray.length == 0) {
	    return;
	}
	// draw frame
	gc.fillRectangle(0, 0, getSize().x - 1, getSize().y - 1);
	gc.setLineStyle(SWT.LINE_SOLID);
	gc.setLineWidth(1);
	gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
	gc.drawRectangle(0, 0, getSize().x - 1, getSize().y - 1);
	gc.setBackground(getBackground());
	gc.setForeground(getForeground());
	if (PlotArea.highlight) {
	    String sXnearestMouse = String.format("%." + numDecimals + "f", seriesArray[0].getXSeries()[xNearestMouse]);
	    sXnearestMouse = sXnearestMouse.replace(',', '.');
	    String xAxisTitle = chart.getAxisSet().getXAxis(0).getTitle().getText();
	    gc.drawText(" " + xAxisTitle + ": " + sXnearestMouse, 2, 5, true);
	    inc = Util.getExtentInGC(getFont(), "  " + xAxisTitle + ": " + sXnearestMouse + "  ").x;
	} else
	    inc = 0;
	int incTmp = inc;
	// draw content
	for (int i = 0; i < seriesArray.length && i < stopDrawingAtSeries; i++) {
	    if (!seriesArray[i].isVisibleInLegend()) {
		continue;
	    }
	    // draw plot line, symbol etc
	    String id = seriesArray[i].getId();
	    Rectangle r = cellBounds.get(id);
	    //            System.out.println(r.width);
	    if (r.y > MARGIN)
		inc = 0;
	    drawSymbol(gc, (Series) seriesArray[i],
		    new Rectangle(inc + r.x + MARGIN, r.y + MARGIN, SYMBOL_WIDTH, r.height - MARGIN * 2));
	    // draw label
	    String label = seriesArray[i].getDescription();
	    gc.setBackground(getBackground());
	    gc.setForeground(getForeground());
	    //gc.drawText(label, r.x + SYMBOL_WIDTH + MARGIN * 2, r.y, true);
	    if (seriesArray[i].getIcon() != null)
		gc.drawImage((Image) seriesArray[i].getIcon(), inc + r.x + SYMBOL_WIDTH + MARGIN * 2, r.y);
	    if (PlotArea.highlight) {
		double yValue = seriesArray[i].getYSeries()[xNearestMouse];
		String sYvalue = yValue == (int) yValue ? String.valueOf((int) yValue) : String.valueOf(yValue);
		gc.drawText("     " + label + ": " + sYvalue, inc + r.x + SYMBOL_WIDTH + MARGIN * 2, r.y, true);
	    } else
		gc.drawText("     " + label, inc + r.x + SYMBOL_WIDTH + MARGIN * 2, r.y, true);
	}
	inc = incTmp;
	//        updateLayoutData();
    }

}
