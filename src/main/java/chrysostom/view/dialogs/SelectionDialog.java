package chrysostom.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Vector;

public class SelectionDialog<T> extends JDialog
{
    private JPanel contentPanel;
    private JButton okButton;
    private JScrollPane scrollPane;
    private JButton cancelButton;
    private JLabel messageLabel;
    private JList<T> list = new JList<>();
    private T selectedItem;
    
    @SafeVarargs
    public SelectionDialog(String message, String title, T... items) {
        this();
        setMessage(message);
        setTitle(title);
        setItems(items);
    }
    
    public void setCellRenderer(ListCellRenderer<T> renderer) {
        list.setCellRenderer(renderer);
    }
    
    public SelectionDialog() {
        initDialog();
        initList();
        initButtons();
    }
    
    private void initDialog() {
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(contentPanel);
        setMinimumSize(contentPanel.getMinimumSize());
    }
    
    private void initList() {
        scrollPane.setViewportView(list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> okButton.setEnabled(list.getSelectedValue() != null));
        list.setFont(new Font(list.getFont().getName(), Font.PLAIN, 15));
        list.setSelectionBackground(Color.LIGHT_GRAY);
        list.setSelectionForeground(Color.BLACK);
    }
    
    private void initButtons() {
        okButton.addActionListener(e -> {
            selectedItem = list.getSelectedValue();
            dispose();
        });
        
        cancelButton.addActionListener(e -> {
            selectedItem = null;
            dispose();
        });
    }

    public final void setItems(Collection<T> items) {
        list.setListData(new Vector<>(items));
    }
    
    @SafeVarargs
    public final void setItems(T... items) {
        list.setListData(items);
    }
    
    public void showDialog(Component parent) {
        if (!isVisible()) {
            pack();
            setLocationRelativeTo(parent);
            setVisible(true);
        }
    }
    
    public T getSelectedItem() {
        return selectedItem;
    }
    
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}
