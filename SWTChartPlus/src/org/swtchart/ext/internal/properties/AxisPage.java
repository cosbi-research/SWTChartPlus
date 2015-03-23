package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Constants;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Direction;
import org.swtchart.IAxis.Position;
import org.swtchart.IDisposeListener;
import org.swtchart.ISeries;
import org.swtchart.LineStyle;
import org.swtchart.Range;
import org.swtchart.ext.InteractiveChart;
import org.swtchart.internal.series.Series;

/**
 * The axis page on properties dialog.
 */
public class AxisPage extends AbstractSelectorPage {

    /** the key for axis title font */
    private static final String AXIS_TITLE_FONT = "org.swtchart.axis.title.font";

    /** the key for axis title foreground */
    private static final String AXIS_TITLE_FOREGROUND = "org.swtchart.axis.title.foreground";

    /** the axes */
    private IAxis[] axes;

    /** the axis direction */
    private Direction direction;

    /** the show title button */
    protected Button showTitleButton;
    
    /** the show grid button */
    protected Button showGridButton;

    /** the label for title */
    private Label titleLabel;

    /** the title text */
    protected Text titleText;

    /** the label for font size */
    private Label fontSizeLabel;

    /** the spinner for font size */
    protected Spinner fontSizeSpinner;

    /** the label for title color */
    private Label titleColorLabel;

    /** the color selector button */
    protected ColorSelector titleColorButton;

    /** the minimum range text */
    protected Text minRangeText;

    /** the maximum range text */
    protected Text maxRangeText;

    /** the position combo box */
    //protected Combo positionCombo;

    protected Button buttonTop;
    
    protected Button buttonDown;

    /** the log scale button */
    protected Button logScaleButton;

    /** the states indicating id title is visible */
    protected boolean[] titleVisibleStates;

    /** the title texts */
    protected String[] titleTexts;

    /** the title font sizes */
    protected int[] titleFontSizes;

    /** the title colors */
    protected RGB[] titleColors;

    /** the minimum ranges */
    protected double[] minRanges;

    /** the maximum ranges */
    protected double[] maxRanges;

    /** the positions */
    protected Position[] positions;

    /** the states indicating if category is enabled */
    protected boolean[] categoryStates;

    /** the states indicating if log scale is enabled */
    protected boolean[] logScaleStates;
    
    /** the key for grid foreground */
    private static final String GRID_FOREGROUND = "org.swtchart.grid.foreground";

    /** the style combo */
    protected Combo styleCombo;

    /** the foreground button */
    protected ColorSelector foregroundButton;
    
    /** the line styles */
    protected LineStyle[] styles;

    /** the foreground colors */
    protected RGB[] foregroundColors;
    
    /** the key for axis tick font */
    private static final String AXIS_TICK_FONT = "org.swtchart.axistick.font";

    /** the key for axis tick foreground */
    private static final String AXIS_TICK_FOREGROUND = "org.swtchart.axistick.foreground";

    /** the show tick button */
    protected Button showTickButton;

    /** the label for font size */
    private Label fontSizeLabelTick;

    /** the spinner for font size */
    protected Spinner fontSizeSpinnerTick;

    /** the foreground label */
    private Label foregroundLabel;

    /** the foreground button */
    protected ColorSelector foregroundButtonTick;

    /** the states indicating the visibility of axis ticks */
    protected boolean[] visibilityStates;

    /** the font sizes */
    protected int[] fontSizes;

