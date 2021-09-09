package chrysostom.view.dialogs;

import chrysostom.model.AnaphoraDictionary;
import chrysostom.model.entities.Anaphora;
import chrysostom.model.entities.Variant;
import chrysostom.util.Colors;
import chrysostom.util.SimpleDocumentListener;
import chrysostom.view.components.EditableList;
import dialog.ColorDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

abstract class AnaphoraPresenter extends JDialog
{
    private Color selectedColor;
    //region Компоненты
    private JPanel contentPane;
    private JTextField nameTextField;
    private JTextField descriptionTextField;
    private final EditableList variantsList = new EditableList();
    private JButton colorButton;
    private JButton okButton;
    private JButton cancelButton;
    private JScrollPane scrollPanel;
    private JButton deleteButton;
    private JCheckBox wordsOnlyCheckBox;
    private JButton addButton;
    //endregion
    
    AnaphoraPresenter(Frame owner, String title) {
        super(owner, title);
        initDialog();
        initComponents();
        initColorButton();
        initListeners();
    }
    
    private static Icon createSquare(int size, Color color) {
        BufferedImage square = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = square.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, size - 1, size - 1);
        g.setColor(Color.WHITE);
        g.drawRect(1, 1, size - 3, size - 3);
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, size - 1, size - 1);
        return new ImageIcon(square);
    }
    
    private static boolean isEmpty(String string) {
        if (string.equals("")) return true;
        for (char c : string.toCharArray()) {
            if (c != ' ') return false;
        }
        return true;
    }
    
    private void initDialog() {
        setContentPane(contentPane);
        setMinimumSize(contentPane.getMinimumSize());
    }
    
    private void initComponents() {
        scrollPanel.setViewportView(variantsList);
    }
    
    private void initColorButton() {
        colorButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        colorButton.setHorizontalTextPosition(SwingConstants.CENTER);
        colorButton.setContentAreaFilled(false);
        int margin = 10;
        colorButton.setMargin(new Insets(margin, margin, margin, margin));
    }
    
    private void initListeners() {
        addButton.addActionListener(e ->
                variantsList.editCellAt(variantsList.getRowCount() - 1, 0));
        deleteButton.addActionListener(e -> variantsList.removeSelectedElements());
        okButton.addActionListener(this::okButtonAction);
        cancelButton.addActionListener(e -> dispose());
        initFillingListeners();
        initColorButtonListeners();
    }
    
    private void initFillingListeners() {
        SimpleDocumentListener checkListener = e -> okButton.setEnabled(checkFilling());
        nameTextField.getDocument().addDocumentListener(checkListener);
        descriptionTextField.getDocument().addDocumentListener(checkListener);
    }
    
    /**
     * Определяет, заполнены ли поля названия и описания в окне.
     *
     * @return true, если поля названия и описания заполнены, иначе false.
     */
    private boolean checkFilling() {
        return (!isEmpty(nameTextField.getText()) && !isEmpty(descriptionTextField.getText()));
    }
    
    abstract void okButtonAction(ActionEvent e);
    
    private void initColorButtonListeners() {
        colorButton.addActionListener(e -> {
            Color color = ColorDialog.showDialog("Выбор цвета", selectedColor);
            if (color != null) {
                setSelectedColor(color);
            }
        });
        
        colorButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e) {
                colorButton.setContentAreaFilled(true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                colorButton.setContentAreaFilled(false);
            }
        });
    }
    
    public void showDialog() {
        if (!isVisible()) {
            pack();
            clear();
            setLocationRelativeTo(getOwner());
            setVisible(true);
        }
    }
    
    public void showDialog(Anaphora initialAnaphora) {
        if (!isVisible()) {
            showDialog();
            fill(initialAnaphora);
        }
    }
    
    private void clear() {
        nameTextField.setText("");
        descriptionTextField.setText("");
        variantsList.removeAllElements();
        setSelectedColor(Color.WHITE);
    }
    
    private void fill(Anaphora anaphora) {
        nameTextField.setText(anaphora.getName());
        descriptionTextField.setText(anaphora.getDescription());
        variantsList.removeAllElements();
        for (Variant variant : anaphora.getVariants()) {
            variantsList.addElement(variant.getText());
        }
        setSelectedColor(Color.decode(anaphora.getHexColor()));
        wordsOnlyCheckBox.setSelected(anaphora.isInnerWordsExcluded());
    }
    
    private void setSelectedColor(Color color) {
        selectedColor = color;
        colorButton.setIcon(createSquare(30, color));
    }
    
    Anaphora createAnaphora() {
        String name = nameTextField.getText();
        String description = descriptionTextField.getText();
        boolean innerWordsExcluded = wordsOnlyCheckBox.isSelected();
        String[] stringVariants = variantsList.getElements();
        List<Variant> variants = new ArrayList<>();
        
        Anaphora anaphora = new Anaphora();
        
        for (String stringVariant : stringVariants) {
            Variant variant = new Variant(stringVariant);
	        variants.add(variant);
	        variant.setAnaphora(anaphora);
        }
        
        anaphora.setName(name);
        anaphora.setDescription(description);
        anaphora.setVariants(variants);
        anaphora.setHexColor(Colors.toHex(selectedColor));
        anaphora.setInnerWordsExcluded(innerWordsExcluded);
        
        return anaphora;
    }
}