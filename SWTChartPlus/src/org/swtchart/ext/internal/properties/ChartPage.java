package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Constants;
import org.swtchart.ILegend;
import org.swtchart.ITitle;
import org.swtchart.ext.InteractiveChart;
import org.swtchart.internal.ChartTitle;

//import ui.swt.AppMain;

/**
 * The chart property page on properties dialog.
 */
public class ChartPage extends AbstractPage {

    /** the key for plot area background */
    private static final String PLOT_AREA_BACKGROUND = "org.swtchart.plotarea.background";

    /** the key for chart background */
    private static final String CHART_BACKGROUND = "org.swtchart.chart.background";

    /** the key for chart background */
    private static final String TITLE_FOREGROUND = "org.swtchart.chart.title.foreground";

    /** the key for title font */
    private static final String TITLE_FONT = "org.swtchart.chart.title.font";

    /** the color selector for background color in plot area */
    private ColorSelector backgroundInPlotAreaButton;

    /** the color selector for background */
    private ColorSelector backgroundButton;

    /** the show title button */
    protected Button showTitleButton;

    /** the title label */
    private Label titleLabel;

    /** the title text */
    private Text titleText;

    /** the font size label */
    private Label fontSizeLabel;

    /** the font size spinner */
    private Spinner fontSizeSpinner;

    /** the title color label */
    private Label titleColorLabel;

    /** the title color button */
    private ColorSelector titleColorButton;
    
    // LEGEND
    
    /** the key for legend font */
    private static final String LEGEND_FONT = "org.swtchart.legend.font";

    /** the key for legend foreground */
    private static final String LEGEND_FOREGROUND = "org.swtchart.legend.foreground";

    /** the key for legend background */
    private static final String LEGEND_GACKGROUND = "org.swtchart.legend.background";

    /** the show legend button */
    protected Button showLegendButton;

    /** the background label */
    private Label backgroundLabelLegend;

    /** the background button */
    private ColorSelector backgroundButtonLegend;

    /** the foreground label */
    private Label foregroundLabelLegend;

    /** the foreground button */
    private ColorSelector foregroundButtonLegend;

    /** the font size label */
    private Label fontSizeLabelLegend;

    /** the font size spinner */
    private Spinner fontSizeSpinnerLegend;

    /** the legend */
    private ILegend legend;
        

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param resources
     *            the properties resources
     * @param title
     *            the title
     */
    public ChartPage(InteractiveChart chart,
            PropertiesResources resources, String title) {
        super(chart, resources, title);
        legend = chart.getLegend();
        
        noDefaultAndApplyButton();
        
    }

    /*
     * @see PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        /*GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);*/
        
        int isMac = 0;
        if(InteractiveChart.isMac)
        	isMac = 5;

        addChartPanel(composite);
        
