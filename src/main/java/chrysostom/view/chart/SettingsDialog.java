package chrysostom.view.chart;

import chrysostom.model.chart.ChartSettings;
import chrysostom.model.chart.Marker;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog
{
    //region Компоненты
    private JPanel contentPanel;
    private JRadioButton inlineRadioButton;
    private JRadioButton blockRadioButton;
    private JComboBox<Marker> markersComboBox;
    private JButton cancelButton;
    private JSpinner markerSizeSpinner;
    private JButton okButton;
    private JSpinner fontSizeSpinner;
    private JCheckBox arrowsCheckBox;
    private JCheckBox specialPositionsCheckBox;
    //endregion
    
    private ChartSettings newSettings;
    
    private SettingsDialog(Frame owner) {
        super(owner, "Параметры", true);
        setContentPane(contentPanel);
        setResizable(false);
        initComponents();
        initButtons();
    }
    
    public static ChartSettings showDialog(Frame owner, ChartSettings initialSettings) {
        SettingsDialog dialog = new SettingsDialog(owner);
        dialog.markerSizeSpinner.setValue(initialSettings.markerSize);
        dialog.markersComboBox.setSelectedItem(initialSettings.marker);
        dialog.fontSizeSpinner.setValue(initialSettings.fontSize);
        if (initialSettings.view == ChartSettings.BLOCK) {
            dialog.blockRadioButton.setSelected(true);
        } else {
            dialog.inlineRadioButton.setSelected(true);
        }
        dialog.arrowsCheckBox.setSelected(initialSettings.arrows);
        dialog.specialPositionsCheckBox.setSelected(initialSettings.specialPositions);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        return dialog.newSettings;
    }
    
    private void initComponents() {
        initMarkerSpinner();
        initMarkerIconsList();
        initFontSpinner();
    }
    
    private void initMarkerIconsList() {
        final int imgSize = 12;
        final int canvasSize = 16;
        for (Marker marker : Marker.defaultMarkers()) {
            markersComboBox.addItem(marker);
        }
        markersComboBox.setRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setIcon(((Marker) value).getIcon(imgSize, canvasSize));
                return label;
            }
        });
    }
    
    private void initMarkerSpinner() {
        final int minSize = 1;
        final int maxSize = 20;
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(minSize);
        model.setMaximum(maxSize);
        markerSizeSpinner.setModel(model);
    }
    
    private void initFontSpinner() {
        final int minSize = 6;
        final int maxSize = 20;
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(minSize);
        model.setMaximum(maxSize);
        fontSizeSpinner.setModel(model);
    }
    
    private void initButtons() {
        okButton.addActionListener(e -> {
            Marker marker = (Marker) markersComboBox.getSelectedItem();
            int markerSize = (int) markerSizeSpinner.getValue();
            int fontSize = (int) fontSizeSpinner.getValue();
            boolean arrows = arrowsCheckBox.isSelected();
            boolean specialPositions = specialPositionsCheckBox.isSelected();
            int view = ChartSettings.INLINE;
            if (inlineRadioButton.isSelected()) {
                view = ChartSettings.INLINE;
            } else if (blockRadioButton.isSelected()) {
                view = ChartSettings.BLOCK;
            }
            newSettings = new ChartSettings(marker, markerSize, view, arrows, fontSize, specialPositions);
            dispose();
        });
        
        cancelButton.addActionListener(e -> {
            newSettings = null;
            dispose();
        });
    }
}
