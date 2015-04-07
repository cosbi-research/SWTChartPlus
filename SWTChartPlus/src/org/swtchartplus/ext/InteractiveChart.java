package org.swtchartplus.ext;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolTip;
import org.swtchartplus.Chart;
import org.swtchartplus.IAxis;
import org.swtchartplus.IAxis.Direction;
import org.swtchartplus.ILineSeries;
import org.swtchartplus.ISeries;
import org.swtchartplus.Range;
import org.swtchartplus.ext.internal.SelectionRectangle;
import org.swtchartplus.ext.internal.properties.AxisPage;
import org.swtchartplus.ext.internal.properties.ChartPage;
import org.swtchartplus.ext.internal.properties.PropertiesResources;
import org.swtchartplus.internal.Legend;

/**
 * An interactive chart which provides the following abilities:
 * <ul>
 * <li>scroll with mouse drag and arrow keys</li>
 * <li>zoom with mouse wheel, zoom in with Ctrl+Plus, zoom out with Ctrl+Minus</li>
 * <li>toggle log/linear scale with Ctrl+L</li>
 * <li>context menus for adjusting axis range and zooming in/out</li>
 * <li>file selector dialog to save chart to image file</li>
 * <li>properties dialog to configure the chart settings</li>
 * </ul>
 */
public class InteractiveChart extends Chart implements PaintListener {

    /** the filter extensions */
    private static final String[] EXTENSIONS = new String[] { "*.png" };
    /** the filter names */
    private static final String [] FILTERNAMES = new String [] {"PNG File (*.png)"};
    
    /** the selection rectangle for zoom in/out */
    protected SelectionRectangle selection;

    /** the clicked time in milliseconds */
    private long clickedTime;

    /** the resources created with properties dialog */
    private PropertiesResources resources;
    
    /** true if the mouse left button is pressed, otherwise false */
    public boolean mousePressed = false;
    /** coordinate x of the mouse on the screen */
    private int pos_x_mouse = 0; 
    /** coordinate y of the mouse on the screen */
    private int pos_y_mouse = 0; 
    /** true if the CTRL on Windows or MELA on Mac is pressed, otherwise false */
    public boolean ctrlPressed = false;
    
    public static boolean propertiesDialogOpen = false;

	private Cursor mouseDownEventCursor;

	private Cursor mouseUpEventCursor;

	private Cursor cursorDefault;
	/** shows the position of the mouse in the graph */
	public static Label lblMousePosition;
	
	public static ToolTip toolTip;
	
	public ILineSeries serieSelected = null;
	
	public Color colorSerieSelected;
	
	/** true if the platform is Mac, otherwise false */
	public static boolean isMac = System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;	//PApplet.platform == PConstants.MACOSX;
	
    /**
     * Constructor.
     * 
     * @param parent
     *            the parent composite
     * @param style
     *            the style
     */
    public InteractiveChart(Composite parent, int style) {
    	super(parent, style);
        init();
    }

    /**
     * Initializes.
     */
    private void init(Cursor curs) {
    	cursorDefault = curs;
        selection = new SelectionRectangle();
        resources = new PropertiesResources();

        Composite plot = getPlotArea();
        plot.addListener(SWT.Resize, this);
        plot.addListener(SWT.MouseMove, this);
        plot.addListener(SWT.MouseDown, this);
        plot.addListener(SWT.MouseUp, this);
        plot.addListener(SWT.MouseWheel, this);
        plot.addListener(SWT.KeyDown, this);
        plot.addListener(SWT.KeyUp, this);

        plot.addPaintListener(this);

        createMenuItems();
        getPlotArea().setCursor(cursorDefault);
    }