        showTitleButton = new Button(composite, SWT.CHECK);
        showTitleButton.setText("Show title");
        showTitleButton.setBackground(composite.getBackground());
        showTitleButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setTitleControlsEnable(showTitleButton.getSelection());
                apply();
            }
        });

        /*GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        showTitleButton.setLayoutData(gridData);*/

        showTitleButton.pack();
        //showTitleButton.setLocation(composite.getChildren()[0].getLocation().x+10, composite.getChildren()[0].getLocation().y+20);
        showTitleButton.setLocation(10, 95-isMac);
                
        //showLegendButton = createCheckBoxControl(composite, "Show legend");
        showLegendButton = new Button(composite, SWT.CHECK);
        showLegendButton .setText("Show legend");
        showLegendButton.setBackground(composite.getBackground());
        showLegendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean visible = showLegendButton.getSelection();
                setControlsEnableLegend(visible);
                apply();
            }
        });
        
        showLegendButton.pack();
        showLegendButton.setLocation(10, 180-isMac);
        
        addTitleGroup(composite);
        addLegendPanel(composite);
        
        Button btnDefaults = new Button(composite, SWT.PUSH);
        btnDefaults.setText("Use Defaults for this page");
        btnDefaults.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	performDefaults();
            	apply();
            }
        });
        
        btnDefaults.pack();
        btnDefaults.setLocation(0, 270);
        
        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.BEGINNING;
        gridData2.verticalAlignment = GridData.BEGINNING;
        gridData2.verticalIndent = 20;
        btnDefaults.setLayoutData(gridData2);
        
        selectValues();
        
        titleText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				apply();
			}
		});
        
        return composite;
    }

    /**
     * Adds the chart panel.
     * 
     * @param parent
     *            the parent to add the chart panel
     */
    private void addChartPanel(Composite parent) {
        Group group = createGroupControl(parent, "Graph:", true);
        //Composite panel = new Composite(parent, SWT.NONE);
        //panel.setLayout(new GridLayout(2, false));
        
        /*
        Composite comp = new Composite(panel, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        comp.setLayout(gridLayout);*/
        //c.setLayout(new GridLayout());
        //c.setLayout(new GridLayout(4, false));
        //panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //panel.setLayoutData(new GridData(GridData.BEGINNING));
        
        Composite comp = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        comp.setLayout(gridLayout);
        
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        comp.setLayoutData(gridData);
        
        createLabelControl(comp, "Background in plot area:");
        backgroundInPlotAreaButton = createColorButtonControl(comp);
        backgroundInPlotAreaButton.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				apply();
			}
		});
        
        GridData gridData2 = new GridData();
        gridData2.horizontalIndent = 36;
        comp.setLayoutData(gridData);
        createLabelControl(comp, "Background:").setLayoutData(gridData2);
        backgroundButton = createColorButtonControl(comp);
        backgroundButton.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				apply();
			}
		});
        
        group.pack();
        group.setLocation(0, 10);
        group.setBackground(parent.getBackground());
        group.setBackgroundMode(SWT.INHERIT_DEFAULT);
        //System.out.println(group.getSize());
    }

    /**
     * Adds the title group.
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

        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        comp.setLayoutData(gridData);

        /*
        showTitleButton = createCheckBoxControl(comp, "Show title");
        showTitleButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setTitleControlsEnable(showTitleButton.getSelection());
                apply();
            }
        });*/

        titleLabel = createLabelControl(comp, "Text:");
        titleText = createTextControl(comp);
        /*
        GridData gridData = new GridData();
        gridData.widthHint = 70;
        gridData.heightHint = 40;
        titleText.setLayoutData(gridData);*/
        //titleText.setLayoutData(new GridData(arg0, arg1)
        
        titleColorLabel = createLabelControl(comp, "Color:");
        titleColorButton = createColorButtonControl(comp);
        titleColorButton.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				apply();
			}
		});
        
        fontSizeLabel = createLabelControl(comp, "Font size:");
        fontSizeSpinner = createSpinnerControl(comp, 8, 30);
        fontSizeSpinner.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				apply();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
        

        group.pack();
        
        gridData = new GridData();
        gridData.widthHint = titleText.getSize().x+38;
        //gridData.heightHint = titleText.getSize().y;
        titleText.setLayoutData(gridData);
        
        group.pack();
        group.setLocation(0, 95);
        group.setBackground(parent.getBackground());
        group.setBackgroundMode(SWT.INHERIT_DEFAULT);
    }

    /**
     * Adds the legend panel.
     * 
     * @param parent
     *            the parent to add the legend panel
     */
    private void addLegendPanel(Composite parent) {
        Group group = createGroupControl(parent, "", false);

        /*Composite group = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        group.setLayout(new GridLayout(2, false));*/
        
        Composite comp = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        comp.setLayout(gridLayout);
        
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        comp.setLayoutData(gridData);

        /*showLegendButton = createCheckBoxControl(comp, "Show legend");
        showLegendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean visible = showLegendButton.getSelection();
                setControlsEnableLegend(visible);
            }
        });*/
        
        foregroundLabelLegend = createLabelControl(comp, "Foreground:");
        foregroundButtonLegend = createColorButtonControl(comp);
        foregroundButtonLegend.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				apply();
			}
		});
        
        backgroundLabelLegend = createLabelControl(comp, "Background:");
        backgroundButtonLegend = createColorButtonControl(comp);
        backgroundButtonLegend.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				apply();
			}
		});
        
        fontSizeLabelLegend = createLabelControl(comp, "Font size:");
        fontSizeSpinnerLegend = createSpinnerControl(comp, 8, 30);
        fontSizeSpinnerLegend.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				apply();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
        
        group.pack();
        group.setLocation(0, 180);
        group.setBackground(parent.getBackground());
        group.setBackgroundMode(SWT.INHERIT_DEFAULT);
    }
    
    /**
     * Sets the enable state of controls.
     * 
     * @param enabled
     *            true if controls are enabled
     */
    protected void setControlsEnableLegend(boolean enabled) {
        backgroundLabelLegend.setEnabled(enabled);
        backgroundButtonLegend.setEnabled(enabled);
        foregroundLabelLegend.setEnabled(enabled);
        foregroundButtonLegend.setEnabled(enabled);
        fontSizeLabelLegend.setEnabled(enabled);
        fontSizeSpinnerLegend.setEnabled(enabled);
    }
    
    /**
     * Selects the values for controls.
     */
    private void selectValues() {
        backgroundInPlotAreaButton.setColorValue(chart.getBackgroundInPlotArea().getRGB());
        backgroundButton.setColorValue(chart.getBackground().getRGB());
        
        ITitle title = chart.getTitle();
        showTitleButton.setSelection(title.isVisible());
        setTitleControlsEnable(title.isVisible());
        titleText.setText(title.getText());
        fontSizeSpinner.setSelection(title.getFont().getFontData()[0]
                .getHeight());
        titleColorButton.setColorValue(title.getForeground().getRGB());
        
        // LEGEND
        showLegendButton.setSelection(legend.isVisible());
        setControlsEnableLegend(legend.isVisible());
        backgroundButtonLegend.setColorValue(legend.getBackground().getRGB());
        foregroundButtonLegend.setColorValue(legend.getForeground().getRGB());
        fontSizeSpinnerLegend.setSelection(legend.getFont().getFontData()[0]
                .getHeight());
    }

    /**
     * Sets the enable state of title controls.
     * 
     * @param enabled
     *            true if title controls are enabled
     */
    protected void setTitleControlsEnable(boolean enabled) {
        titleLabel.setEnabled(enabled);
        titleText.setEnabled(enabled);
        fontSizeLabel.setEnabled(enabled);
        fontSizeSpinner.setEnabled(enabled);
        titleColorLabel.setEnabled(enabled);
        titleColorButton.setEnabled(enabled);
    }

    /*
     * @see AbstractPreferencePage#apply()
     */
    @Override
    public void apply() {
        Color color = new Color(Display.getDefault(),
                backgroundInPlotAreaButton.getColorValue());
        chart.setBackgroundInPlotArea(color);
        resources.put(PLOT_AREA_BACKGROUND, color);

        color = new Color(Display.getDefault(), backgroundButton
                .getColorValue());
        chart.setBackground(color);
        resources.put(CHART_BACKGROUND, color);

        ITitle title = chart.getTitle();
        title.setVisible(showTitleButton.getSelection());
        title.setText(titleText.getText());
      
        FontData fontData = title.getFont().getFontData()[0];
        fontData.setHeight(fontSizeSpinner.getSelection());
        Font font = new Font(Display.getDefault(), fontData);
        title.setFont(font);
        resources.put(TITLE_FONT, font);

        color = new Color(Display.getDefault(), titleColorButton.getColorValue());
        title.setForeground(color);
        resources.put(TITLE_FOREGROUND, color);
        
        legend.setVisible(showLegendButton.getSelection());

        Color color2 = new Color(Display.getDefault(), backgroundButtonLegend
                .getColorValue());
        legend.setBackground(color2);
        resources.put(LEGEND_GACKGROUND, color2);

        color2 = new Color(Display.getDefault(), foregroundButtonLegend
                .getColorValue());
        legend.setForeground(color2);
        resources.put(LEGEND_FOREGROUND, color2);

        FontData fontData2 = legend.getFont().getFontData()[0];
        Font font2 = new Font(legend.getFont().getDevice(), fontData2.getName(),
                fontSizeSpinnerLegend.getSelection(), fontData2.getStyle());
        legend.setFont(font2);
        resources.put(LEGEND_FONT, font2);
    
        chart.redraw();
    }

    /*
     * @see PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        backgroundInPlotAreaButton.setColorValue(new RGB(255, 255, 255));
        backgroundButton.setColorValue(Display.getDefault().getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND).getRGB());
        
        showTitleButton.setSelection(true);
        setTitleControlsEnable(true);
        titleText.setText(ChartTitle.DEFAULT_TEXT);
        fontSizeSpinner.setSelection(Constants.LARGE_FONT_SIZE);
        titleColorButton.setColorValue(new RGB(0, 0, 0));
        
        showLegendButton.setSelection(true);
        setControlsEnableLegend(true);

        backgroundButtonLegend.setColorValue(new RGB(255, 255, 255));
        foregroundButtonLegend.setColorValue(new RGB(0, 0, 0));
        fontSizeSpinnerLegend.setSelection(Constants.SMALL_FONT_SIZE);

        super.performDefaults();
    }
}