    /** the foreground colors */
    protected RGB[] foregroundColorsTick;
    
    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param resources
     *            the properties resources
     * @param direction
     *            the direction
     * @param title
     *            the title
     */
    public AxisPage(InteractiveChart chart, PropertiesResources resources,
            Direction direction, String title) {
        super(chart, resources, title, "Axes:");
        this.direction = direction;
        if (direction == Direction.X) {
            this.axes = chart.getAxisSet().getXAxes();
        } else if (direction == Direction.Y) {
            this.axes = chart.getAxisSet().getYAxes();
        }
        
        visibilityStates = new boolean[axes.length];
        fontSizes = new int[axes.length];
        titleVisibleStates = new boolean[axes.length];
        titleTexts = new String[axes.length];
        titleFontSizes = new int[axes.length];
        titleColors = new RGB[axes.length];
        minRanges = new double[axes.length];
        maxRanges = new double[axes.length];
        positions = new Position[axes.length];
        if (direction == Direction.X) {
            categoryStates = new boolean[axes.length];
        }
        logScaleStates = new boolean[axes.length];
        
        if (direction == Direction.X) {
            this.axes = chart.getAxisSet().getXAxes();
        } else if (direction == Direction.Y) {
            this.axes = chart.getAxisSet().getYAxes();
        }
        styles = new LineStyle[axes.length];
        foregroundColors = new RGB[axes.length];
        foregroundColorsTick = new RGB[axes.length];
        
        noDefaultAndApplyButton();
        
    }

    /*
     * @see AbstractSelectorPage#getListItems()
     */
    @Override
    protected String[] getListItems() {
        String[] items = new String[axes.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(axes[i].getId());
        }
        return items;
    }

    /*
     * @see AbstractSelectorPage#selectInitialValues()
     */
    @Override
    protected void selectInitialValues() {
        for (int i = 0; i < axes.length; i++) {
            titleVisibleStates[i] = axes[i].getTitle().isVisible();
            titleTexts[i] = axes[i].getTitle().getText();
            titleFontSizes[i] = axes[i].getTitle().getFont().getFontData()[0]
                    .getHeight();
            titleColors[i] = axes[i].getTitle().getForeground().getRGB();
            minRanges[i] = axes[i].getRange().lower;
            maxRanges[i] = axes[i].getRange().upper;
            positions[i] = axes[i].getPosition();
            if (direction == Direction.X) {
                categoryStates[i] = axes[i].isCategoryEnabled();
            }
            logScaleStates[i] = axes[i].isLogScaleEnabled();
            
            styles[i] = axes[i].getGrid().getStyle();
            if(styles[i] != LineStyle.NONE)
            	setControlsGridEnable(true);
            else
            	setControlsGridEnable(false);
            foregroundColors[i] = axes[i].getGrid().getForeground().getRGB();
            
            visibilityStates[i] = axes[i].getTick().isVisible();
            fontSizes[i] = axes[i].getTick().getFont().getFontData()[0].getHeight();
            foregroundColorsTick[i] = axes[i].getTick().getForeground().getRGB();
        }
        
    }

    /*
     * @see AbstractSelectorPage#updateControlSelections()
     */
    @Override
    protected void updateControlSelections() {
        showTitleButton.setSelection(titleVisibleStates[selectedIndex]);
        setControlsEnable(titleVisibleStates[selectedIndex]);
        titleText.setText(titleTexts[selectedIndex]);
        fontSizeSpinner.setSelection(titleFontSizes[selectedIndex]);
        titleColorButton.setColorValue(titleColors[selectedIndex]);

        double minRange = ((double)((int)(minRanges[selectedIndex]*10000)))/10000;
        double maxRange = ((double)((int)(maxRanges[selectedIndex]*10000)))/10000;
        minRangeText.setText(String.valueOf(minRange));
        maxRangeText.setText(String.valueOf(maxRange));
        //positionCombo.setText(String.valueOf(positions[selectedIndex]));
        
        if(positions[selectedIndex]==Position.Primary){
        	if(direction == Direction.X)
        		buttonDown.setSelection(true);
        	else
        		buttonTop.setSelection(true);
        }
        else{
        	if(direction == Direction.X)
        		buttonTop.setSelection(true);
        	else
        		buttonDown.setSelection(true);
        }
        
        if(direction==Direction.Y)
        logScaleButton.setSelection(logScaleStates[selectedIndex]);
        
        if(!String.valueOf(styles[selectedIndex]).equals("NONE"))
        	styleCombo.setText(styles[selectedIndex].label);
        else
        	styleCombo.setText(LineStyle.DOT.label);
        foregroundButton.setColorValue(foregroundColors[selectedIndex]);
        
        showTickButton.setSelection(visibilityStates[selectedIndex]);
        setControlsTickEnable(visibilityStates[selectedIndex]);
        fontSizeSpinnerTick.setSelection(fontSizes[selectedIndex]);
        foregroundButtonTick.setColorValue(foregroundColorsTick[selectedIndex]);
    }