    private void init() {
    	init(null);
    }
    /**
     * Creates menu items.
     */
    private void createMenuItems() {
        Menu menu = new Menu(getPlotArea());
        getPlotArea().setMenu(menu);

        // adjust axis range menu group
        MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
        menuItem.setText(Messages.ADJUST_AXIS_RANGE_GROUP);
        Menu adjustAxisRangeMenu = new Menu(menuItem);
        menuItem.setMenu(adjustAxisRangeMenu);

        // adjust axis range
        menuItem = new MenuItem(adjustAxisRangeMenu, SWT.PUSH);
        menuItem.setText(Messages.ADJUST_AXIS_RANGE);
        menuItem.addListener(SWT.Selection, this);

        // adjust X axis range
        menuItem = new MenuItem(adjustAxisRangeMenu, SWT.PUSH);
        menuItem.setText(Messages.ADJUST_X_AXIS_RANGE);
        menuItem.addListener(SWT.Selection, this);

        // adjust Y axis range
        menuItem = new MenuItem(adjustAxisRangeMenu, SWT.PUSH);
        menuItem.setText(Messages.ADJUST_Y_AXIS_RANGE);
        menuItem.addListener(SWT.Selection, this);

        menuItem = new MenuItem(menu, SWT.SEPARATOR);

        // zoom in menu group
        menuItem = new MenuItem(menu, SWT.CASCADE);
        menuItem.setText(Messages.ZOOMIN_GROUP);
        Menu zoomInMenu = new Menu(menuItem);
        menuItem.setMenu(zoomInMenu);

        // zoom in both axes
        menuItem = new MenuItem(zoomInMenu, SWT.PUSH);
        menuItem.setText(Messages.ZOOMIN);
        menuItem.addListener(SWT.Selection, this);

        // zoom in X axis
        menuItem = new MenuItem(zoomInMenu, SWT.PUSH);
        menuItem.setText(Messages.ZOOMIN_X);
        menuItem.addListener(SWT.Selection, this);

        // zoom in Y axis
        menuItem = new MenuItem(zoomInMenu, SWT.PUSH);
        menuItem.setText(Messages.ZOOMIN_Y);
        menuItem.addListener(SWT.Selection, this);

        // zoom out menu group
        menuItem = new MenuItem(menu, SWT.CASCADE);
        menuItem.setText(Messages.ZOOMOUT_GROUP);
        Menu zoomOutMenu = new Menu(menuItem);
        menuItem.setMenu(zoomOutMenu);

        // zoom out both axes
        menuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
        menuItem.setText(Messages.ZOOMOUT);
        menuItem.addListener(SWT.Selection, this);

        // zoom out X axis
        menuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
        menuItem.setText(Messages.ZOOMOUT_X);
        menuItem.addListener(SWT.Selection, this);

        // zoom out Y axis
        menuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
        menuItem.setText(Messages.ZOOMOUT_Y);
        menuItem.addListener(SWT.Selection, this);

        menuItem = new MenuItem(menu, SWT.SEPARATOR);

        // Save As
        menuItem = new MenuItem(menu, SWT.CASCADE);
        menuItem.setText(Messages.SAVE_AS);
        menuItem.addListener(SWT.Selection, this);

        Menu menuSave = new Menu(menuItem);
		menuItem.setMenu(menuSave);
		
        menuItem = new MenuItem(menu, SWT.SEPARATOR);
        
        // properties
        menuItem = new MenuItem(menu, SWT.PUSH);
        menuItem.setText(Messages.PROPERTIES);
        menuItem.addListener(SWT.Selection, this);
    }

    /*
     * @see PaintListener#paintControl(PaintEvent)
     */
    @Override
	public void paintControl(PaintEvent e) {
        selection.draw(e.gc);
    }

