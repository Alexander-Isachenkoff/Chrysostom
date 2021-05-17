package chrysostom.view.chart;

import chrysostom.interfaces.Command;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;

public class SaveImageDialog extends JDialog
{
    private final static String defaultDir = new File("").getAbsolutePath();
    private final static String defaultFileName = "New";
    private final static Resolution[] resolutions = Resolution.getDefaultResolutions();
    
    private final Resolution other = new Resolution()
    {
        @Override
        public String toString() {
            return "Другое";
        }
    };
    
    // region Components
    private JTextField fileNameTextField;
    private JTextField dirTextField;
    private JPanel contentPanel;
    private JSpinner heightSpinner;
    private JSpinner widthSpinner;
    private JButton browseButton;
    private JRadioButton JPGRadioButton;
    private JRadioButton PNGRadioButton;
    private JCheckBox transparencyCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    private JComboBox<Resolution> resolutionsComboBox;
    private ButtonGroup formatButtonGroup;
    private JFileChooser dirChooser;
    // endregion
    private Command saveCommand = () -> {};
    
    public SaveImageDialog() {
        initDialog();
        initComponents();
        setCurrentFile(defaultFileName);
        setCurrentDirectory(defaultDir);
    }
    
    private void initDialog() {
        setTitle("Сохранение");
        setModal(true);
        setContentPane(contentPanel);
        setResizable(false);
    }
    
    private void initComponents() {
        initSpinners();
        initResolutionsComboBox();
        initFormatButtons();
        initButtons();
    }
    
    public void showDialog(Component parent) {
        widthSpinner.setValue(Toolkit.getDefaultToolkit().getScreenSize().width);
        heightSpinner.setValue(Toolkit.getDefaultToolkit().getScreenSize().height);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    public void setCurrentFile(String file) {
        fileNameTextField.setText(file);
    }
    
    public void setCurrentDirectory(String dir) {
        dirTextField.setText(dir);
    }
    
    private void initDirChooser() {
        dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setAcceptAllFileFilterUsed(false);
        dirChooser.setApproveButtonText("Выбрать папку");
        dirChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
    }
    
    /**
     * Инициализирует счеттчики для задания ширины и высоты изображения.
     */
    private void initSpinners() {
        final int minSize = 1;
        final int maxSize = 20000;
        final SpinnerNumberModel widthModel = new SpinnerNumberModel();
        widthModel.setMinimum(minSize);
        widthModel.setMaximum(maxSize);
        final SpinnerNumberModel heightModel = new SpinnerNumberModel();
        heightModel.setMinimum(minSize);
        heightModel.setMaximum(maxSize);
        widthSpinner.setModel(widthModel);
        heightSpinner.setModel(heightModel);
        
        ChangeListener changeListener = e -> {
            int width = (int) widthSpinner.getValue();
            int height = (int) heightSpinner.getValue();
            for (Resolution res : resolutions) {
                if (res.width == width && res.height == height) {
                    resolutionsComboBox.setSelectedItem(res);
                    return;
                }
            }
            other.width = width;
            other.height = height;
            resolutionsComboBox.setSelectedItem(other);
        };
        
        widthSpinner.addChangeListener(changeListener);
        heightSpinner.addChangeListener(changeListener);
    }
    
    /**
     * Инициализирует поле со списком для выбора одного из предустановленных разрешений.
     */
    private void initResolutionsComboBox() {
        ComboBoxModel<Resolution> model = new DefaultComboBoxModel<>(resolutions);
        resolutionsComboBox.setModel(model);
        resolutionsComboBox.addItem(other);
        resolutionsComboBox.setMaximumRowCount(resolutionsComboBox.getItemCount());
        resolutionsComboBox.addItemListener(e -> {
            Resolution res = (Resolution) resolutionsComboBox.getSelectedItem();
            if (res != null) {
                widthSpinner.setValue(res.width);
                heightSpinner.setValue(res.height);
                other.width = res.width;
                other.height = res.height;
            }
        });
    }
    
    /**
     * Регистрирует обработчики событий на переключатели формата: "JPG" и "PNG".
     */
    private void initFormatButtons() {
        ItemListener formatListener = e -> {
            if (PNGRadioButton.isSelected()) {
                transparencyCheckBox.setEnabled(true);
            } else if (JPGRadioButton.isSelected()) {
                transparencyCheckBox.setSelected(false);
                transparencyCheckBox.setEnabled(false);
            }
        };
        PNGRadioButton.addItemListener(formatListener);
        JPGRadioButton.addItemListener(formatListener);
    }
    
    private void initButtons() {
        browseButton.addActionListener(e -> onBrowseClick());
        saveButton.addActionListener(e -> saveCommand.execute());
        cancelButton.addActionListener(e -> dispose());
    }
    
    public void setSaveAction(Command saveCommand) {
        this.saveCommand = saveCommand;
    }
    
    private void onBrowseClick() {
        if (dirChooser == null) {
            initDirChooser();
        }
        dirChooser.setCurrentDirectory(new File(dirTextField.getText()));
        if (dirChooser.showDialog(SaveImageDialog.this, null)
                == JFileChooser.APPROVE_OPTION) {
            dirTextField.setText(dirChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    /**
     * Инициализирует {@code saveParameters} в соответствии с заполнением полей окна.
     */
    public SaveParameters getSaveParameters() {
        int width = (int) widthSpinner.getValue();
        int height = (int) heightSpinner.getValue();
        boolean transparency = transparencyCheckBox.isSelected();
        return new SaveParameters(width, height, getFormat(), transparency, getFullFile());
    }
    
    public String getDirName() {
        return dirTextField.getText();
    }
    
    public String getFileName() {
        return fileNameTextField.getText();
    }
    
    /**
     * @return Расширение в зависимости от выбранного формата (JPG, PNG).
     */
    public String getFormat() {
        return formatButtonGroup.getSelection().getActionCommand();
    }
    
    /**
     * @return Имя файла, составленное из пути для сохранения, указанного в
     * {@code dirTextField}, имени файла, указанного в {@code nameTextField},
     * точки и расширения, получаемого в зависимости от указанного формата.
     */
    private File getFullFile() {
        return new File(dirTextField.getText() + File.separator + fileNameTextField.getText() + "." + getFormat());
    }
}

class Resolution
{
    private final static Resolution[] defaultResolutions = new Resolution[]{
            new Resolution(320, 240),
            new Resolution(400, 300),
            new Resolution(640, 480),
            new Resolution(720, 480),
            new Resolution(800, 600),
            new Resolution(1024, 768),
            new Resolution(1200, 900),
            new Resolution(1280, 720),
            new Resolution(1280, 960),
            new Resolution(1280, 1024),
            new Resolution(1440, 960),
            new Resolution(1600, 1200),
            new Resolution(1920, 1080),
    };
    
    int width;
    int height;
    private final int hRatio;
    private final int vRatio;
    
    Resolution() {
        this(0, 0);
    }
    
    Resolution(int width, int height) {
        this.width = width;
        this.height = height;
        int gcf = GCF(width, height);
        hRatio = width / gcf;
        vRatio = height / gcf;
    }
    
    public static Resolution[] getDefaultResolutions() {
        return defaultResolutions;
    }
    
    private static int GCF(int a, int b) {
        int gcf = 1;
        for (int i = Math.min(a, b); i > 0; i--) {
            if ((a % i == 0) && (b % i == 0)) {
                gcf = i;
                break;
            }
        }
        return gcf;
    }
    
    @Override
    public String toString() {
        return String.format("%d × %d (%d:%d)", width, height, hRatio, vRatio);
    }
}

