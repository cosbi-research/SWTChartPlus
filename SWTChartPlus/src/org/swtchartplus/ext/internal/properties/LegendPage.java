package org.swtchartplus.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.swtchartplus.Constants;
import org.swtchartplus.ILegend;
import org.swtchartplus.ext.InteractiveChart;

/**
 * The legend property page on properties dialog.
 */
public class LegendPage extends AbstractPage {

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
    public LegendPage(InteractiveChart chart,
            PropertiesResources resources, String title) {
        super(chart, resources, title);
        legend = chart.getLegend();
    }

    /*
     * @see PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);

        addLegendPanel(composite);

        selectValues();
        return composite;
    }

    /**
     * Adds the legend panel.
     * 
     * @param parent
     *            the parent to add the legend panel
     */
    private void addLegendPanel(Composite parent) {

        Composite group = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        group.setLayout(new GridLayout(2, false));

        showLegendButton = createCheckBoxControl(group, "Show legend");
        showLegendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean visible = showLegendButton.getSelection();
                setControlsEnableLegend(visible);
            }
        });

        backgroundLabelLegend = createLabelControl(group, "Background:");
        backgroundButtonLegend = createColorButtonControl(group);

        foregroundLabelLegend = createLabelControl(group, "Foreground:");
        foregroundButtonLegend = createColorButtonControl(group);

        fontSizeLabelLegend = createLabelControl(group, "Font size:");
        fontSizeSpinnerLegend = createSpinnerControl(group, 8, 30);
    }

    /**
     * Selects the values for controls.
     */
    private void selectValues() {
        showLegendButton.setSelection(legend.isVisible());
        setControlsEnableLegend(legend.isVisible());
        backgroundButtonLegend.setColorValue(legend.getBackground().getRGB());
        foregroundButtonLegend.setColorValue(legend.getForeground().getRGB());
        fontSizeSpinnerLegend.setSelection(legend.getFont().getFontData()[0]
                .getHeight());
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

    /*
     * @see AbstractPreferencePage#apply()
     */
    @Override
    public void apply() {
        legend.setVisible(showLegendButton.getSelection());

        Color color = new Color(Display.getDefault(), backgroundButtonLegend
                .getColorValue());
        legend.setBackground(color);
        resources.put(LEGEND_GACKGROUND, color);

        color = new Color(Display.getDefault(), foregroundButtonLegend
                .getColorValue());
        legend.setForeground(color);
        resources.put(LEGEND_FOREGROUND, color);

        FontData fontData = legend.getFont().getFontData()[0];
        Font font = new Font(legend.getFont().getDevice(), fontData.getName(),
                fontSizeSpinnerLegend.getSelection(), fontData.getStyle());
        legend.setFont(font);
        resources.put(LEGEND_FONT, font);
    }

    /*
     * @see PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        showLegendButton.setSelection(true);
        setControlsEnableLegend(true);

        backgroundButtonLegend.setColorValue(new RGB(255, 255, 255));
        foregroundButtonLegend.setColorValue(new RGB(0, 0, 0));
        fontSizeSpinnerLegend.setSelection(Constants.SMALL_FONT_SIZE);

        super.performDefaults();
    }
}