    /*
     * @see Listener#handleEvent(Event)
     */
    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);

        switch (event.type) {
        case SWT.MouseMove:
            handleMouseMoveEvent(event);
            break;
        case SWT.MouseDown:
            handleMouseDownEvent(event);
            break;
        case SWT.MouseUp:
            handleMouseUpEvent(event);
            break;
        case SWT.MouseWheel:
            handleMouseWheel(event);
            break;
        case SWT.KeyDown:
            handleKeyDownEvent(event);
            break;
        case SWT.KeyUp:
            handleKeyUpEvent(event);
            break;
        case SWT.Selection:
            handleSelectionEvent(event);
            break;
        default:
            break;
        }
        redraw();
}

    /*
     * @see Chart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        resources.dispose();
    }

    /**
     * Handles mouse move event.
     * 
     * @param event
     *            the mouse move event
     */
    private void handleMouseMoveEvent(Event event) {
//		setVisibleToolTip(false);
        if(mousePressed){
        	// PAN
        	if(!ctrlPressed){
	    		double xMouseDown = getAxisSet().getXAxis(0).getDataCoordinate(pos_x_mouse);
	            double yMouseDown = getAxisSet().getYAxis(0).getDataCoordinate(pos_y_mouse);
	            double xMouseMove = getAxisSet().getXAxis(0).getDataCoordinate(event.x);
	            double yMouseMove = getAxisSet().getYAxis(0).getDataCoordinate(event.y);
	        	double diff_x = xMouseDown-xMouseMove;
	        	double diff_y = yMouseDown-yMouseMove;
	        	// System.out.println("Diff: x ="+diff_x+" y= "+diff_y);
	        	IAxis yAxis = getAxisSet().getYAxis(0);
	        	for (IAxis axis : getAxes(SWT.HORIZONTAL)) {
	                axis.scrollUp(diff_x);
		        }   	
	        	if(yAxis.isLogScaleEnabled()==false){
		        	for (IAxis axis : getAxes(SWT.VERTICAL)) {
		        		axis.scrollUp(diff_y);
		        	}
	        	}
	        	// Pan if the log scale is enabled
	        	if(yAxis.isLogScaleEnabled()){
//	        		setVisibleToolTip(false);
	        		if(diff_y != 0){
		        		double upper = getAxisSet().getYAxis(0).getRange().upper;
		        		double lower = getAxisSet().getYAxis(0).getRange().lower;
		        		//double upper2 = getAxisSet().getYAxis(0).getDataCoordinate(0);
		        		//System.out.println(upper2-upper);
		        		double new_upper = getAxisSet().getYAxis(0).getDataCoordinate(pos_y_mouse-event.y);
		        		double new_lower = lower * new_upper / upper;
		        		for (IAxis axis : getAxes(SWT.VERTICAL)) {
			        		axis.setRange(new Range(new_lower, new_upper));
			        	}	
	        		}
	        	}
	        }
        	// ZOOM
        	else{
        		if (!selection.isDisposed()) {
                    selection.setEndPoint(event.x, event.y);
                }
	        }
    		pos_x_mouse = event.x;
    		pos_y_mouse = event.y;
        }
        double xPosMouse = getAxisSet().getXAxis(0).getDataCoordinate(event.x);
        double yPosMouse = getAxisSet().getYAxis(0).getDataCoordinate(event.y);
        if(lblMousePosition!=null)
        	lblMousePosition.setText("Mouse position: ("+((double)((int)(xPosMouse*100)))/100 + ", " + ((double)((int)(yPosMouse*100)))/100+")");
        
       ISeries[] series = getSeriesSet().getSeries();
       if(series.length==0){
       		return;
       }       
       double[] vTime = series[0].getXSeries();
       Legend.xNearestMouse = vTime.length-1;
       for (int i = 0; i < vTime.length-1; i++) {
			if(Math.abs((float)(vTime[i]-xPosMouse))<Math.abs((float)(vTime[i+1]-xPosMouse))){
				Legend.xNearestMouse=i;
				break;
			}
		}
       updateLayout();
       redraw();
    }
    
    /**
     * Handles the mouse down event.
     * 
     * @param event
     *            the mouse down event
     */
    private void handleMouseDownEvent(Event event) {
    	if(serieSelected!=null){
    		serieSelected.setLineColor(colorSerieSelected);
        	serieSelected = null;
    	}
    	if(event.button==1){
    		mousePressed = true;
        	pos_x_mouse = event.x;
        	pos_y_mouse = event.y;
        	if(!ctrlPressed)
        		getPlotArea().setCursor(mouseDownEventCursor);
        	else{
        		selection.setStartPoint(event.x, event.y);
                clickedTime = System.currentTimeMillis();
        	}
    	}
    }
    
    public void setMouseDownEventCursor(Cursor curs) {
    	mouseDownEventCursor  = curs;
    }

    /**
     * Handles the mouse up event.
     * 
     * @param event
     *            the mouse up event
     */
    private void handleMouseUpEvent(Event event) {
    	mousePressed = false;
    	if(!ctrlPressed)
    		getPlotArea().setCursor(mouseUpEventCursor);    	
        if (event.button == 1 && System.currentTimeMillis() - clickedTime > 100) {
            for (IAxis axis : getAxisSet().getAxes()) {
                Point range = null;
                if ((getOrientation() == SWT.HORIZONTAL && axis.getDirection() == Direction.X)
                        || (getOrientation() == SWT.VERTICAL && axis
                                .getDirection() == Direction.Y)) {
                    range = selection.getHorizontalRange();
                } else {
                    range = selection.getVerticalRange();
                }

                if (range != null && range.x != range.y) {
                    setRange(range, axis);
                }
            }
        }
        selection.dispose();
        redraw();  	
    }
    
    public void setMouseUpEventCursor(Cursor curs) {
    	mouseUpEventCursor = curs;
    }
    
    /**
     * Handles mouse wheel event.
     * 
     * @param event
     *            the mouse wheel event
     */
    private void handleMouseWheel(Event event) {
        for (IAxis axis : getAxes(SWT.HORIZONTAL)) {
            double coordinate = axis.getDataCoordinate(event.x);
            if (event.count > 0) {
                axis.zoomIn(coordinate);
            } else {
                axis.zoomOut(coordinate);
            }
        }

        for (IAxis axis : getAxes(SWT.VERTICAL)) {
            double coordinate = axis.getDataCoordinate(event.y);
            if (event.count > 0) {
                axis.zoomIn(coordinate);
            } else {
                axis.zoomOut(coordinate);
            }
        }
//        TabTimeSeries.highlight = false;
        redraw();
    }

    /**
     * Handles the key down event.
     * 
     * @param event
     *            the key down event
     */
    private void handleKeyDownEvent(Event event) {
    	if (event.keyCode == SWT.COMMAND || event.keyCode == SWT.CTRL){ // event.keyCode == S.meta_key
        	ctrlPressed = true;
        	getPlotArea().setCursor(null);
            selection = new SelectionRectangle();
            selection.setStartPoint(pos_x_mouse, pos_y_mouse);
    	}
    	else
    		ctrlPressed = false;
        if (event.keyCode == SWT.ARROW_DOWN) {
            /*if (event.stateMask == SWT.CTRL || event.stateMask == SWT.COMMAND) { // event.stateMask == S.meta_key
                getAxisSet().zoomOut();
            } else {*/
                for (IAxis axis : getAxes(SWT.VERTICAL)) {
                    axis.scrollDown();
                }
            //}
            redraw();
        } else if (event.keyCode == SWT.ARROW_UP) {
            /*if (event.stateMask == SWT.CTRL || event.stateMask == SWT.COMMAND) { // event.stateMask == S.meta_key
                getAxisSet().zoomIn();
            } else {*/
                for (IAxis axis : getAxes(SWT.VERTICAL)) {
                    axis.scrollUp();
                }
            //}
            redraw();
        } else if (event.keyCode == SWT.ARROW_LEFT) {
            for (IAxis axis : getAxes(SWT.HORIZONTAL)) {
                axis.scrollDown();
            }
            redraw();
        } else if (event.keyCode == SWT.ARROW_RIGHT) {
            for (IAxis axis : getAxes(SWT.HORIZONTAL)) {
                axis.scrollUp();
            }
            redraw();
        } else if (event.character == '+' || event.keyCode == '+') {
        	if (event.stateMask == SWT.CTRL || event.stateMask == SWT.COMMAND) {
                getAxisSet().zoomIn();
            } 
            redraw();
        } else if (event.character == '-' || event.keyCode == '-') {
        	if (event.stateMask == SWT.CTRL || event.stateMask == SWT.COMMAND) {
                getAxisSet().zoomOut();
            } 
            redraw();
        } 
        else if (event.character == 'L' || event.keyCode == 'L'
        		|| event.character == 'l' || event.keyCode == 'l') {
        	if (event.stateMask == SWT.CTRL || event.stateMask == SWT.COMMAND) {
        		setVisibleToolTip(false);
        		getAxisSet().getYAxis(0).enableLogScale(!getAxisSet().getYAxis(0).isLogScaleEnabled());
            } 
            redraw();
        } 
    }
    
    private void handleKeyUpEvent(Event event) {
        getPlotArea().setCursor(cursorDefault);
    	if(ctrlPressed){
    		selection.dispose();
    		if(mousePressed)
    			getPlotArea().setCursor(mouseDownEventCursor);
    	}
    	ctrlPressed = false;
    }
    

    /**
     * Gets the axes for given orientation.
     * 
     * @param orientation
     *            the orientation
     * @return the axes
     */
    public IAxis[] getAxes(int orientation) {
        IAxis[] axes;
        if (getOrientation() == orientation) {
            axes = getAxisSet().getXAxes();
        } else {
            axes = getAxisSet().getYAxes();
        }
        return axes;
    }

    /**
     * Handles the selection event.
     * 
     * @param event
     *            the event
     */
    private void handleSelectionEvent(Event event) {

        if (!(event.widget instanceof MenuItem)) {
            return;
        }
        MenuItem menuItem = (MenuItem) event.widget;

        if (menuItem.getText().equals(Messages.ADJUST_AXIS_RANGE)) {
            getAxisSet().adjustRange();
        } else if (menuItem.getText().equals(Messages.ADJUST_X_AXIS_RANGE)) {
            for (IAxis axis : getAxisSet().getXAxes()) {
                axis.adjustRange();
            }
        } else if (menuItem.getText().equals(Messages.ADJUST_Y_AXIS_RANGE)) {
            for (IAxis axis : getAxisSet().getYAxes()) {
                axis.adjustRange();
            }
        } else if (menuItem.getText().equals(Messages.ZOOMIN)) {
            getAxisSet().zoomIn();
        } else if (menuItem.getText().equals(Messages.ZOOMIN_X)) {
            for (IAxis axis : getAxisSet().getXAxes()) {
                axis.zoomIn();
            }
        } else if (menuItem.getText().equals(Messages.ZOOMIN_Y)) {
            for (IAxis axis : getAxisSet().getYAxes()) {
                axis.zoomIn();
            }
        } else if (menuItem.getText().equals(Messages.ZOOMOUT)) {
            getAxisSet().zoomOut();
        } else if (menuItem.getText().equals(Messages.ZOOMOUT_X)) {
            for (IAxis axis : getAxisSet().getXAxes()) {
                axis.zoomOut();
            }
        } else if (menuItem.getText().equals(Messages.ZOOMOUT_Y)) {
            for (IAxis axis : getAxisSet().getYAxes()) {
                axis.zoomOut();
            }
        } else if (menuItem.getText().equals(Messages.PROPERTIES)) {
            openPropertiesDialog();
        }
        redraw();
    }

    /**
     * Opens the Save As dialog.
     */
    public void openSaveAsDialog() {
        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
        dialog.setText(Messages.SAVE_AS_DIALOG_TITLE);
        dialog.setFilterExtensions(EXTENSIONS);
        dialog.setFilterNames(FILTERNAMES);
        
        String filename = dialog.open();
        if (filename == null) {
            return;
        }
        int format;
        
        if (filename.endsWith(".png")) {
            format = SWT.IMAGE_PNG;
        } else {
            format = SWT.IMAGE_UNDEFINED;
        }

        if (format != SWT.IMAGE_UNDEFINED) {
            save(filename, format);
        }
    }

    /**
     * Opens the properties dialog.
     */
    public void openPropertiesDialog() {
        PreferenceManager manager = new PreferenceManager();

        final String chartTitle = "Graph setting";
        PreferenceNode chartNode = new PreferenceNode(chartTitle);
        chartNode.setPage(new ChartPage(this, resources, chartTitle));
        manager.addToRoot(chartNode);

        /*final String legendTitle = "Legend";
        PreferenceNode legendNode = new PreferenceNode(legendTitle);
        legendNode.setPage(new LegendPage(this, resources, legendTitle));
        manager.addTo(chartTitle, legendNode);*/

        final String xAxisTitle = getAxisSet().getXAxis(0).getTitle().getText();
        PreferenceNode xAxisNode = new PreferenceNode(xAxisTitle);
        xAxisNode.setPage(new AxisPage(this, resources, Direction.X, xAxisTitle));
        manager.addTo(chartTitle, xAxisNode);

        /*final String gridTitle = "Grid";
        PreferenceNode xGridNode = new PreferenceNode(gridTitle);
        xGridNode.setPage(new GridPage(this, resources, Direction.X, gridTitle));
        manager.addTo(chartTitle + "." + xAxisTitle, xGridNode);

        final String tickTitle = "Tick";
        PreferenceNode xTickNode = new PreferenceNode(tickTitle);
        xTickNode.setPage(new AxisTickPage(this, resources, Direction.X,
                tickTitle));
        manager.addTo(chartTitle + "." + xAxisTitle, xTickNode);*/
        
        final String yAxisTitle = getAxisSet().getYAxis(0).getTitle().getText();
        PreferenceNode yAxisNode = new PreferenceNode(yAxisTitle);
        yAxisNode.setPage(new AxisPage(this, resources, Direction.Y, yAxisTitle));
        manager.addTo(chartTitle, yAxisNode);
        
        /*PreferenceNode yGridNode = new PreferenceNode(gridTitle);
        yGridNode.setPage(new GridPage(this, resources, Direction.Y, gridTitle));
        manager.addTo(chartTitle + "." + yAxisTitle, yGridNode);

        PreferenceNode yTickNode = new PreferenceNode(tickTitle);
        yTickNode.setPage(new AxisTickPage(this, resources, Direction.Y,
                tickTitle));
        manager.addTo(chartTitle + "." + yAxisTitle, yTickNode);
        
        final String seriesTitle = "Timeseries style";
        PreferenceNode plotNode = new PreferenceNode(seriesTitle);
        /*ImageDescriptor im = new ImageDescriptor() {
			
			@Override
			public ImageData getImageData() {
				// TODO Auto-generated method stub
				return AppResources.swtGetImageResource("/img/protein16.png").getImageData();
				//return AppResources.swtGetImageResource("/img/roughmaterial16.png").getImageData();
			}
		};
        PreferenceNode plotNode = new PreferenceNode(seriesTitle, "", im, "");
        */
        /*plotNode.setPage(new SeriesPage(this, resources, seriesTitle));
        manager.addTo(chartTitle, plotNode);

        /*final String labelTitle = "Label";
        PreferenceNode labelNode = new PreferenceNode(labelTitle);
        labelNode.setPage(new SeriesLabelPage(this, resources, labelTitle));
        manager.addTo(chartTitle + "." + seriesTitle, labelNode);*/
        
        PreferenceDialog dialog = new PreferenceDialog(getShell(), manager);
        //dialog.setSelectedNode("Timeseries style");
        dialog.create();
        dialog.getShell().setText("Properties");
        dialog.getTreeViewer().expandAll();
        
        propertiesDialogOpen = true;
        dialog.open();
        propertiesDialogOpen = false;
    }

    /**
     * Sets the axis range.
     * 
     * @param range
     *            the axis range in pixels
     * @param axis
     *            the axis to set range
     */
    private void setRange(Point range, IAxis axis) {
        if (range == null) {
            return;
        }

        double min = axis.getDataCoordinate(range.x);
        double max = axis.getDataCoordinate(range.y);

        axis.setRange(new Range(min, max));
    }

    /**
     * Sets the label that shows the position of the mouse in the graph
     * @param lblMousePosition the label
     */
	public void setLblMousePosition(Label lblMousePosition) {
		this.lblMousePosition = lblMousePosition;
	}
	
	/**
     * Sets the visible of the toolTip
	 * @param visible if true the toolTip is shown, else it is hidden
	 */
	public static void setVisibleToolTip(boolean visible) {
		if(toolTip!=null)
			toolTip.setVisible(visible);
	}
}