    /*
     * @see AbstractSelectorPage#addRightPanelContents(Composite)
     */
    @Override
    protected void addRightPanelContents(Composite parent) {
    	//parent.setLayout(null);
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(null);
        int isMac = 0;
        if(InteractiveChart.isMac)
        	isMac = 5;
    	
    	//showTitleButton = createCheckBoxControl(composite, "Show title");
    	showTitleButton = new Button(composite, SWT.CHECK);
        showTitleButton.setText("Show title");
        showTitleButton.setBackground(composite.getBackground());
        showTitleButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean visible = showTitleButton.getSelection();
                titleVisibleStates[selectedIndex] = visible;
                setControlsEnable(visible);
                apply();
            }
        });
        showTitleButton.pack();
        showTitleButton.setLocation(10, 95-isMac);
        
        //showGridButton = createCheckBoxControl(composite, "Show grid");
        showGridButton = new Button(composite, SWT.CHECK);
        showGridButton.setText("Show grid");
        showGridButton.setBackground(composite.getBackground());
        showGridButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean enable = showGridButton.getSelection();
                styleCombo.setEnabled(enable);
                foregroundButton.setEnabled(enable);
                if(!enable)
                	styles[selectedIndex] = LineStyle.NONE;
                else{
	                String value = styleCombo.getText();
	                LineStyle selectedStyle = LineStyle.NONE;
	                for (LineStyle style : LineStyle.values()) {
	                    if (style.label.toUpperCase().equals(value.toUpperCase())) {
	                        selectedStyle = style;
	                    }
	                }
	                styles[selectedIndex] = selectedStyle;
	            }
                apply();
            }
        });
        showGridButton.pack();
        showGridButton.setLocation(10, 178-isMac);
        
        //showTickButton = createCheckBoxControl(composite, "Show tick");
        showTickButton = new Button(composite, SWT.CHECK);
        showTickButton.setText("Show tick");
        showTickButton.setBackground(composite.getBackground());
        showTickButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean visible = showTickButton.getSelection();
                visibilityStates[selectedIndex] = visible;
                setControlsTickEnable(visible);
                apply();
            }
        });
        showTickButton.pack();
        showTickButton.setLocation(10, 261-isMac);
    	
        addAxisPanel(composite);
        addTitleGroup(composite);
        addGridGroup(composite);
        addTickGroup(composite);
        
        Button btnDefaults = new Button(parent, SWT.PUSH);
        btnDefaults.setText("Use Defaults for this page");
        btnDefaults.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	performDefaults();
            	apply();
            }
        });
        
        GridData gridData2 = new GridData();
        gridData2.verticalIndent = 20;
        gridData2.horizontalAlignment = GridData.BEGINNING;
        gridData2.verticalAlignment = GridData.END;
        btnDefaults.setLayoutData(gridData2);
        
        minRangeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				minRanges[selectedIndex] = Double.valueOf(minRangeText.getText());
				apply();
			}
		});
        
        maxRangeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				maxRanges[selectedIndex] = Double.valueOf(maxRangeText.getText());
				apply();
			}
		});
        
        titleText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				apply();
			}
		});
        

    }

    /**
     * Adds axis panel.
     * 
     * @param parent
     *            the parent to add the axis panel
     */
    private void addAxisPanel(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        //composite.setLayout(new GridLayout(4, false));

        GridData gridData = new GridData();
        gridData = new GridData();
        gridData.horizontalIndent = -50;
        gridData.widthHint = 45;
        gridData.heightHint = 16;
        Label lblMin = createLabelControl(composite, "Minimum:");
        lblMin.pack();
        lblMin.setLocation(0, 15);
        minRangeText = createTextControl(composite);
        minRangeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                minRanges[selectedIndex] = Double.valueOf(minRangeText.getText());
            }
        });
        minRangeText.setLayoutData(gridData);
        minRangeText.pack();
        minRangeText.setLocation(lblMin.getLocation().x+lblMin.getSize().x+5,lblMin.getLocation().y-
        		(minRangeText.getSize().y-lblMin.getSize().y)/2);

        gridData = new GridData();
        gridData.horizontalIndent = 0;
        Label lblMax = createLabelControl(composite, "Maximum:");
        lblMax.pack();
        lblMax.setLocation(minRangeText.getLocation().x+minRangeText.getSize().x+30, lblMin.getLocation().y);
        maxRangeText = createTextControl(composite);
        /*GridData gridData2 = new GridData();
        gridData2.horizontalIndent = 15;
        maxRangeText.setLayoutData(gridData2);*/
        
        maxRangeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                maxRanges[selectedIndex] = Double.valueOf(maxRangeText.getText());
            }
        });
        //maxRangeText.setLayoutData(new GridData(45,16));
        maxRangeText.pack();
        maxRangeText.setLocation(lblMax.getLocation().x+lblMax.getSize().x+5,lblMax.getLocation().y-
        		(maxRangeText.getSize().y-lblMax.getSize().y)/2);
        
        Composite comp = new Composite(composite, SWT.NONE);
        comp.setLocation(lblMin.getLocation().x,lblMin.getLocation().y+lblMin.getSize().y+25);
        
        gridData = new GridData();
        gridData.verticalIndent = 20;
        
        comp.setLayoutData(gridData);
        Label lblPos = createLabelControl(comp, "Position:");
