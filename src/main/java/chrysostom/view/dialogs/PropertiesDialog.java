package chrysostom.view.dialogs;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.entities.Variant;
import chrysostom.view.editor.EditorWindow;
import chrysostom.model.Statistics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PropertiesDialog extends JDialog
{
    private JPanel contentPanel;
    private JTextField nameTextField;
    private JTextField descriptionTextFiled;
    private JTextArea variantTextArea;
    private JTable table;
    private JTextField repeatsNumberTextField;
    private DefaultTableModel tableModel;
    
    private EditorWindow editor;
    
    public PropertiesDialog(EditorWindow editor) {
        super(editor, "Свойства", true);
        this.editor = editor;
        setContentPane(contentPanel);
        setPreferredSize(contentPanel.getPreferredSize());
        setMinimumSize(contentPanel.getMinimumSize());
        initTable();
    }
    
    private void initTable() {
        tableModel = new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        tableModel.addColumn("№");
        tableModel.addColumn("Координата");
        table.getColumnModel().getColumn(0).setMinWidth(30);
        table.getColumnModel().getColumn(0).setMaxWidth(30);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setResizingAllowed(false);
    }
    
    public void showDialog(Anaphora anaphora) {
        nameTextField.setText(anaphora.getName());
        descriptionTextFiled.setText(anaphora.getDescription());
        for (Variant variant : anaphora.getVariants()) {
            variantTextArea.append(variant.getText() + System.lineSeparator());
        }
        
        List<Double> coordinates = Statistics.getCoordinates(anaphora, editor.getText());
        repeatsNumberTextField.setText(String.valueOf(coordinates.size()));
        
        tableModel.setRowCount(coordinates.size());
        
        for (int i = 0; i < coordinates.size(); i++) {
            double value = Statistics.roundTo(coordinates.get(i), 6);
            tableModel.setValueAt(i + 1, i, 0);
            tableModel.setValueAt(value, i, 1);
        }
        
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
