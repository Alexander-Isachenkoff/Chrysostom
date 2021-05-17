package chrysostom.view.components;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.AnaphoraDictionary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

public class AnaphoraTable extends JTable implements Observer
{
    private final AnaphoraDictionary dictionary;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    
    public AnaphoraTable(AnaphoraDictionary dictionary) {
        this.dictionary = dictionary;
        dictionary.addObserver(this);
        setModel(tableModel);
        
        tableModel.addColumn("");
        tableModel.addColumn("Цвет");
        tableModel.addColumn("Название");
        
        setShowGrid(false);
        setSelectionBackground(Color.LIGHT_GRAY);
        setSelectionForeground(Color.BLACK);
        setFont(getFont().deriveFont(15f));
        setRowHeight(20);
        setFillsViewportHeight(true);
        setTableHeader(null);
        setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
        getColumnModel().getColumn(0).setMaxWidth(20);
        getColumnModel().getColumn(1).setMaxWidth(25);
        getColumnModel().setColumnMargin(0);
        setFocusable(false);
    }
    
    private static Icon createColoredCircle(int size, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0, true));
        g.fillRect(0, 0, size - 1, size - 1);
        g.setColor(color);
        g.fillOval(0, 0, size - 1, size - 1);
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, size - 1, size - 1);
        return new ImageIcon(image);
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return Boolean.class;
            case 1:
                return Icon.class;
            case 2:
                return String.class;
            default:
                return super.getColumnClass(column);
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        tableModel.setRowCount(0);
        for (Anaphora anaphora : dictionary.getAllAnaphora()) {
            tableModel.addRow(new Object[]{ true, createColoredCircle(15, anaphora.getColor()), anaphora.getName() });
        }
    }
    
    public Anaphora getSelectedValue() {
        return dictionary.getByName(getValueAt(getSelectedRow(), 2).toString());
    }
}