//        String[] items = new String[] { Position.Primary.name(), Position.Secondary.name() };
        lblPos.pack();
        lblPos.setLocation(0,0);

        /*positionCombo = createComboControl(group, items);
        positionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                positions[selectedIndex] = Position.valueOf(positionCombo.getText());
            }
        });*/
        
        buttonTop = new Button(comp, SWT.RADIO);
        if(direction == Direction.X)
        	buttonTop.setText("Top");
        else
        	buttonTop.setText("Left");
        buttonTop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if(direction == Direction.X)
            		positions[selectedIndex] = Position.Secondary;
            	else
            		positions[selectedIndex] = Position.Primary;
                apply();
            }
        });
        gridData = new GridData();
        gridData.horizontalIndent = 0;
        gridData.verticalIndent = 10;
        buttonTop.setLayoutData(gridData);
        buttonTop.pack();
        buttonTop.setLocation(lblPos.getLocation().x+lblPos.getSize().x+15,lblPos.getLocation().y);
                
        buttonDown = new Button(comp, SWT.RADIO);
        if(direction == Direction.X)
        	buttonDown.setText("Bottom");
        else
        	buttonDown.setText("Right     ");
        buttonDown.setLayoutData(gridData);
        buttonDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if(direction == Direction.X)
            		positions[selectedIndex] = Position.Primary;
            	else
            		positions[selectedIndex] = Position.Secondary;
                apply();
            }
        });
        gridData = new GridData();
        gridData.horizontalIndent = -9;
        gridData.verticalIndent = 10;
        buttonDown.setLayoutData(gridData);
        buttonDown.pack();
        buttonDown.setLocation(buttonTop.getLocation().x+57,buttonTop.getLocation().y);
        
        if(direction==Direction.Y){
	        //logScaleButton = createCheckBoxControl(comp, "Enable log scale");
	        logScaleButton = new Button(comp, SWT.CHECK);
	        logScaleButton.setText("Enable log scale");
	        final String toolTipLogScale = "Toggle log scale (Ctrl+L)";
	        logScaleButton.setToolTipText(toolTipLogScale);
	        logScaleButton.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	if(logScaleButton.getToolTipText().equals(toolTipLogScale))
	        	        logScaleButton.setToolTipText("Toggle linear scale (CTRL+L)");
	            	else
	        	        logScaleButton.setToolTipText(toolTipLogScale);	            		
	                logScaleStates[selectedIndex] = logScaleButton.getSelection();
	                checkValuesForLogScale(logScaleButton.getSelection());
	                apply();
	            }
	        });
	        /*gridData = new GridData();
	        gridData.horizontalIndent = 10;
	        gridData.verticalIndent = 10;
	        logScaleButton.setLayoutData(gridData);*/
	        logScaleButton.pack();
	        logScaleButton.setLocation(buttonDown.getLocation().x+84,buttonDown.getLocation().y);
        }
        comp.pack();
        composite.pack();
        composite.setLocation(0, 0);
    }
    
    private void checkValuesForLogScale(boolean enable){
    	if(direction==Direction.Y){
    	if(enable){
	    	for (ISeries series : chart.getSeriesSet().getSeries()) {
	            double[] v = ((Series)series).getYSeries();
	            for (int i = 0; i < v.length; i++) {
					if(v[i]<=0)
						v[i]=0.000001;
				}
	            ((Series)series).setYSeries(v);
	    	}
    	}
    	else{
        	if(chart.getSeriesSet().getSeries().length>0)    			 
	    		for (ISeries series : chart.getSeriesSet().getSeries()) {
	    			double[] v = ((Series)series).getYSeries();
		            if(v!=null)
		            	((Series)series).setYSeries(v);
		    }
    	}
    	}
    	if(direction==Direction.X){
    		if(enable){
    	    	for (ISeries series : chart.getSeriesSet().getSeries()) {
    	            double[] v = ((Series)series).getXSeries();
    	            
    	            for (int i = 0; i < v.length; i++) {
    					v[i]=v[i]+1;
    				}
    	            
    	            v = new double[((Series)series).getXSeries().length-1];
    	            for (int i = 0; i < v.length; i++) {
    	            	v[i]=((Series)series).getXSeries()[i+1];
    				}
    	            ((Series)series).setXSeries(v);
    	            
    	            for (int i = 0; i < v.length; i++) {
    	            	v[i]=((Series)series).getYSeries()[i+1];
    				}
    	            ((Series)series).setYSeries(v);
    	            
    	    	}
        	}
        	else{
            	if(chart.getSeriesSet().getSeries().length>0)    
    	    		for (ISeries series : chart.getSeriesSet().getSeries()) {
    		            double[] v = ((Series)series).getXSeries();
    		            if(v!=null)
    		            	((Series)series).setXSeries(v);
    		    }
        	}
    	}
    }

    /**
     * Adds title group.
     * 
     * @param parent
     *            the parent to add the title group
     */
    private void addTitleGroup(Composite parent) {

        Group group = createGroupControl(parent, "", false);
        
        Composite comp = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        comp.setLayout(gridLayout);
        /*
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        comp.setLayoutData(gridData);*/

        

        titleLabel = createLabelControl(comp, "Text:");
        titleText = createTextControl(comp);
        titleText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                titleTexts[selectedIndex] = titleText.getText();
            }
        });

        titleColorLabel = createLabelControl(comp, "Color:");
        titleColorButton = createColorButtonControl(comp);
        titleColorButton.addListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                titleColors[selectedIndex] = titleColorButton.getColorValue();
                apply();
            }
        });
   
        fontSizeLabel = createLabelControl(comp, "Font size:");
        fontSizeSpinner = createSpinnerControl(comp, 8, 30);
        fontSizeSpinner.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                titleFontSizes[selectedIndex] = fontSizeSpinner.getSelection();
                apply();
            }
        });
        
        group.pack();
        group.setLocation(0, 95);
        group.setBackground(parent.getBackground());
        group.setBackgroundMode(SWT.INHERIT_DEFAULT);
        
    }
    
    private void addGridGroup(Composite parent) {
    	Group group = createGroupControl(parent, "", false);
        /*GridData gridData = new GridData();
        gridData.horizontalSpan = 1;
        group.setLayoutData(gridData);
        group.setLayout(new GridLayout(4, false));*/
    	
    	 Composite comp = new Composite(group, SWT.NONE);
         GridLayout gridLayout = new GridLayout();
         gridLayout.numColumns = 4;
         comp.setLayout(gridLayout);  

        createLabelControl(comp, "Line style:");
        LineStyle[] values = LineStyle.values();
        String[] labels = new String[values.length-1];
        for (int i = 1; i < values.length; i++) {
            labels[i-1] = values[i].label;
        }
        styleCombo = createComboControl(comp, labels);
        styleCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String value = styleCombo.getText();
                LineStyle selectedStyle = LineStyle.NONE;
                for (LineStyle style : LineStyle.values()) {
                    if (style.label.equals(value)) {
                        selectedStyle = style;
                    }
                }
                styles[selectedIndex] = selectedStyle;
                apply();
            }
        });

        createLabelControl(comp, "Color:");
        foregroundButton = createColorButtonControl(comp);
        foregroundButton.addListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                foregroundColors[selectedIndex] = foregroundButton.getColorValue();
                apply();
            }
        });
    
        group.pack();
        group.setLocation(0, 178);
        group.setBackground(parent.getBackground());
        group.setBackgroundMode(SWT.INHERIT_DEFAULT);
        
        //System.out.println(group.getSize());
    }

    /**
     * Create the tick panel.
     * 
     * @param parent
     *            the parent to add the tick panel
     */
    private void addTickGroup(Composite parent) {
    	Group group = createGroupControl(parent, "", false);
        /*GridData gridData = new GridData();
        gridData.horizontalSpan = 1;
        group.setLayoutData(gridData);
        group.setLayout(new GridLayout(2, false));*/
    	
    	Composite comp = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        comp.setLayout(gridLayout);      

        foregroundLabel = createLabelControl(comp, "Color:");
        foregroundButtonTick = createColorButtonControl(comp);
        foregroundButtonTick.addListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                foregroundColorsTick[selectedIndex] = foregroundButtonTick.getColorValue();
                apply();
            }
        });
    
        fontSizeLabelTick = createLabelControl(comp, "Font size:");
        fontSizeSpinnerTick = createSpinnerControl(comp, 8, 30);
        fontSizeSpinnerTick.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fontSizes[selectedIndex] = fontSizeSpinnerTick.getSelection();
                apply();
            }
        });
        
        group.pack();
        group.setLocation(0, 261);
        group.setBackground(parent.getBackground());
        group.setBackgroundMode(SWT.INHERIT_DEFAULT);
        
    }
    
    protected void setControlsGridEnable(boolean enabled){
    	showGridButton.setSelection(enabled);
    	styleCombo.setEnabled(enabled);
        foregroundButton.setEnabled(enabled);
    }
        
    /**
     * Sets the enable state of controls.
     * 
     * @param enabled
     *            true if controls are enabled
     */
    protected void setControlsEnable(boolean enabled) {
        titleLabel.setEnabled(enabled);
        titleText.setEnabled(enabled);
        fontSizeLabel.setEnabled(enabled);
        fontSizeSpinner.setEnabled(enabled);
        titleColorLabel.setEnabled(enabled);
        titleColorButton.setEnabled(enabled);
    }

    protected void setControlsTickEnable(boolean enabled) {
        fontSizeLabelTick.setEnabled(enabled);
        fontSizeSpinnerTick.setEnabled(enabled);
        foregroundLabel.setEnabled(enabled);
        foregroundButtonTick.setEnabled(enabled);
    }
    
    
    /*
     * @see AbstractPreferencePage#apply()
     */
    @Override
    public void apply() {
        for (int i = 0; i < axes.length; i++) {
            axes[i].getTitle().setVisible(titleVisibleStates[i]);
            axes[i].getTitle().setText(titleTexts[i]);

            FontData fontData = axes[i].getTitle().getFont().getFontData()[0];
            fontData.setHeight(titleFontSizes[i]);
            Font font = new Font(Display.getDefault(), fontData);
            axes[i].getTitle().setFont(font);
            final String fontKey = AXIS_TITLE_FONT + axes[i].getDirection()
                    + axes[i].getId();
            if (resources.getFont(fontKey) == null) {
                axes[i].addDisposeListener(new IDisposeListener() {
                    public void disposed(Event e) {
                        resources.removeFont(fontKey);
                    }
                });
            }
            resources.put(fontKey, font);

            Color color = new Color(Display.getDefault(), titleColors[i]);
            axes[i].getTitle().setForeground(color);
            final String colorKey = AXIS_TITLE_FOREGROUND
                    + axes[i].getDirection() + axes[i].getId();
            if (resources.getColor(colorKey) == null) {
                axes[i].addDisposeListener(new IDisposeListener() {
                    public void disposed(Event e) {
                        resources.removeColor(colorKey);
                    }
                });
            }
            resources.put(colorKey, color);

            axes[i].setRange(new Range(minRanges[i], maxRanges[i]));
            axes[i].setPosition(positions[i]);
            try {
                axes[i].enableLogScale(logScaleStates[i]);
                /*if(logScaleStates[i]==true){
                	minRangeText.setText(String.valueOf(axes[i].getRange().lower));
                	minRanges[i] = Double.valueOf(minRangeText.getText());
                }*/
            } catch (IllegalStateException e) {
                axes[i].enableLogScale(false);
                logScaleButton.setSelection(false);
            }
            if (direction == Direction.X) {
                axes[i].enableCategory(categoryStates[i]);
            }
            axes[i].setRange(new Range(minRanges[i], maxRanges[i]));
            
            axes[i].getGrid().setStyle(styles[i]);
            Color color2 = new Color(Display.getDefault(), foregroundColors[i]);
            axes[i].getGrid().setForeground(color2);
            resources.put(GRID_FOREGROUND + axes[i].getDirection()
                    + axes[i].getId(), color2);
        }
        // Tick
        for (int i = 0; i < axes.length; i++) {
            axes[i].getTick().setVisible(visibilityStates[i]);

            FontData fontData = axes[i].getTick().getFont().getFontData()[0];
            fontData.setHeight(fontSizes[i]);
            Font font = new Font(Display.getDefault(), fontData);
            axes[i].getTick().setFont(font);
            final String fontKey = AXIS_TICK_FONT + axes[i].getDirection()
                    + axes[i].getId();
            if (resources.getFont(fontKey) == null) {
                axes[i].addDisposeListener(new IDisposeListener() {
                    public void disposed(Event e) {
                        resources.removeFont(fontKey);
                    }
                });
            }
            resources.put(fontKey, font);

            Color color = new Color(Display.getDefault(), foregroundColorsTick[i]);
            axes[i].getTick().setForeground(color);
            final String colorKey = AXIS_TICK_FOREGROUND
                    + axes[i].getDirection() + axes[i].getId();
            if (resources.getColor(colorKey) == null) {
                axes[i].addDisposeListener(new IDisposeListener() {
                    public void disposed(Event e) {
                        resources.removeColor(colorKey);
                    }
                });
            }
            resources.put(colorKey, color);
        }

        chart.redraw();
    }

    /*
     * @see PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        titleVisibleStates[selectedIndex] = true;
        if (direction == Direction.X) {
            titleTexts[selectedIndex] = "X Axis";
            categoryStates[selectedIndex] = false;
        } else if (direction == Direction.Y) {
            titleTexts[selectedIndex] = "Y Axis";
        }
        positions[selectedIndex] = Position.Primary;
        buttonDown.setSelection(false);
        
        titleFontSizes[selectedIndex] = Constants.LARGE_FONT_SIZE;
        titleColors[selectedIndex] = Display.getDefault().getSystemColor(SWT.COLOR_BLACK).getRGB();

        minRanges[selectedIndex] = 0.0;
        maxRanges[selectedIndex] = 1.0;
        logScaleStates[selectedIndex] = false;
        
        styles[selectedIndex] = LineStyle.DOT;
        foregroundColors[selectedIndex] = Display.getDefault().getSystemColor(SWT.COLOR_GRAY).getRGB();
        
        visibilityStates[selectedIndex] = true;
        fontSizes[selectedIndex] = Constants.SMALL_FONT_SIZE;
        foregroundColorsTick[selectedIndex] = Display.getDefault().getSystemColor(SWT.COLOR_BLACK).getRGB();
        
        showGridButton.setSelection(true);
        boolean enable = showGridButton.getSelection();
        styleCombo.setEnabled(enable);
        foregroundButton.setEnabled(enable);
        
        updateControlSelections();
        super.performDefaults();
    }
}
